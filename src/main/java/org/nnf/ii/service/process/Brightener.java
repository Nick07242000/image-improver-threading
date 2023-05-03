package org.nnf.ii.service.process;

import lombok.Builder;
import org.apache.log4j.Logger;
import org.nnf.ii.model.Container;
import org.nnf.ii.model.Image;
import org.nnf.ii.service.semaphore.Queue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static java.lang.ThreadLocal.withInitial;
import static org.nnf.ii.model.enums.Resolution.*;
import static org.nnf.ii.model.enums.Status.READY;
import static org.nnf.ii.util.Util.delay;
import static org.nnf.ii.util.Util.waitFor;

@Builder
public class Brightener implements Runnable {
    private final Logger log = Logger.getLogger(Brightener.class);
    private final Container container;
    private final Queue initialQueue;
    private final CountDownLatch waiter;
    private final ThreadLocal<List<Image>> accessed = withInitial(ArrayList::new);

    @Override
    public void run() {
        log.debug(format("Brightener Running - %s", currentThread().getName()));
        waitFor(waiter);
        brightenCollection();
        accessed.remove();
        log.debug(format("Brightener Finished - %s", currentThread().getName()));
    }

    private void brightenCollection() {
        while (accessed.get().size() < container.getSize()) {
            Image image = getImage();

            accessed.get().add(image);

            log.debug(format("Brightening image %s in %s", image.getUrl(), currentThread().getName()));

            brighten(image);
            improve(image);

            delay(200);

            image.setStatus(READY);
        }
    }

    private Image getImage() {
        Optional<Image> image = initialQueue.getImage();
        while (!image.isPresent() || accessed.get().contains(image.get())) {
            image.ifPresent(value -> value.setStatus(READY));
            image = initialQueue.getImage();
        }
        return image.get();
    }

    private void brighten(Image image) {
        switch (image.getResolution()) {
            case LOW:
                image.setResolution(MEDIUM);
                break;
            case MEDIUM:
                image.setResolution(HIGH);
                break;
            case HIGH:
                image.setResolution(ULTRA_HIGH);
                break;
            default:
                break;
        }
    }

    private void improve(Image image) {
        image.setImprovements(image.getImprovements() + 1);
    }
}

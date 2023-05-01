package org.nnf.ii.service.process;

import lombok.Builder;
import lombok.Getter;
import org.apache.log4j.Logger;
import org.nnf.ii.model.Container;
import org.nnf.ii.model.Image;
import org.nnf.ii.service.semaphore.impl.InitialQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static java.lang.ThreadLocal.withInitial;
import static org.nnf.ii.model.enums.Resolution.*;
import static org.nnf.ii.model.enums.Status.READY;
import static org.nnf.ii.util.Util.waitFor;

@Getter
@Builder
public class Brightener implements Runnable {
    private final Logger log = Logger.getLogger(Brightener.class);
    private final Container container;
    private final InitialQueue initialQueue;
    private final CountDownLatch waiter;
    private final ThreadLocal<List<Image>> accessed = withInitial(ArrayList::new);

    @Override
    public void run() {
        log.debug(format("Brightener Running - %s", currentThread().getName()));
        waitFor(waiter);
        brightenCollection(accessed.get());
        accessed.remove();
        log.debug(format("Brightener Finished - %s", currentThread().getName()));
    }

    private void brightenCollection(List<Image> accessed) {
        while (accessed.size() < container.getSize()) {
            Image image = getImage(accessed);

            accessed.add(image);

            log.debug(format("Brightening image %s in %s", image.getUrl(), currentThread().getName()));

            brighten(image);
            improve(image);

            //delay(200);

            image.setStatus(READY);
        }
    }

    private Image getImage(List<Image> accessed) {
        Optional<Image> image = initialQueue.getImage(container);
        while (!image.isPresent() || accessed.contains(image.get())) {
            image.ifPresent(value -> value.setStatus(READY));
            image = initialQueue.getImage(container);
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

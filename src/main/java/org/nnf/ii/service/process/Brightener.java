package org.nnf.ii.service.process;

import lombok.Builder;
import lombok.Getter;
import lombok.Synchronized;
import org.apache.log4j.Logger;
import org.nnf.ii.model.Container;
import org.nnf.ii.model.Image;
import org.nnf.ii.service.semaphore.Queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static org.nnf.ii.model.enums.Resolution.*;
import static org.nnf.ii.model.enums.Status.READY;
import static org.nnf.ii.util.Util.delay;
import static org.nnf.ii.util.Util.waitFor;

@Getter
@Builder
public class Brightener implements Runnable {
    private final Logger log = Logger.getLogger(Brightener.class);
    private final Container container;
    private final Queue queue;
    private final CountDownLatch waiter;

    @Override
    public void run() {
        log.debug(format("Brightener Running - %s", currentThread().getName()));
        waitFor(waiter);
        brightenCollection();
        log.debug(format("Brightener Finished - %s", currentThread().getName()));
    }

    private void brightenCollection() {
        List<Image> accessed = new ArrayList<>();

        while (accessed.size() < container.getSize()) {
            Image image = getImage(accessed);

            accessed.add(image);

            log.debug(format("Brightening image %s in %s", image.getUrl(), currentThread().getName()));

            delay(100);

            brighten(image);
            improve(image);
            image.setStatus(READY);
        }
    }

    @Synchronized
    private Image getImage(List<Image> accessed) {
        Image image = queue.getImage(container);
        while (accessed.contains(image)) {
            image.setStatus(READY);
            image = queue.getImage(container);
        };
        return image;
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
        }
    }

    private void improve(Image image) {
        image.setImprovements(image.getImprovements() + 1);
    }
}

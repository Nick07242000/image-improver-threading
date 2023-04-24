package org.nnf.ii.service.process;

import lombok.Builder;
import lombok.Getter;
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
        List<Image> accessed = new ArrayList<>();
        log.debug(format("Brightener Running - %s", currentThread().getName()));
        waitFor(waiter);
        brightenCollection(accessed);
        log.debug(format("Brightener Finished - %s", currentThread().getName()));
    }

    private void brightenCollection(List<Image> accessed) {
        while (accessed.size() < container.getSize()) {
            /*if(!container.hasImproperImprovImages()){
                break;
            }*/
            Image image = getImage(accessed);
            accessed.add(image);

            log.debug(format("Brightening image %s in %s", image.getUrl(), currentThread().getName()));

            brighten(image);
            log.debug(format("Improvement before %s of %s", image.getImprovements(), currentThread().getName()));

            improve(image);
            log.debug(format("Accessed size %s in %s", accessed.size(), currentThread().getName()));

            delay(200);
            log.debug(format("Improvement after %s of %s", image.getImprovements(), currentThread().getName()));
            image.setStatus(READY);
        }
    }

    private Image getImage(List<Image> accessed) {
        Image image = queue.getImage(container);
        while (accessed.contains(image)) {
            image.setStatus(READY);
            image = queue.getImage(container);
        }
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
            default:
                break;
        }
    }

    private void improve(Image image) {
            image.setImprovements(image.getImprovements() + 1);
    }
}

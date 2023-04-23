package org.nnf.ii.service.process;

import lombok.Builder;
import lombok.Synchronized;
import org.apache.log4j.Logger;
import org.nnf.ii.model.Container;
import org.nnf.ii.model.Image;
import org.nnf.ii.service.semaphore.Queue;

import java.util.concurrent.CountDownLatch;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static org.nnf.ii.model.enums.Size.MEDIUM;
import static org.nnf.ii.model.enums.Status.*;
import static org.nnf.ii.util.Util.delay;
import static org.nnf.ii.util.Util.waitFor;

@Builder
public class Resizer implements Runnable {
    private final Logger log = Logger.getLogger(Resizer.class);
    private final Container container;
    private final Queue queue;
    private final CountDownLatch waiter;

    @Override
    public void run() {
        log.debug(format("Resizer Running - %s", currentThread().getName()));
        waitFor(waiter);
        resizeCollection();
        log.debug(format("Resizer Finished - %s", currentThread().getName()));
    }

    private void resizeCollection() {
        while (container.hasImproperSizedImages()) {
            Image image = getImage();

            log.debug(format("Resizing image %s in %s", image.getUrl(), currentThread().getName()));

            delay(200);

            resize(image);

            image.setStatus(READY);
        }

    }

    private void resize(Image image) {
        if(image.getSize() != MEDIUM) image.setSize(MEDIUM);
    }

    @Synchronized
    private Image getImage() {
        Image image = queue.getImage(container);
        while (image.getImprovements() < 3) {
            image.setStatus(READY);
            image = queue.getImage(container);
        }
        return image;
    }

}

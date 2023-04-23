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
import static org.nnf.ii.util.Util.waitFor;
import static org.nnf.ii.util.Util.delay;

@Builder
public class Persister implements Runnable {
    private final Logger log = Logger.getLogger(Persister.class);
    private final Container source;
    private final Container destination;
    private final Queue queue;
    private final CountDownLatch waiter;

    @Override
    public void run() {
        log.debug(format("Persister Running - %s", currentThread().getName()));
        waitFor(waiter);
        fillDestination();
        log.debug(format("Persister Finished - %s", currentThread().getName()));
    }

    private void fillDestination() {
        while (destination.hasCapacity()) {
            if (source.hasReadyImages()) {
                Image image = getImage();

                log.debug(format("Persisting - %s image in %s", image.getUrl(), currentThread().getName()));

                delay(300);

                destination.add(image);
                queue.deleteImage(source, image);
                image.setStatus(FINISHED);
            }
        }

    }

    @Synchronized
    private Image getImage() {
        Image image = queue.getImage(source);
        while (image.getSize() != MEDIUM) {
            image.setStatus(READY);
            image = queue.getImage(source);
        }
        return image;
    }

}

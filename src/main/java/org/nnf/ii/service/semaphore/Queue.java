package org.nnf.ii.service.semaphore;

import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.nnf.ii.model.Container;
import org.nnf.ii.model.Image;

import java.util.concurrent.Semaphore;

import static org.nnf.ii.model.enums.Status.IN_PROGRESS;
import static org.nnf.ii.model.enums.Status.READY;
import static java.lang.Thread.currentThread;

@NoArgsConstructor
public class Queue {
    private final Logger log = Logger.getLogger(Queue.class);
    private final Semaphore semaphore = new Semaphore(1);

    public Image getImage(Container container) {
        waitForAccess();
        Image image;
        do {
           image = container.getRandom();
        } while (image.getStatus() != READY);
        image.setStatus(IN_PROGRESS);
        semaphore.release();
        return image;
    }

    public void deleteImage(Container container, Image image) {
        waitForAccess();
        container.delete(image);
        semaphore.release();
    }

    public boolean hasReadyImages(Container container) {
        waitForAccess();
        boolean b = container.hasReadyImages();
        semaphore.release();
        return b;
    }

    public boolean hasImproperSizedImages(Container container) {
        waitForAccess();
        boolean b = container.hasImproperSizedImages();
        semaphore.release();
        return b;
    }

    private void waitForAccess() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            currentThread().interrupt();
        }
    }

}

package org.nnf.ii.service.semaphore;

import org.apache.log4j.Logger;
import org.nnf.ii.model.Container;
import org.nnf.ii.model.Image;

import java.util.Optional;
import java.util.concurrent.Semaphore;

import static java.lang.Thread.currentThread;
import static org.nnf.ii.model.enums.Status.IN_PROGRESS;
import static org.nnf.ii.model.enums.Status.READY;

public abstract class Queue {
    protected final Logger log = Logger.getLogger(Queue.class);
    protected final Semaphore semaphore = new Semaphore(1);

    protected void waitForAccess() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            currentThread().interrupt();
        }
    }

    public Optional<Image> getImage(Container container) {
        waitForAccess();
        if (!container.hasReadyImages()) {
            semaphore.release();
            return Optional.empty();
        }
        Image image;
        do {
            image = container.getRandom();
        } while (image.getStatus() != READY);
        image.setStatus(IN_PROGRESS);
        semaphore.release();
        return Optional.of(image);
    }

    public boolean addImage(Container container, Image image) {
        waitForAccess();
        boolean b = false;
        if (!container.isPresent(image)) {
            container.add(image);
            b = true;
        }
        semaphore.release();
        return b;
    }

    public void deleteImage(Container container, Image image) {
        waitForAccess();
        container.delete(image);
        semaphore.release();
    }

    public boolean hasCapacity(Container container) {
        waitForAccess();
        boolean b = container.hasCapacity();
        semaphore.release();
        return b;
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
}

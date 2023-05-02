package org.nnf.ii.service.semaphore;

import lombok.Builder;
import org.apache.log4j.Logger;
import org.nnf.ii.model.Container;
import org.nnf.ii.model.Image;

import java.util.Optional;
import java.util.concurrent.Semaphore;

import static java.lang.Thread.currentThread;
import static org.nnf.ii.model.enums.Status.IN_PROGRESS;
import static org.nnf.ii.model.enums.Status.READY;

@Builder
public class Queue {
    private final Logger log = Logger.getLogger(Queue.class);
    private final Semaphore semaphore = new Semaphore(1);
    private final Container container;

    protected void waitForAccess() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            currentThread().interrupt();
        }
    }

    public Optional<Image> getImage() {
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

    public boolean addImage(Image image) {
        waitForAccess();
        boolean b = container.add(image);
        semaphore.release();
        return b;
    }

    public void deleteImage(Image image) {
        waitForAccess();
        container.delete(image);
        semaphore.release();
    }

    public boolean hasCapacity() {
        waitForAccess();
        boolean b = container.hasCapacity();
        semaphore.release();
        return b;
    }

    public boolean hasReadyImages() {
        waitForAccess();
        boolean b = container.hasReadyImages();
        semaphore.release();
        return b;
    }
}

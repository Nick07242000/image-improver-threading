package org.nnf.ii.service.semaphore;

import lombok.NoArgsConstructor;
import org.nnf.ii.model.Container;
import org.nnf.ii.model.Image;

import java.util.concurrent.Semaphore;

@NoArgsConstructor
public class Queue {
    private final Semaphore semaphore = new Semaphore(1);

    public Image getImage(Container container) {
        waitForAccess();
        waitForData(container);
        Image image = container.getRandom();
        semaphore.release();
        return image;
    }

    public void deleteImage(Container container, Image image) {
        waitForAccess();
        container.delete(image);
    }

    private void waitForAccess() {
        boolean locked = true;
        while (locked) {
            locked = !semaphore.tryAcquire();
        }
    }

    private void waitForData(Container container) {
        boolean isEmpty = container.isEmpty();
        while (isEmpty) {
            isEmpty = container.isEmpty();
        }
    }

}

package org.nnf.ii.service.semaphore;

import lombok.NoArgsConstructor;
import org.nnf.ii.model.Container;
import org.nnf.ii.model.Image;

import java.util.concurrent.Semaphore;

@NoArgsConstructor
public class Queue {
    private final Semaphore semaphore = new Semaphore(1);

    public Image getImage(Container container) {
        if (!semaphore.tryAcquire()) {
            return this.getImage(container);
        } else {
            semaphore.release();
            return container.getRandom();
        }
    }

}

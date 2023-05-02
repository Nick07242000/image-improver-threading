package org.nnf.ii.service.process;

import lombok.Builder;
import org.apache.log4j.Logger;
import org.nnf.ii.model.Container;
import org.nnf.ii.model.Image;
import org.nnf.ii.service.semaphore.Queue;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static java.lang.ThreadLocal.withInitial;
import static org.nnf.ii.model.enums.Size.MEDIUM;
import static org.nnf.ii.model.enums.Status.READY;
import static org.nnf.ii.util.Util.delay;
import static org.nnf.ii.util.Util.waitFor;

@Builder
public class Persister implements Runnable {
    private final Logger log = Logger.getLogger(Persister.class);
    private final Container source;
    private final Container destination;
    private final Queue initialQueue;
    private final Queue finalQueue;
    private final CountDownLatch waiter;
    private final ThreadLocal<Integer> persisted = withInitial(() -> 0);

    @Override
    public void run() {
        log.debug(format("Persister Running - %s", currentThread().getName()));
        waitFor(waiter);
        fillDestination();
        log.debug(format("Persister Finished - %s", currentThread().getName()));
        log.info(format("%s moved %d images", currentThread().getName(), persisted.get()));
        persisted.remove();
    }

    private void fillDestination() {
        while (finalQueue.hasCapacity()) {
            if (initialQueue.hasReadyImages()) {
                Optional<Image> optional = initialQueue.getImage();

                if (!optional.isPresent()) continue;
                
                Image image = optional.get();
                
                if (isNotPersistable(image)) continue;

                if (finalQueue.addImage(image)) {
                    log.debug(format("Persisting - %s image in %s", image.getUrl(), currentThread().getName()));
                    initialQueue.deleteImage(image);
                    persisted.set(persisted.get() + 1);
                }

                delay(300);

                image.setStatus(READY);
            }
        }

    }
    
    private isNotPersistable(Image image) {
        return image.getSize() != MEDIUM;
    }

}

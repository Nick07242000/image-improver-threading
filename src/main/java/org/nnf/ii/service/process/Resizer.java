package org.nnf.ii.service.process;

import lombok.Builder;
import org.apache.log4j.Logger;
import org.nnf.ii.model.Container;
import org.nnf.ii.model.Image;
import org.nnf.ii.service.semaphore.impl.InitialQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static java.lang.ThreadLocal.withInitial;
import static org.nnf.ii.model.enums.Size.MEDIUM;
import static org.nnf.ii.model.enums.Status.READY;
import static org.nnf.ii.util.Util.waitFor;

@Builder
public class Resizer implements Runnable {
    private final Logger log = Logger.getLogger(Resizer.class);
    private final Container container;
    private final InitialQueue initialQueue;
    private final CountDownLatch waiter;
    private final ThreadLocal<Integer> threadResized = withInitial(() -> 0);
    private AtomicInteger totalResized;

    @Override
    public void run() {
        log.debug(format("Resizer Running - %s", currentThread().getName()));
        waitFor(waiter);
        resizeCollection();
        log.debug(format("Resizer Finished - %s", currentThread().getName()));
        log.info(format("%s resized %d images", currentThread().getName(), threadResized.get()));
        threadResized.remove();
    }

    private void resizeCollection() {
        while (totalResized.get() < container.getSize()) {
            Image image = getImage();

            resize(image);

            //delay(300);

            image.setStatus(READY);
        }
    }

    private void resize(Image image) {
        if(image.getSize() != MEDIUM) {
            log.debug(format("Resizing image %s in %s", image.getUrl(), currentThread().getName()));
            image.setSize(MEDIUM);
            totalResized.incrementAndGet();
            threadResized.set(threadResized.get() + 1);
        }
    }

    private Image getImage() {
        Optional<Image> image = initialQueue.getImage(container);
        while (!image.isPresent() || image.get().getImprovements() < 3) {
            image.ifPresent(value -> value.setStatus(READY));
            image = initialQueue.getImage(container);
        }
        return image.get();
    }

}

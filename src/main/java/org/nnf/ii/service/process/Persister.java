package org.nnf.ii.service.process;

import lombok.Builder;
import org.apache.log4j.Logger;
import org.nnf.ii.model.Container;
import org.nnf.ii.model.Image;
import org.nnf.ii.service.semaphore.Queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static org.nnf.ii.model.enums.Size.MEDIUM;
import static org.nnf.ii.model.enums.Status.READY;
import static org.nnf.ii.util.Util.delay;
import static org.nnf.ii.util.Util.waitFor;

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
        List<String> urls = new ArrayList<>();
        waitFor(waiter);
        fillDestination(urls);
        log.debug(format("Persister Finished - %s", currentThread().getName()));
        log.info(format("%s moved %d images", currentThread().getName(), urls.size()));
    }

    private void fillDestination(List<String> urls) {
        while (destination.hasCapacity()) {
            if (queue.hasReadyImages(source)) {
                Image image = getImage();

                urls.add(image.getUrl());

                log.debug(format("Persisting - %s image in %s", image.getUrl(), currentThread().getName()));

                destination.add(image);
                queue.deleteImage(source, image);

                delay(300);

                image.setStatus(READY);
            }
        }

    }

    private Image getImage() {
        Image image = queue.getImage(source);
        while (image.getSize() != MEDIUM) {
            image.setStatus(READY);
            image = queue.getImage(source);
        }
        return image;
    }

}

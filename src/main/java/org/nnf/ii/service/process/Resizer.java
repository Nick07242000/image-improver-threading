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
public class Resizer implements Runnable {
    private final Logger log = Logger.getLogger(Resizer.class);
    private final Container container;
    private final Queue queue;
    private final CountDownLatch waiter;

    @Override
    public void run() {
        log.debug(format("Resizer Running - %s", currentThread().getName()));
        List<String> urls = new ArrayList<>();
        waitFor(waiter);
        resizeCollection(urls);
        log.debug(format("Resizer Finished - %s", currentThread().getName()));
        log.info(format("%s resized %d images", currentThread().getName(), urls.size()));
    }

    private void resizeCollection(List<String> urls) {
        while (queue.hasImproperSizedImages(container)) {
            Image image = getImage();

            resize(image,urls);

            delay(300);

            image.setStatus(READY);
        }

    }

    private void resize(Image image, List<String> urls) {
        if(image.getSize() != MEDIUM) {
            log.debug(format("Resizing image %s in %s", image.getUrl(), currentThread().getName()));
            image.setSize(MEDIUM);
            urls.add(image.getUrl());
        }
    }

    private Image getImage() {
        Image image = queue.getImage(container);
        while (image.getImprovements() < 3) {
            image.setStatus(READY);
            image = queue.getImage(container);
        }
        return image;
    }

}

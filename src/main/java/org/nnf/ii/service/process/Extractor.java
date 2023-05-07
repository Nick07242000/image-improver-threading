package org.nnf.ii.service.process;

import lombok.Builder;
import org.apache.log4j.Logger;
import org.nnf.ii.model.Container;
import org.nnf.ii.model.Image;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static org.nnf.ii.util.Util.delay;
import static org.nnf.ii.util.Util.getRandomNumber;

@Builder
public class Extractor implements Runnable {
    private final Logger log = Logger.getLogger(Extractor.class);
    private final List<Image> source;
    private final Container destination;
    private final CountDownLatch unlocker;
    private final Set<Image> extracted;
    private AtomicInteger extractedAmount;

    @Override
    public void run() {
        log.debug(format("Extractor Running - %s",currentThread().getName()));
        while (extractedAmount.get() < destination.getSize()) {
            addToDestination(extractFromSource());
            delay(30);
            unlock();
        }
        log.debug(format("Extractor Finished - %s",currentThread().getName()));
        log.debug(format("Container has %d images",destination.getAmountPresent()));
    }

    private void unlock() {
        unlocker.countDown();
    }

    private Image extractFromSource() {
        int n = getRandomNumber(0, source.size());
        log.debug(format("Extracting image %d from source",n));
        return source.get(n);
    }

    private void addToDestination(Image image) {
        log.debug("Attempting to add image to initial container");
        if (extracted.contains(image) || extractedAmount.get() == destination.getSize()) return;
        if (destination.add(image)) {
            extractedAmount.getAndIncrement();
            extracted.add(image);
        }
    }
}

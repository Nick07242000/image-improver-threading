package org.nnf.ii.service;

import lombok.Builder;
import org.apache.log4j.Logger;
import org.nnf.ii.model.Container;
import org.nnf.ii.model.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static java.lang.ThreadLocal.withInitial;
import static org.nnf.ii.model.enums.Status.READY;
import static org.nnf.ii.util.Util.delay;
import static org.nnf.ii.util.Util.waitFor;

@Builder
public class Brightener implements Runnable {
    private final Logger log = Logger.getLogger(Brightener.class);
    private final Container container;
    private final CountDownLatch waiter;
    private final ThreadLocal<List<Image>> accessed = withInitial(ArrayList::new);

    @Override
    public void run() {
        log.debug(format("Brightener Running - %s", currentThread().getName()));
        waitFor(waiter);
        brightenCollection();
        accessed.remove();
        log.debug(format("Brightener Finished - %s", currentThread().getName()));
    }

    private void brightenCollection() {
        while (accessed.get().size() < container.getSize()) {
            Optional<Image> optional = container.getRandom();

            if (!optional.isPresent() || accessed.get().contains(optional.get())) {
                optional.ifPresent(image -> image.setStatus(READY));
                continue;
            }

            Image image = optional.get();

            accessed.get().add(image);

            log.debug(format("Brightening image %s in %s", image.getUrl(), currentThread().getName()));

            image.brighten();
            image.improve();

            delay(200);

            image.setStatus(READY);
        }
    }
}

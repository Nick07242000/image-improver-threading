package org.nnf.ii.service.process;

import lombok.Builder;
import lombok.Getter;
import lombok.Synchronized;
import org.apache.log4j.Logger;
import org.nnf.ii.model.Container;
import org.nnf.ii.model.Image;
import org.nnf.ii.service.semaphore.Queue;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static org.nnf.ii.model.enums.Resolution.*;
import static org.nnf.ii.model.enums.Status.IN_PROGRESS;
import static org.nnf.ii.model.enums.Status.READY;
import static org.nnf.ii.util.Util.delay;

@Getter
@Builder
public class Brightener implements Runnable {
    private final Logger log = Logger.getLogger(Brightener.class);
    private final Container container;
    private final Queue queue;

    @Override
    public void run() {
        log.debug(format("Brightener Running - %s",currentThread().getName()));
        brightenCollection();
        log.debug(format("Brightener Finished - %s",currentThread().getName()));
    }

    private void brightenCollection() {
        List<Image> accessed = new ArrayList<>();

        while (accessed.size() < container.getSize()) {
            Image image = getImage(accessed);
            accessed.add(image);

            log.debug(format("Brightening image %s in %s", image.getUrl(), currentThread().getName()));

            delay(100);

            brighten(image);
            improve(image);
            image.setStatus(READY);
        }
    }

    @Synchronized
    private Image getImage(List<Image> accessed) {
        Image image;
        do {
            image = queue.getImage(container);
        }
        while (image.getStatus()!=READY || accessed.contains(image));
        image.setStatus(IN_PROGRESS);
        return image;
    }

    private void brighten(Image image) {
        switch (image.getResolution()) {
            case LOW: image.setResolution(MEDIUM);
            case MEDIUM: image.setResolution(HIGH);
            case HIGH: image.setResolution(ULTRA_HIGH);
        }
    }

    private void improve(Image image) {
        image.setImprovements(image.getImprovements() + 1);
    }
}

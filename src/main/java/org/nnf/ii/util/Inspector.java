package org.nnf.ii.util;

import lombok.Builder;
import org.apache.log4j.Logger;
import org.nnf.ii.model.Container;

import static java.lang.String.format;
import static org.nnf.ii.model.enums.Size.MEDIUM;
import static org.nnf.ii.util.Util.delay;

@Builder
public class Inspector implements Runnable {
    private final Logger log = Logger.getLogger(Inspector.class);
    private Container source;
    private Container destination;

    @Override
    public void run() {
        while (destination.hasCapacity()) {
            log();
            delay(500);
        }
        log();
        log.info("Finished!");
    }

    private void log() {
        log.info(format("There are %d images in the initial container", source.getAmountPresent()));
        log.info(format("%d images have been improved", getImprovedImages()));
        log.info(format("%d images have been resized", getResizedImages()));
        log.info(format("There are %d completed images in the final container", destination.getAmountPresent()));
    }

    private long getImprovedImages() {
        return source.getImages().stream().filter(i -> i.getImprovements() == 3).count() +
                destination.getImages().stream().filter(i -> i.getImprovements() == 3).count();
    }

    private long getResizedImages() {
        return source.getImagesOfSize(MEDIUM).size() + destination.getImagesOfSize(MEDIUM).size();
    }
}

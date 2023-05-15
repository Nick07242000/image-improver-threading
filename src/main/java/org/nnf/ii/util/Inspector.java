package org.nnf.ii.util;

import lombok.Builder;
import org.apache.log4j.Logger;
import org.nnf.ii.model.Container;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.lang.Thread.getAllStackTraces;
import static java.util.Comparator.comparing;
import static org.nnf.ii.util.Util.delay;

@Builder
public class Inspector implements Runnable {
    private final Logger log = Logger.getLogger(Inspector.class);
    private Container source;
    private Container destination;

    @Override
    public void run() {
        while (destination.hasCapacity()) {
            delay(500);
            logContainers();
            logThreads();
        }
        logContainers();
        logThreads();
        log.info("Finished!");
    }

    private void logContainers() {
        log.info(format("There are %d images in the initial container", source.getAmountPresent()));
        log.info(format("%d images have been improved", getImprovedImages()));
        log.info(format("%d images have been resized", getResizedImages()));
        log.info(format("There are %d completed images in the final container", destination.getAmountPresent()));
    }

    private long getImprovedImages() {
        return source.countImprovedImages() + destination.countImprovedImages();
    }

    private long getResizedImages() {
        return source.countResizedImages() + destination.countResizedImages();
    }

    private void logThreads() {
        getAllStackTraces().keySet().stream()
                .filter(thread -> thread.getName().contains("Thread-"))
                .sorted(comparing(t -> parseInt(t.getName().split("-")[1])))
                .forEach(thread -> log.info(format("%s - %s", thread.getName(), thread.getState())));
    }
}

package org.nnf.ii.util;

import lombok.Builder;
import lombok.SneakyThrows;
import org.apache.log4j.Logger;
import org.nnf.ii.model.Container;

import static java.lang.String.format;
import static java.lang.Thread.sleep;

@Builder
public class Inspector implements Runnable {
    private final Logger log = Logger.getLogger(Inspector.class);
    private Container source;
    private Container destination;

    @SneakyThrows
    @Override
    public void run() {
        while (destination.hasCapacity())
        log.info(format("There are %d images in the initial container", source.getAmountPresent()));
        log.info(format("%d images have been improved", source.getImages().stream().filter(i -> i.getImprovements() == 3).count()));
        //log.info(format("%d images have been resized", source.getImages().stream().filter(i -> i.?).count()));
        //log.info(format("There are %d completed images in the final container", data.getAmountPresent()));
        sleep(500);
    }
}

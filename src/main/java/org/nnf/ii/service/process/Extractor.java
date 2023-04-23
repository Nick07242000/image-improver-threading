package org.nnf.ii.service.process;

import lombok.Builder;
import lombok.Getter;
import lombok.Synchronized;
import org.apache.log4j.Logger;
import org.nnf.ii.model.Container;
import org.nnf.ii.model.Image;

import java.util.List;

import static java.lang.String.format;
import static org.nnf.ii.util.Util.getRandomNumber;
import static java.lang.Thread.currentThread;

@Getter
@Builder
public class Extractor implements Runnable {
    private final Logger log = Logger.getLogger(Extractor.class);
    private final List<Image> source;
    private final Container destination;

    @Override
    public void run() {
        log.debug(format("Extractor Running - %s",currentThread().getName()));
        while (destination.hasCapacity()) {
            addToDestination(extractFromSource());
        }
        log.debug(format("Extractor Finished - %s",currentThread().getName()));
        log.debug(format("Container has %d images",destination.getAmountPresent()));
    }

    private Image extractFromSource() {
        int n = getRandomNumber(0, source.size());
        log.debug(format("Extracting image %d from source",n));
        return source.get(n);
    }

    @Synchronized
    private void addToDestination(Image image) {
        if (!destination.isPresent(image)) {
            log.debug("Image %d not present in source - adding");
            destination.add(image);
        }
    }
}

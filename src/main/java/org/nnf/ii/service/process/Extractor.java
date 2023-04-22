package org.nnf.ii.service.process;

import lombok.Builder;
import lombok.Getter;
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
        log.info(format("Extractor Running - %s",currentThread().getName()));
        while (destination.hasCapacity()) {
            extract();
        }
        log.info(format("Extractor Finished - %s",currentThread().getName()));
        log.info(format("Container has %d images",destination.getAmountPresent()));
    }

    private void extract() {
        int n = getRandomNumber(0, source.size());
        Image image = source.get(n);

        log.info(format("Extracting image %d from source",n));

        synchronized (this) {
            if (!destination.isPresent(image)) {
                log.info(format("Image %d not present in source - adding",n));
                destination.add(image);
            }
        }
    }
}

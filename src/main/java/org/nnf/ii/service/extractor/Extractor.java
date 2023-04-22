package org.nnf.ii.service.extractor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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

    private final Object key;

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
        Image image;
        int n = getRandomNumber(0, source.size());
        log.info(format("Extracting image %d from source",n));
        image = source.get(n);

        synchronized (key) {
            if (!destination.isPresent(image)) {
                log.info(format("Image %d not present in source - adding",n));
                destination.add(image);
            }
        }


    }
}

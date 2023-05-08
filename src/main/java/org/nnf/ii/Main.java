package org.nnf.ii;

import org.apache.log4j.Logger;
import org.nnf.ii.model.Container;
import org.nnf.ii.model.Image;
import org.nnf.ii.service.Brightener;
import org.nnf.ii.service.Extractor;
import org.nnf.ii.service.Persister;
import org.nnf.ii.service.Resizer;
import org.nnf.ii.util.Inspector;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.nnf.ii.repository.ImageRepository.findAll;
import static org.nnf.ii.util.ThreadFactory.startThreads;

public class Main {

    public static void main(String[] args) {
        Logger log = Logger.getLogger(Main.class);

        log.info("Starting...");

        List<Image> images = findAll(500);

        Container initialContainer = Container.builder().size(100).build();
        Container finalContainer = Container.builder().size(100).build();

        CountDownLatch collectionNotEmpty = new CountDownLatch(1);

        Inspector inspector = Inspector.builder()
                .source(initialContainer)
                .destination(finalContainer)
                .build();
        startThreads(inspector,1);

        AtomicInteger extracted = new AtomicInteger(0);
        Extractor extractor = Extractor.builder()
                .source(images)
                .destination(initialContainer)
                .unlocker(collectionNotEmpty)
                .extracted(extracted)
                .build();
        startThreads(extractor,2);

        Brightener brightener = Brightener.builder()
                .container(initialContainer)
                .waiter(collectionNotEmpty)
                .build();
        startThreads(brightener, 3);

        AtomicInteger resized = new AtomicInteger(0);
        Resizer resizer = Resizer.builder()
                .container(initialContainer)
                .totalResized(resized)
                .waiter(collectionNotEmpty)
                .build();
        startThreads(resizer, 3);

        Persister persister = Persister.builder()
                .source(initialContainer)
                .destination(finalContainer)
                .waiter(collectionNotEmpty)
                .build();
        startThreads(persister, 2);

    }
}

package org.nnf.ii;

import org.apache.log4j.Logger;
import org.nnf.ii.model.Container;
import org.nnf.ii.model.Image;
import org.nnf.ii.service.process.Brightener;
import org.nnf.ii.service.process.Extractor;
import org.nnf.ii.service.process.Resizer;
import org.nnf.ii.service.semaphore.Queue;
import org.nnf.ii.util.Inspector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.nnf.ii.repository.ImageRepository.findAll;
import static org.nnf.ii.util.ThreadFactory.startThreads;

public class Main {

    public static void main(String[] args) {
        Logger log = Logger.getLogger(Main.class);

        log.debug("Starting...");

        List<Image> images = findAll(500);

        Container initialContainer = Container.builder().size(100).build();
        Container finalContainer = Container.builder().size(100).build();

        CountDownLatch collectionNotEmpty = new CountDownLatch(1);

        Inspector inspector = Inspector.builder().source(initialContainer).destination(finalContainer).build();
        startThreads(inspector,1);

        Extractor extractor = Extractor.builder().source(images).destination(initialContainer).unlocker(collectionNotEmpty).build();
        startThreads(extractor,2);

        Queue queue = new Queue();

        Brightener brightener = Brightener.builder().container(initialContainer).queue(queue).waiter(collectionNotEmpty).build();
        startThreads(brightener, 3);

        Resizer resizer = Resizer.builder().container(initialContainer).queue(queue).waiter(collectionNotEmpty).build();
        startThreads(resizer, 3);

    }
}

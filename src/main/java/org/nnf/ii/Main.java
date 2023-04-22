package org.nnf.ii;

import org.apache.log4j.Logger;
import org.nnf.ii.model.Container;
import org.nnf.ii.model.Image;
import org.nnf.ii.service.extractor.Extractor;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.nnf.ii.repository.ImageRepository.findAll;
import static org.nnf.ii.util.ThreadFactory.startThreads;

public class Main {

    public static void main(String[] args) {
        Logger log = Logger.getLogger(Main.class);

        log.info("Starting...");

        List<Image> images = findAll(500);

        Container initialContainer = Container.builder().size(100).images(new ArrayList<>()).build();

        Extractor extractor = Extractor.builder().source(images).destination(initialContainer).build();

        startThreads(extractor,2);
    }
}

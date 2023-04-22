package org.nnf.ii.service.brightener;

import lombok.Builder;
import lombok.Getter;
import org.apache.log4j.Logger;
import org.nnf.ii.model.Container;
import org.nnf.ii.model.Image;
import org.nnf.ii.model.enums.Status;


import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static org.nnf.ii.util.Util.getRandomNumber;

@Builder
public class Brightener implements Runnable {

    private final Logger log = Logger.getLogger(Brightener.class);
    private final Container container;
    private final Object key;

    @Override
    public void run() {
        log.info(format("Brightener Running - %s",currentThread().getName()));
        bright();
        log.info(format("Brightener Finished - %s",currentThread().getName()));
    }

    private void bright() {
        List<Image> accessedList = new ArrayList<>();
        while (accessedList.size()<container.getSize()) {
            Image image;
            synchronized (key) {
                do {
                    synchronized (container) {
                        image = container.getRandom();
                    }
                }
                while (image.getStatus()!=Status.READY || accessedList.contains(image));
                image.setStatus(Status.IN_PROGRESS);
            }
            log.info(format("Brightening - %s image %s",Thread.currentThread().getName(), image.getUrl()));
            try {
                Thread.sleep(10);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            image.setImprovements(image.getImprovements()+1);
            accessedList.add(image);
            synchronized (key){
                image.setStatus(Status.READY);
            }
        }

    }
}

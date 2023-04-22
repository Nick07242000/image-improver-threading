package org.nnf.ii.service.resizer;

import lombok.Builder;
import org.apache.log4j.Logger;
import org.nnf.ii.model.Container;
import org.nnf.ii.model.Image;
import org.nnf.ii.model.enums.Size;
import org.nnf.ii.model.enums.Status;
import org.nnf.ii.service.brightener.Brightener;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
import static org.nnf.ii.util.Util.getRandomNumber;

@Builder
public class Resizer implements Runnable{
    private final Logger log = Logger.getLogger(Resizer.class);
    private final Container container;
    private final Object key;


    @Override
    public void run() {
        log.info(format("Resizer Running - %s",currentThread().getName()));
        resize();
        log.info(format("Resizer Finished - %s",currentThread().getName()));
    }

    private void resize() {

            Image image;
            while (!container.isEmpty()) {
                synchronized (key) {
                    do {
                        synchronized (container) {
                            image = container.getRandom();
                        }
                    }
                    while (image.getImprovements()<3 && image.getStatus()!=Status.READY);
                    image.setStatus(Status.IN_PROGRESS);
                }
                log.info(format("Resizing - %s image %s",Thread.currentThread().getName() ,image.getUrl()));
                try {
                    Thread.sleep(200);

                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                if(image.getSize()!=Size.MEDIUM) image.setSize(Size.MEDIUM);
                synchronized (key) {
                    image.setStatus(Status.FINISHED);
                }
            }

    }
}

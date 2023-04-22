package org.nnf.ii.service.persister;

import lombok.Builder;
import org.apache.log4j.Logger;
import org.nnf.ii.model.Container;
import org.nnf.ii.model.Image;
import org.nnf.ii.model.enums.Size;
import org.nnf.ii.model.enums.Status;
import org.nnf.ii.service.resizer.Resizer;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;
@Builder
public class Persister implements Runnable{
    private final Logger log = Logger.getLogger(Persister.class);
    private final Container container;
    private Container finalContainer;
    private final Object key;

    @Override
    public void run() {
        log.info(format("Persister Running - %s",currentThread().getName()));
        resize();
        log.info(format("Persister Finished - %s",currentThread().getName()));
    }

    private void resize() {

        Image image;
        while (finalContainer.hasCapacity()) {
            synchronized (key) {
                do {
                    synchronized (container) {
                        image = container.getRandom();
                    }
                }
                while (image.getStatus()!= Status.FINISHED);
                image.setStatus(Status.IN_PROGRESS);
            }
            log.info(format("Persisting -%s image %s",Thread.currentThread().getName(), image.getUrl()));
            try {
                Thread.sleep(300);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            finalContainer.add(image);
            container.remove(image);
            image.setStatus(Status.FINISHED);
            log.info(format("imagenes en el contenedor final %d", finalContainer.getAmountPresent()));
        }

    }
}

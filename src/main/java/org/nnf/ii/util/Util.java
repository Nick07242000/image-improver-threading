package org.nnf.ii.util;

import org.apache.log4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Thread.sleep;

public final class Util {
    private final static Logger log = Logger.getLogger(Util.class);
    private Util() {}

    public static int getRandomNumber(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public static void delay(int max) {
        try {
            sleep(getRandomNumber(0,max));
        } catch (InterruptedException e){
            log.error(e.getMessage());
        }
    }

    public static void waitFor(CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }
}

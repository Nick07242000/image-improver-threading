package org.nnf.ii.util;

import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Thread.sleep;

public final class Util {
    private Util() {}

    public static int getRandomNumber(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public static void delay(int max) {
        try {
            sleep(getRandomNumber(0,max));
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}

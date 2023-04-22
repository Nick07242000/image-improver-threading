package org.nnf.ii.util;

import java.util.concurrent.ThreadLocalRandom;

public final class Util {
    private Util() {}

    public static int getRandomNumber(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }
}

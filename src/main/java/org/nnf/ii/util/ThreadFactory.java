package org.nnf.ii.util;

public final class ThreadFactory {
    public static void startThreads(Runnable runnable, int amount) {
        for (int i = 0; i < amount; i++){
            Thread t = new Thread(runnable);
            t.start();
        }
    }
}

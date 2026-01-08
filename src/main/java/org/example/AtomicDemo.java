package org.example;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicDemo {
    //Atomic classes use low-level CPU instructions like CAS (Compare-And-Swap) for fast thread-safe updates.
    static AtomicInteger count = new AtomicInteger();

    public static void main(String[] args) throws Exception {

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) count.incrementAndGet();
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) count.incrementAndGet();
        });

        t1.start(); t2.start();
        t1.join(); t2.join();

        System.out.println("Final count = " + count.get());
}
}

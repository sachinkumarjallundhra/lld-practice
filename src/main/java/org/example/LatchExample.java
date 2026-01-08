package org.example;

import java.util.concurrent.CountDownLatch;

public class LatchExample {

    public static void main(String[] args) throws Exception {

        CountDownLatch latch = new CountDownLatch(3);

        for (int i = 1; i <= 3; i++) {
            int worker = i;
            new Thread(() -> {
                System.out.println("Worker " + worker + " done");
                latch.countDown(); // signal completion
            }).start();
        }

        latch.await();
        System.out.println("All workers completed!");
    }
}

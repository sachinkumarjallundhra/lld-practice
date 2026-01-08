package org.example;

public class SyncDemo {
    static int count = 0;

    public synchronized static void increment() {
        count++;
    }

    public static void main(String[] args) throws Exception {

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) increment();
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) increment();
        });

        t1.start(); t2.start();
        t1.join(); t2.join();

        System.out.println("Final count = " + count);
    }
}

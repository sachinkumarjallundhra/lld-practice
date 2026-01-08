package org.example;

public class SimpleDeadlockDemo {

    private static final Object R1 = new Object();
    private static final Object R2 = new Object();

    public static void main(String[] args) {

        Thread T1 = new Thread(() -> {
            System.out.println("T1: trying to lock R1");
            synchronized (R1) {
                System.out.println("T1: locked R1");

                sleep(100); // give T2 time to lock R2

                System.out.println("T1: trying to lock R2");
                synchronized (R2) {
                    System.out.println("T1: locked R2");
                }
            }
        });

        Thread T2 = new Thread(() -> {
            System.out.println("T2: trying to lock R2");
            synchronized (R2) {
                System.out.println("T2: locked R2");

                sleep(100); // give T1 time to lock R1

                System.out.println("T2: trying to lock R1");
                synchronized (R1) {
                    System.out.println("T2: locked R1");
                }
            }
        });

        T1.start();
        sleep(101);
        T2.start();
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { }
    }
}
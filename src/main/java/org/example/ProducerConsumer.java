package org.example;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ProducerConsumer {
    public static void main(String[] args) {

        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);

        Thread producer = new Thread(() -> {
            try {
                int value = 0;
                while (true) {
                    System.out.println("Producing: " + value);
                    queue.put(value++);  // waits if queue is full
                    Thread.sleep(300);
                }
            } catch (Exception e) {}
        });

        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    int value = queue.take();  // waits if queue is empty
                    System.out.println("Consuming: " + value);
                    Thread.sleep(500);
                }
            } catch (Exception e) {}
        });

        producer.start();
        consumer.start();
    }
}

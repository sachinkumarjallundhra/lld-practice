package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Most interviews expect you know why creating many threads is bad.
//Tasks executed by only 3 threads → efficient and fast.
public class ThreadPoolExample {
    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(3);

        for (int i = 1; i <= 10; i++) {
            int taskId = i;
            service.submit(() ->
                    System.out.println("Executing task " + taskId + " by " + Thread.currentThread().getName())
            );
        }

        service.shutdown();
    }
}

package com.ft.javabase.concurrent;

import org.junit.Test;
import sun.nio.ch.ThreadPool;

import java.util.concurrent.*;

public class TestSemaphore {
    private ThreadPoolExecutor initThreadPool() {
        int processorNum = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor pool = new ThreadPoolExecutor(processorNum, processorNum*2, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        return pool;
    }
    private void waitPoolEnd(ThreadPoolExecutor pool, Semaphore sem) {
        while (true) {
            int activCount = pool.getActiveCount();
            int taskCount = pool.getQueue().size();

            System.out.println("            " + pool.toString());

            if (activCount == 0 && taskCount == 0) {
                pool.shutdown();
                break;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void testSemphore(boolean faire) {
        ThreadPoolExecutor pool = initThreadPool();

        Semaphore sem = new Semaphore(3, faire);
        for (int i=0; i<5; i++) {
            try {
                Future<Long> futureResult = pool.submit(() -> {
                    try {
                        sem.acquire();
//                        System.out.println(Thread.currentThread().getId() + " | availablePermits is " + sem.availablePermits() + " drainPermits is " + sem.drainPermits()
//                                + " getQueueLength is " + sem.getQueueLength()
//                                + " hasQueuedThreads is " + sem.hasQueuedThreads());
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch(Exception e) {
                        e.printStackTrace();
                    } finally {
                        sem.release();
                    }
                    return Thread.currentThread().getId();
                });
            } catch (RejectedExecutionException e) {
                e.printStackTrace();
            }
        }

        waitPoolEnd(pool, sem);
    }

    @Test
    public void testSemphoreNonFaire() {
        testSemphore(false);
    }

    @Test
    public void testSemphoreFaire() {
        testSemphore(true);
    }
}

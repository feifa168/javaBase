package com.ft.javabase.concurrent;

import org.junit.Test;

import java.util.concurrent.*;

public class TestCountDownLatch {

    @Test
    public void testLatchWaitAllEnd() {
        ThreadPoolExecutor pool = initThreadPool();

        final int latchNum = 10;
        CountDownLatch latch = new CountDownLatch(latchNum);
        for (int i=0; i<latchNum; i++) {
            pool.execute(new LatchDownTask(latch));
        }

        waitPoolEnd(latch);
    }
    @Test
    public void testLatchWaitAllBegin() {
        ThreadPoolExecutor pool = initThreadPool();

        final int latchNum = 1;
        CountDownLatch latch = new CountDownLatch(latchNum);
        for (int i=0; i<10; i++) {
            pool.execute(new LatchWaitTask(latch));
        }

        try {
            System.out.println("wait 2 second to continue...");
            Thread.sleep(200000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        latch.countDown();
    }

    @Test
    public void testLatchWaitAndDown() {
        ThreadPoolExecutor pool = initThreadPool();

        final int latchNum = 10;
        CountDownLatch latchWait = new CountDownLatch(1);
        CountDownLatch latchDown = new CountDownLatch(latchNum);
        for (int i=0; i<latchNum; i++) {
            pool.execute(new LatchWaitAndDownTask(latchWait, latchDown));
        }

        try {
            System.out.println("wait 2 second to continue...");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        latchWait.countDown();
        waitPoolEnd(latchDown);
    }

    private class LatchWaitTask implements Runnable {
        CountDownLatch latch;
        public LatchWaitTask(CountDownLatch latch) {
            this.latch = latch;
        }
        @Override
        public void run() {
            try {
                latch.await();
                System.out.println(Thread.currentThread().getId() + " | " + latch.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private class LatchDownTask implements Runnable {
        CountDownLatch latch;
        public LatchDownTask(CountDownLatch latch) {
            this.latch = latch;
        }
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getId() + " | " + latch.toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            latch.countDown();
        }
    }
    private class LatchWaitAndDownTask implements Runnable {
        CountDownLatch latchWait;
        CountDownLatch latchDown;
        public LatchWaitAndDownTask(CountDownLatch latchWait, CountDownLatch latchDown) {
            this.latchWait = latchWait;
            this.latchDown = latchDown;
        }
        @Override
        public void run() {
            try {
                latchWait.await();
                System.out.println(Thread.currentThread().getId() + " | " + latchDown.toString());
                Thread.sleep(1000);
                latchDown.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private ThreadPoolExecutor initThreadPool() {
        int processorNum = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor pool = new ThreadPoolExecutor(processorNum, processorNum*2, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        return pool;
    }
    private void waitPoolEnd(CountDownLatch latch) {
        try {
            System.out.println("wait for all end to continue...");
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("pool is end " + latch.toString());
    }
}

package com.ft.javabase.concurrent;

import org.junit.Test;
import sun.nio.ch.ThreadPool;

import java.util.concurrent.*;

public class TestSemaphore {
    @Test
    public void testSemphoreNonFaire() {
        long taskWaitMillis = 1000;
        int semNum  = 2;
        int poolNum = 3;
        int loopNum = 5;
        boolean faire = false;
        testSemphore(taskWaitMillis, semNum, poolNum, loopNum, faire);
    }

    @Test
    public void testSemphoreFaire() {
        long taskWaitMillis = 5000;
        int semNum  = Runtime.getRuntime().availableProcessors();
        int poolNum = semNum*2;
        int loopNum = semNum*2;
        boolean faire = true;
        testSemphore(taskWaitMillis, semNum, poolNum, loopNum, faire);
    }

    @Test
    public void testTryAcquire() {
        long taskWaitMillis = 2000;
        int semNum  = 1;//Runtime.getRuntime().availableProcessors();
        int poolNum = 2;//semNum*2;
        int loopNum = 2;//semNum*2;
        boolean faire = true;

        boolean useTry = true;
        boolean tryWithMillions = true;
        testSemphore(taskWaitMillis, semNum, poolNum, loopNum, faire, useTry, tryWithMillions);
    }

    private void displaySemaphore(String preTag, Semaphore sem, String endTag) {
        StringBuilder sb = new StringBuilder(128);
        // drainPermits是把信号降为0并返回被取消的信号个数，误把它作为获取某个返回值使用了
        // 而线程池一直等待激活的线程数为0，由于信号被随机取消，导致已经处于acquire的线程一直得不到调度，所以才会导致一直运行
        sb.append(Thread.currentThread().getId()).append(" ")
                .append(", availablePermits ").append(sem.availablePermits())
                //.append(", drainPermits ").append(sem.drainPermits())
                .append(", hasQueuedThreads ").append(sem.hasQueuedThreads())
                .append(", getQueueLength ").append(sem.getQueueLength())
                .append(", isFair ").append(sem.isFair())
        ;
        System.out.println(preTag + sb.toString() + endTag);
    }

    private ThreadPoolExecutor initThreadPool(int coreNum) {
        int processorNum = coreNum;//Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor pool = new ThreadPoolExecutor(processorNum, processorNum*2, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        return pool;
    }
    private void waitPoolEnd(ThreadPoolExecutor pool, Semaphore sem) {
        while (true) {
            int activCount = pool.getActiveCount();
            int taskCount = pool.getQueue().size();

            System.out.println("            @@ " + pool.toString());
            displaySemaphore("              ## wait ", sem, "");

            if (activCount == 0 && taskCount == 0) {
                displaySemaphore("end pool ", sem, "");
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

    private void testSemphore(long taskWaitMillis, int semNum, int poolNum, int loopNum, boolean faire, boolean useTry, boolean tryWithMillis) {
        ThreadPoolExecutor pool = initThreadPool(poolNum);

        Semaphore sem = new Semaphore(semNum, faire);
        for (int i=0; i<loopNum; i++) {
            try {
                Future<Long> futureResult = pool.submit(() -> {
                    try {
                        if (useTry) {
                            while (true) {
                                displaySemaphore("    task before tryAccquire ", sem, "");
                                if (tryWithMillis) {
                                    if (sem.tryAcquire(taskWaitMillis, TimeUnit.MILLISECONDS)) {
                                        break;
                                    }
                                } else {
                                    if (sem.tryAcquire()) {
                                        break;
                                    }
                                    Thread.sleep(taskWaitMillis);
                                }
                                Thread.currentThread().interrupt();
                                //displaySemaphore("      ** task after accquire ", sem, "");
                            }
                        } else {
                            displaySemaphore("    task accquire ", sem, "");
                            sem.acquire();
                        }

                        Thread.sleep(taskWaitMillis*2);
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

    private void testSemphore(long taskWaitMillis, int semNum, int poolNum, int loopNum, boolean faire) {
        testSemphore(taskWaitMillis, semNum, poolNum, loopNum, faire, false, false);
    }
}

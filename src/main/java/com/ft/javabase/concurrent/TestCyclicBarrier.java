package com.ft.javabase.concurrent;

import org.junit.Test;

import java.util.concurrent.*;

public class TestCyclicBarrier {

    /**
     * CyclicBarrier有个限制，等待的线程必须是parties初始值相等，否则未满足条件前会一直等待或者超时
     * await结束条件，
     * 1.屏障打开，也就是说等待的线程必须等于parties初始值;
     * 2.本线程被interrupt;
     * 3.本线程timeout;
     * 4.其他等待线程被interrupted;
     * 5.其他等待线程timeout;
     * 6.其他线程调用reset()
     * await内部调用dowait，dowait执行barrierAction，也就是说await返回前会先触发barrierAction
     */
    @Test
    public void testRunnable() {
        int processorNum = 2;//Runtime.getRuntime().availableProcessors();
        int cyclicNum    = processorNum;
        int loopNum      = cyclicNum;//cyclicNum * 2;
        cyclicWithRunnable(cyclicNum, loopNum, true, 1000);
    }

    /**
     * Cyclic触发了barrier之后，可以重复使用，继续等待下一轮的触发barrier
     */
    @Test
    public void testLoop() {
        int processorNum = 2;//Runtime.getRuntime().availableProcessors();
        int cyclicNum    = processorNum;
        int loopNum      = cyclicNum * 2;
        cyclicWithRunnable(cyclicNum, loopNum, true, 1000);
    }

    /**
     * 当前线程超时会抛出TimeoutException，如果有一个线程抛出异常，则其他等待的线程都将抛出BrokenBarrierException
     * 一旦有抛出异常，则后续所有的await都会抛出异常BrokenBarrierException
     */
    @Test
    public void testTimeout() {
        int processorNum = 2;//Runtime.getRuntime().availableProcessors();
        int cyclicNum    = processorNum+1;
        int loopNum      = cyclicNum+1;
        cyclicWithRunnable(cyclicNum, loopNum, true, 1000);
    }

    /**
     * reset让正在等待的线程抛出BrokenBarrierException异常，会清空等待的线程数，不影响后续线程的执行。
     */
    @Test
    public void testReset() {
        int processorNum = 2;//Runtime.getRuntime().availableProcessors();
        int cyclicNum    = processorNum+1;
        int loopNum      = cyclicNum;
        cyclicWithRunnable(cyclicNum, loopNum, true, 1500, true, 1000);
    }

    private void cyclicWithRunnable(int cyclicNum, int loopNum, boolean ifwait, long timeout) {
        cyclicWithRunnable(cyclicNum, loopNum, ifwait, timeout, false, 0);
    }

    private void cyclicWithRunnable(int cyclicNum, int loopNum, boolean ifwait, long timeout, boolean ifreset, long waitResetTime) {
        CyclicBarrier cb = new CyclicBarrier(cyclicNum, new EatEndTask(0));

        String[] names = new String[] {
                "ZhangSan",
                "LiSi",
                "WangWu",
                "MaLiu"
        };
        ThreadPoolExecutor pool = initThreadPool();
        for (int i=0; i<loopNum; i++) {
            pool.execute(new EatTask(cb, names[i%names.length], ifwait, timeout));
        }

        if (ifreset) {
            try {
                Thread.sleep(waitResetTime);
                System.out.println("   ++++ " + displayCyclicBarrier(cb) + " begin reset ");
                cb.reset();
                System.out.println("   ++++ " + displayCyclicBarrier(cb) + " after reset ");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        waitPoolEnd(cb);
    }

    private class EatEndTask implements  Runnable {
        public EatEndTask(long sleepMillis) {
            this.sleepMillis = sleepMillis;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getId() + " Now that everyone is here, let's go to dinner together.");
            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private long sleepMillis;
    }

    private class EatTask implements Runnable {

        public EatTask(CyclicBarrier cb, String name) {
            init(cb, name, false, -1);
        }

        public EatTask(CyclicBarrier cb, String name, boolean ifwait, long timeout) {
            init(cb, name, ifwait, timeout);
        }

        @Override
        public void run() {
            System.out.println(displayCyclicBarrier(cb) + "    " + Thread.currentThread().getId() + " " + name + ": wait me for eat... ");
            try {
                if (ifwait) {
                    cb.await(timeout, TimeUnit.MILLISECONDS);
                } else {
                    cb.await();
                }
            } catch (InterruptedException e) {
                // e.printStackTrace();
                System.out.println("   ||" + displayCyclicBarrier(cb) + "  InterruptedException || " + e.getMessage());
            } catch (BrokenBarrierException e) {
                // e.printStackTrace();
                System.out.println("   ||" + displayCyclicBarrier(cb) + "  BrokenBarrierException || " + e.getMessage());
            } catch (TimeoutException e) {
                // e.printStackTrace();
                System.out.println("   ||" + displayCyclicBarrier(cb) + "  TimeoutException || " + e.getMessage());
            } catch (Exception e) {
                // e.printStackTrace();
                System.out.println("   ||" + displayCyclicBarrier(cb) + "  Exception || " + e.getMessage());
            }
            System.out.println("            " + displayCyclicBarrier(cb) + " " + name + ": I'm eating.");
        }

        private void init(CyclicBarrier cb, String name, boolean ifwait, long timeout) {
            this.cb = cb;
            this.name = name;
            this.ifwait = ifwait;
            this.timeout = timeout;
        }

        private String name;
        private CyclicBarrier cb;
        private boolean ifwait;
        private long timeout;
    }
    private ThreadPoolExecutor initThreadPool() {
        int processorNum = 2;//Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor pool = new ThreadPoolExecutor(processorNum, processorNum*2, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        return pool;
    }
    private void waitPoolEnd(CyclicBarrier cb) {
        //System.out.println("wait for all end to continue..." + displayCyclicBarrier(cb));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("pool is end " + displayCyclicBarrier(cb));
    }
    private String displayCyclicBarrier(CyclicBarrier cb) {
        StringBuilder sb = new StringBuilder(128);
        sb.append(" ").append(Thread.currentThread().getId())
                .append(" getParties ").append(cb.getParties())
                .append(" getNumberWaiting ").append(cb.getNumberWaiting())
                .append(" isBroken [").append(cb.isBroken()).append("]")
                ;
        return sb.toString();
    }
}

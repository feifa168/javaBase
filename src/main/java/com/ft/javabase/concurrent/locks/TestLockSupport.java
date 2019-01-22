package com.ft.javabase.concurrent.locks;

import org.junit.Test;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.locks.LockSupport;

public class TestLockSupport {
    static class FinishFlag {
        public boolean finish = false;
    }

    /**
     * 通过object的wait和notify控制线程运行或等待，要考虑以下几种情况，比较复杂。
     * 1.notify先于wait之前发出消息，notify之后设置一个变量标识已经发过消息，wait之前判断若不满足该变量的条件则执行wait，否则直接跳过。
     * 2.wait被虚假唤醒，要在循环中加入判断条件以确定是真实触发了通知。
     * 3.wait和notify必须和synchronized配合使用。
     */
    @Test
    public void testObjectWaitAndNotify() {
        FinishFlag ff = new FinishFlag();

        Object obj = new Object();

        Thread td = new Thread(()->{
            System.out.println(Thread.currentThread() + " is begin");
            synchronized (obj) {
                try {
                    Thread.sleep(1000);
                    while (!ff.finish) {
                        obj.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread() + " is end");
        });
        td.start();

        try {
            //Thread.sleep(1000);

            synchronized (obj) {
                obj.notify();
                ff.finish = true;
                System.out.println("obj is notify");
            }

            td.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过LockSupport.park和unpark控制线程挂起和恢复，很简单。
     * 1.unpark可以先于park执行，但必须在线程调用start后执行unpark才有效，没有notify先于wait之前发出消息导致无法获取通知的问题。
     * 2.没有wait虚假唤醒的问题。
     * 3.不依赖synchronized，不需要像wait和notify那样必须和synchronized配合使用。
     */
    @Test
    public void testLockSupport() {
        Thread td = new Thread(()->{
            System.out.println(Thread.currentThread() + " is begin");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread() + " is park");
            LockSupport.park();
            System.out.println(Thread.currentThread() + " is end");
        });

        td.start();

        try {
            //Thread.sleep(1000);
            LockSupport.unpark(td);
            System.out.println(Thread.currentThread() + " is unpark");

            td.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

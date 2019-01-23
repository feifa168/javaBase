package com.ft.javabase;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class FtAQS {
    @Test
    public void testAQS() {
        ReentrantLock rtl = new ReentrantLock();

        for (int i=0; i<5; i++) {
            new Thread(() -> {
                rtl.lock();
                try {
                    Thread.sleep(100000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    rtl.unlock();
                }
            }).start();
        }
    }
}

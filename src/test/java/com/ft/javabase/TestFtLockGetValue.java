package com.ft.javabase;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TestFtLockGetValue {
    private void testGetVByThreadPool(String className, int loopNum) {
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor pool = new ThreadPoolExecutor(corePoolSize, corePoolSize*2, 60L, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        //Executors.newFixedThreadPool(corePoolSize);

        FtLockGetValue ftLock = new FtLockGetValue();
        try {
            Method md = ftLock.getClass().getMethod(className, String.class);

            long begin = System.currentTimeMillis();
            pool.execute(()->{
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int j=0; j<loopNum; j++) {
                    try {
                        md.invoke(ftLock, "one");
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            });
            pool.shutdown();
            System.out.println(className + " time is " + (System.currentTimeMillis()-begin) + "ms");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void testGetVByMT(String className, int loopNum) {
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        FtLockGetValue ftLock = new FtLockGetValue();
        try {
            Method md = ftLock.getClass().getMethod(className, String.class);
            List<Thread> threads = new ArrayList<>();
            for (int i=0; i<corePoolSize; i++) {
                threads.add(new Thread(()->{
                    for (int j=0; j<loopNum; j++) {
                        try {
                            md.invoke(ftLock, "one");
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }));
            }
            long begin = System.currentTimeMillis();
            for (Thread td : threads) {
                td.start();
            }
            System.out.println(className + " time is " + (System.currentTimeMillis()-begin) + "ms");
            for (Thread td : threads) {
                try {
                    td.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void testInvokeGetV(String methodName, String methodName2, int loopNum) {
        try {
            Method md = this.getClass().getDeclaredMethod(methodName, String.class, int.class);
            try {
                md.setAccessible(true);
                md.invoke(this, methodName2, loopNum);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void testGetValue(String methodName, int loopNum) {
        testInvokeGetV(methodName, "getValue", loopNum);
    }
    public void testGetValueSyn(String methodName, int loopNum) {
        testInvokeGetV(methodName, "getValueSyn", loopNum);
    }
    public void testGetValueLocalSyn(String methodName, int loopNum) {
        testInvokeGetV(methodName, "getValueLocalSyn", loopNum);
    }
    public void testGetValueRWLock(String methodName, int loopNum) {
        testInvokeGetV(methodName, "getValueRWLock", loopNum);
    }

    @Test
    public void testAll() {
        int threadNum=8;
        int loopNum=10000000;
        String methodName = "testGetVByThreadPool";
        //String methodName = "testGetVByMT";

        for (int i=0; i<2; i++) {
            //testGetValue(methodName, loopNum);
            testGetValueSyn(methodName, loopNum);
            testGetValueLocalSyn(methodName, loopNum);
            testGetValueRWLock(methodName, loopNum);
        }
    }
}

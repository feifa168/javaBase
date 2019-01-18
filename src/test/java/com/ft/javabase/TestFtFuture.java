package com.ft.javabase;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Future;

public class TestFtFuture {

    static class FtCallBackImpl implements FtCallBack<Boolean, Integer> {
        @Override
        public Boolean call(Future<Integer> ft) {
            Boolean done = ft.isDone();
            if (!done) {
                System.out.println("callback is not done");
            } else {
                System.out.println("========= callback is done");
            }
            return done;
        }
    }

    private void testFuture(String methodName, long mills, boolean block, FtCallBack fcb) {
        System.out.println("===========================================\nbegin " + methodName + " wait time["+mills+"ms] " + " block["+block+"] callback is null["+(fcb==null)+"]");
        FtFuture fft = new FtFuture();
        try {
            Method md = fft.getClass().getDeclaredMethod(methodName, new Class[]{long.class, boolean.class, FtCallBack.class});
            md.invoke(fft, mills, block, fcb);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        System.out.println("end " + methodName + " wait time["+mills+"ms] " + " block["+block+"] callback is null["+(fcb==null)  + "]");
    }
    private void testFutureTask(String methodName, long mills, boolean block, FtCallBack fcb, boolean threadMod) {
        System.out.println("===========================================\nbegin " + methodName + " wait time["+mills+"ms] " + " block["+block+"] callback is null["+(fcb==null)+"] threadmod["+threadMod+"]");
        FtFuture fft = new FtFuture();
        try {
            Method md = fft.getClass().getDeclaredMethod(methodName, new Class[]{long.class, boolean.class, FtCallBack.class, boolean.class});
            md.invoke(fft, mills, block, fcb, threadMod);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        System.out.println("end " + methodName + " wait time["+mills+"ms] " + " block["+block+"] callback is null["+(fcb==null)  + "]");
    }

    public void testByFutureTask(boolean threadMod) {
        //FtCallBackImpl fcbi = new FtCallBackImpl();
        FtCallBack<Boolean, Integer> fcbi = (Future<Integer> ft)->{
            Boolean done = ft.isDone();
            if (!done) {
                System.out.println("callback is not done");
            } else {
                System.out.println("========= callback is done");
            }
            return done;
        };

        long waitMills = 1000;
        String methodName = "futureByFutureTask";
        testFutureTask(methodName, waitMills, false, null, threadMod);
        testFutureTask(methodName, waitMills, false, fcbi, threadMod);
        testFutureTask(methodName, waitMills, true, null, threadMod);
        testFutureTask(methodName, waitMills, true, fcbi, threadMod);
    }
    @Test
    public void testByFutureTaskNotUseThread() {
        testByFutureTask(true);
    }
    @Test
    public void testByFutureTaskUseThread() {
        testByFutureTask(false);
    }
    @Test
    public void testByFutureTask() {
        testByFutureTask(true);
        testByFutureTask(false);
    }

    @Test
    public void testByFuture() {
        //FtCallBackImpl fcbi = new FtCallBackImpl();
        FtCallBack<Boolean, Integer> fcbi = (Future<Integer> ft)->{
            Boolean done = ft.isDone();
            if (!done) {
                System.out.println("callback is not done");
            } else {
                System.out.println("========= callback is done");
            }
            return done;
        };

        long waitMills = 1000;
        String methodName = "futureByFuture";
        testFuture(methodName, waitMills, false, null);
        testFuture(methodName, waitMills, false, fcbi);
        testFuture(methodName, waitMills, true, null);
        testFuture(methodName, waitMills, true, fcbi);
    }
}

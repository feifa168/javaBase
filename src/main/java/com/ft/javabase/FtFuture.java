package com.ft.javabase;

import java.util.concurrent.*;

interface FtCallBack<T, V> {
    T call(Future<V> ft);
}

public class FtFuture {
    public void futureByFutureTask(long millis, boolean block, FtCallBack<Boolean, Integer> callback, boolean threadMod) {
//        FutureTask<Integer> ft = new FutureTask<>(new Callable<Integer>() {
//            @Override
//            public Integer call() throws Exception {
//                Thread.sleep(millis);
//                return 5;
//            }
//        });
        FutureTask<Integer> ft = new FutureTask<>(()->{
            int retValue = 0;
            try {
                Thread.sleep(millis);
                retValue = 5;
            }catch (Exception e) {
            }
           return retValue;
        });

        if (threadMod) {
            new Thread(ft).start();
        } else {
            ExecutorService es = Executors.newFixedThreadPool(1);
            es.submit(ft);
        }

        if (!block) {
            while (true) {
                if (null != callback) {
                    if (callback.call(ft)) {
                        break;
                    }
                } else {
                    if (!ft.isDone()) {
                        System.out.println("future is not done");
                    } else {
                        System.out.println("--------future is done");
                        break;
                    }
                }

                try {
                    Thread.sleep(millis / 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            long begin = System.currentTimeMillis();
            Integer result = (Integer) (ft.get());
            long end = System.currentTimeMillis();
            System.out.println("future with thread result is " + result + " total time is " + (end-begin));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void futureByFuture(long millis, boolean block, FtCallBack<Boolean, Integer> callback) {

        ExecutorService es = Executors.newFixedThreadPool(1);
        Future<Integer> ft = es.submit(()->{
            Thread.sleep(millis);
            return 5;
        });

        if (!block) {
            while (true) {
                if (null != callback) {
                    if (callback.call(ft)) {
                        break;
                    }
                } else {
                    if (!ft.isDone()) {
                        System.out.println("future is not done");
                    } else {
                        System.out.println("--------future is done");
                        break;
                    }
                }

                try {
                    Thread.sleep(millis / 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            long begin = System.currentTimeMillis();
            Integer result = (Integer)(ft.get());
            long end = System.currentTimeMillis();
            System.out.println("future with thread result is " + result + " total time is " + (end-begin));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}

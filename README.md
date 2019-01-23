# javaBase
java 基础

## 多线程
* Runnable
* 继承Thread
* 线程池
* 并发
    * LockSupport  
    > 通过LockSupport.park和unpark控制线程挂起和恢复，简单且安全，可以替代Object的wait/notify/notifyAll工作。  
    1.unpark可以先于park执行，但必须在线程调用start后执行unpark才有效，没有notify先于wait之前发出消息导致无法获取通知的问题。  
    2.没有wait虚假唤醒的问题。  
    3.不依赖synchronized，不需要像wait和notify那样必须和synchronized配合使用。  

    * CountDownLatch  
    > 添加CountDownLatch，底层使用LockSupport控制。 初始一个count，通过await等待，通过执行线程调用down减1，等count减到0时则达到条件继续执行触发所有的await为运行态，其实await是使用LockSupport.park，countDown达到0时使用LockSupport.unpark，控制线程状态，底层使用了操作系统的互斥和条件变量(相当于windows上的事件)。  
    1.可以作为许多线程执行前提条件，让所有线程等待某个条件。初始化count为1的对象，在多线程中调用await，在控制线程调用countDown(1)触发执行条件。  
    2.可以等待所有线程执行结果，在多线程中执行countDown(1)，在等待线程中执行await  
    
    * CyclicBarrier  
    > CyclicBarrier有个限制，等待的线程必须是parties初始值相等，否则未满足条件前会一直等待或者超时或捕获异常 使用介绍 Cyclic触发了barrier之后，可以重复使用，继续等待下一轮的触发barrier 当前线程超时会抛出TimeoutException，如果有一个线程抛出异常，则其他等待的线程都将抛出BrokenBarrierException，一旦有抛出异常，则后续所有的await都会抛出异常BrokenBarrierException reset让正在等待的线程抛出BrokenBarrierException异常，会清空等待的线程数，不影响后续线程的执行。  
      使用介绍  
      Cyclic触发了barrier之后，可以重复使用，继续等待下一轮的触发barrier  
      当前线程超时会抛出TimeoutException，如果有一个线程抛出异常，则其他等待的线程都将抛出BrokenBarrierException，一旦有抛出异常，则后续所有的await都会抛出异常BrokenBarrierException  
      reset让正在等待的线程抛出BrokenBarrierException异常，会清空等待的线程数，不影响后续线程的执行。  
      
     > await结束条件  
      1.屏障打开，也就是说等待的线程必须等于parties初始值;  
      2.本线程被interrupt;  
      3.本线程timeout;  
      4.其他等待线程被interrupted;  
      5.其他等待线程timeout;  
      6.其他线程调用reset()  
      await内部调用dowait，dowait执行barrierAction，也就是说await返回前会先触发barrierAction  
      
    * Semaphore  
    > 初始化数量为N的信号，允许多个线程通过acquire获取信号，release释放信号，当N个线程获取了信号没有释放，那其他线程只能等待。
    * ...  

### Runnable
> 实现Runnable接口并实现run方法。
>> 优点
* 代码量小，仅是一个接口，不影响再继承。
>> 缺点
* 无法得到执行结果，只能通过共享变量或者线程间通信方式得到结果。
```java
@FunctionalInterface
public interface Runnable {
    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see     java.lang.Thread#run()
     */
    public abstract void run();
}

Thread t = new Thread(()->{
   System.out.println(Thread.currentThread());
});
```
### 继承Thread
> 继承Thread并重写run方法
>> 优点
* 代码量小，简单。
>> 缺点
* 无法再继承其他类，可以通过子类或其他接口再扩展
```java
class FtThread extends Thread {
    @Override
    public void run() {
        System.out.println(Thread.currentThread());
    }    
}
```

### 线程池
> 线程池概念较多，后期引入一章单独说明。
>> 优点
* 对于执行大量一样功能的多线程可使用线程池，线程可复用，提升性能。
>> 缺点
* 概念上比线程复杂，引入了阻塞队列，核心线程，最大线程以及丢弃策略。


# javaBase
java 基础

## 多线程
* Runnable
* 继承Thread
* 线程池

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
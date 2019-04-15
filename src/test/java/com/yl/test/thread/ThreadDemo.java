package com.yl.test.thread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Alex
 * @since 2019/4/12 16:23
 */
public class ThreadDemo {

    private static Lock lock = new ReentrantLock();

    public static void main(String[] args) throws Exception{

        Thread t1 = new Thread(() -> {
            System.err.println("t1子线程进来了");
            int i = 0;
            LockSupport.park();
            while (true){
                System.err.println(i++);
                if(i > 10){
                    Thread.currentThread().interrupt();
                }
                System.err.println(Thread.currentThread().isInterrupted());
                if(Thread.currentThread().isInterrupted()){
                    System.err.println("t1子线程出来了");
                    return;
                }
            }
        });

        Thread t2 = new Thread(() -> {
            System.err.println("t2子线程进来了");
            int i = 0;
            LockSupport.park();
            while (true){
                System.err.println(i++);
                if(i > 10){
                    Thread.currentThread().interrupt();
                }
                System.err.println(Thread.currentThread().isInterrupted());
                if(Thread.currentThread().isInterrupted()){
                    System.err.println("t2子线程出来了");
                    return;
                }
            }

        });

        t1.start();
        t2.start();
        Thread.sleep(5*1000);
        System.err.println("主线程休眠结束");
        LockSupport.unpark(t1);
        LockSupport.unpark(t2);
        t1.join();
        t2.join();
    }

}

package com.yl.test.thread;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                System.err.println("t1:" + i++);
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
                System.err.println("t2:" + i++);
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

    @Test
    public void demo()throws Exception{

        String s1 = new String("你好".getBytes("UTF-8"),"GBK");

        String s2 = new String(s1.getBytes("GBK"),"UTF-8");

        System.err.println(s2);

        System.err.println(1 << 2);
    }

    @Test
    public void demo_1(){

        byte[] bytes = new byte[10];

        byte b1 = bytes[1];

        System.err.println(bytes[1]);

    }

    @Test
    public void demo_2(){
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> m1 = new HashMap<>();
        m1.put("name", "miss");
        m1.put("age", "18");
        list.add(m1);

        Map<String,Object> m2 = new HashMap<>();
        m2.put("name", "zack");
        m2.put("age", "30");
        list.add(m2);

        System.err.println(list.toString());

        Integer i = 100;
        System.err.println(i.toString());

    }

    @Test
    public void demo_3(){
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);

        System.err.println(Lists.partition(list, 3));

    }

    @Test
    public void demo_4(){

        try {

            try {
                int i = 1/0;
            }catch (Exception e){
                System.err.println("里面catch");
            }

        }catch (Exception e){
            System.err.println("外面catch");
        }

    }
}

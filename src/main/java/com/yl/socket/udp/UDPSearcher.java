package com.yl.socket.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Alex
 * @since 2019/4/1 21:00
 */
public class UDPSearcher {

    public static void demo(String[] args) throws Exception{
        System.err.println("UDPSearcher Started...");

        // 作为搜索方,端口由系统指定
        DatagramSocket ds = new DatagramSocket();

        // 发送消息
        DatagramPacket requestPack = new DatagramPacket("hello world".getBytes(), "hello world".getBytes().length);
        requestPack.setAddress(InetAddress.getLocalHost());
        requestPack.setPort(20000);
        // 发送数据
        ds.send(requestPack);

        // 构建接收实体
        final byte[] buf = new byte[512];
        DatagramPacket recievePack = new DatagramPacket(buf, buf.length);

        // 接收消息
        ds.receive(recievePack);

        // 获取发送者的ip、port
        String hostAddress = recievePack.getAddress().getHostAddress();
        int port = recievePack.getPort();

        // 接收消息
        String data  = new String(recievePack.getData(), 0, recievePack.getLength());
        System.err.println("UDPSearcher recieve from ip:" + hostAddress + ",port:" +
            port + ",data:" + data);

        System.err.println("UDPSearcher Finished...");
        ds.close();
    }

    private static final int LISTEN_PORT = 30000;

    public static void main(String[] args) throws Exception{
        // 监听
        Listener listener = listen();

        // 发送广播
        sendBroadcast();

        System.in.read();

        System.err.println(listener.devices);

    }

    // 发送广播
    private static void sendBroadcast()throws Exception{
        System.err.println("UDPSearcher sendBroadcast started...");
        // 作为搜索方,端口由系统指定
        DatagramSocket ds = new DatagramSocket();

        // 发送消息
        String message = MessageCreator.buildWithPort(LISTEN_PORT);
        DatagramPacket requestPack = new DatagramPacket(message.getBytes(), message.getBytes().length);
        // 20000端口,广播地址
        requestPack.setAddress(InetAddress.getByName("255.255.255.255"));
        requestPack.setPort(20000);
        // 发送数据
        ds.send(requestPack);
        ds.close();
        System.err.println("UDPSearcher sendBroadcast finished...");
    }

    // 监听消息回送
    public static Listener listen() throws Exception{
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Listener listener = new Listener(LISTEN_PORT, countDownLatch);
        listener.start();
        countDownLatch.await();
        return listener;
    }

    private static class Device {
        private String host;

        private int port;

        private String sn;

        public Device(String host,int port,String sn){
            this.port = port;
            this.host = host;
            this.sn = sn;
        }

        @Override
        public String toString() {
            return "Device{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", sn='" + sn + '\'' +
                '}';
        }
    }

    private static class Listener extends Thread {
        private final int listenPort;
        private final CountDownLatch latch;
        private final List<Device> devices = new ArrayList<>();
        private boolean done =false;
        private DatagramSocket ds = null;

        private Listener(int listenPort, CountDownLatch latch) {
            super();
            this.listenPort = listenPort;
            this.latch = latch;
        }

        @Override
        public void run() {
            super.run();
            try {
                System.err.println("UDPSearcher Listener Started...");
                ds = new DatagramSocket(listenPort);
                latch.countDown();
                while (!done){
                    final byte[] buf = new byte[512];
                    DatagramPacket receivePack = new DatagramPacket(buf, buf.length);
                    // 接收消息
                    ds.receive(receivePack);
                    // 获取发送者的ip、port
                    String hostAddress = receivePack.getAddress().getHostAddress();
                    int port = receivePack.getPort();
                    // 接收消息
                    String data  = new String(receivePack.getData(), 0, receivePack.getLength());
                    System.err.println("UDPSearcher receive from ip:" + hostAddress + ",port:" +
                        port + ",data:" + data);

                    String sn = MessageCreator.parseSn(data);
                    if (sn != null){
                        devices.add(new Device(hostAddress, port, sn));
                    }
                }
            }catch (Exception e){
            }finally {
                close();
            }
            System.err.println("UDPSearcher Listener finished...");
        }

        private void close(){
            if(ds != null){
                ds.close();
                ds = null;
            }
        }

        List<Device> getDevicesAndClose(){
            done = true;
            close();
            return this.devices;
        }

    }

}

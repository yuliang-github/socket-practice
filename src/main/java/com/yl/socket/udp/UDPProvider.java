package com.yl.socket.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.UUID;

/**
 * @author Alex
 * @since 2019/4/1 21:00
 */
public class UDPProvider {

    public static void demo(String[] args) throws Exception{
        System.err.println("UDPProvider Started...");

        // 作为接收者,指定一个端口用于数据接收
        DatagramSocket ds = new DatagramSocket(20000);

        // 构建接收实体
        final byte[] buf = new byte[512];
        DatagramPacket receivePack = new DatagramPacket(buf, buf.length);

        // 接收消息
        ds.receive(receivePack);

        // 获取发送者的ip、port
        String hostAddress = receivePack.getAddress().getHostAddress();
        int port = receivePack.getPort();

        // 接收消息
        String data  = new String(receivePack.getData(), 0, receivePack.getLength());
        System.err.println("UDPProvider recieve from ip:" + hostAddress + ",port:" +
            port + ",data:" + data);

        // 回写数据
        String responseData = "Receive data with len:" + receivePack.getLength();
        DatagramPacket responsePack = new DatagramPacket(responseData.getBytes(), responseData.getBytes().length
            , receivePack.getAddress(), receivePack.getPort());
        ds.send(responsePack);

        System.err.println("UDPProvider Finished...");
        ds.close();
    }

    public static void main(String[] args) throws Exception{
        String sn = UUID.randomUUID().toString();

        Provider provider = new Provider(sn);
        provider.start();

        System.in.read();
        provider.exit();
    }


    private static class Provider extends Thread {
        private final String sn;
        private boolean done = false;
        private DatagramSocket ds = null;

        public Provider(String sn) {
            super();
            this.sn = sn;
        }

        @Override
        public void run() {
            super.run();
            System.err.println("UDPProvider Started...");
            try {
                // 监听20000端口
                ds = new DatagramSocket(20000);
                while (!done){
                    final byte[] buf = new byte[512];
                    DatagramPacket receivePack = new DatagramPacket(buf, buf.length);

                    // 接收消息
                    ds.receive(receivePack);

                    // 接收消息
                    String data  = new String(receivePack.getData(), 0, receivePack.getLength());
                    System.err.println("UDPProvider receive from ip:" + receivePack.getAddress() + ",port:" +
                        receivePack.getPort() + ",data:" + data);

                    // 解析回电端口
                    int port = MessageCreator.parsePort(data);
                    if(port != -1){
                        // 回写数据
                        String responseData = MessageCreator.buildWithSn(sn);
                        DatagramPacket responsePack = new DatagramPacket(responseData.getBytes(), responseData.getBytes().length
                            , receivePack.getAddress(), port);
                        ds.send(responsePack);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                close();
            }
        }

        private void close(){
            if(ds != null){
                ds.close();
                ds = null;
            }
        }

        void exit(){
            done = true;
            close();
        }
    }

}

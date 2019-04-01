package com.yl.socket.bio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author Alex
 * @since 2019/3/30 18:11
 */
public class Server {

    public static void main(String[] args) throws Exception{
        ServerSocket server = new ServerSocket(8083);
        System.err.println("服务端已启动....");
        System.err.println("服务端信息:" + server.getInetAddress() + ",P:" + server.getLocalPort());

        // 接收连接
        for(;;){
            new SocketHandler(server.accept()).start();
        }
    }

    private static  class SocketHandler extends Thread {
        private Socket socket;
        private boolean flag = true;
        private LongAdder longAdder = new LongAdder();
        public SocketHandler(Socket socket){
            this.socket = socket;
        }
        @Override
        public void run() {
            System.err.println("接收到连接:" + socket.getInetAddress()+",P:" + socket.getPort());
            try {
                // 获取客户端发送的数据
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintStream printStream = new PrintStream(socket.getOutputStream());
                do {
                    String getInfo = input.readLine();
                    if("bye".equalsIgnoreCase(getInfo)){
                        flag = false;
                        printStream.println("bye");
                    }else {
                        longAdder.increment();
                        printStream.println("第" + longAdder.intValue() + "次给你响应");
                    }
                    System.err.println("接收到的客户端:"+socket.getPort()+"的数据:" + getInfo);
                }while (flag);

            }catch (Exception e){
                System.err.println("连接异常关闭:" + e);
            }finally {
                try {
                    socket.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

}

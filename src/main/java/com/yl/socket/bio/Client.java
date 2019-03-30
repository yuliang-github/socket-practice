package com.yl.socket.bio;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author Alex
 * @since 2019/3/30 18:11
 */
public class Client {

    public static void main(String[] args) throws Exception{
        Socket client = new Socket();
        // 超时时间
        client.setSoTimeout(10000);

        // 连接服务端,连接超时时间3000
        client.connect(new InetSocketAddress(Inet4Address.getLocalHost(), 8083), 3000);

        System.err.println("开始连接服务端...");

        try {
            send(client);
        }catch (Exception e){
            System.err.println("出现异常:" + e);
        }
        client.close();
        System.err.println("客户端退出...");
    }

    private static void send(Socket client) throws Exception{
        BufferedReader print = new BufferedReader(new InputStreamReader(System.in));
        PrintStream request = new PrintStream(client.getOutputStream());
        // 得到socket输出流
        BufferedReader response = new BufferedReader(new InputStreamReader(client.getInputStream()));
        boolean flag = true;
        do {
            // 获取键盘输入内容
            String sendInfo = print.readLine();
            // 发送数据
            request.println(sendInfo);
            // 获取服务端响应数据
            String res = response.readLine();
            if("bye".equalsIgnoreCase(res)){
                flag = false;
            }
            System.err.println(res);
        }while (flag);

    }

}

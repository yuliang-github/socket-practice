package com.yl.socket.bio;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
//        client.setSoTimeout(10000);

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
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        // 得到socket输出流
        boolean flag = true;
        do {
            // 获取键盘输入内容
            String sendInfo = input.readLine();
            // 发送数据
            client.getOutputStream().write(sendInfo.getBytes());

            // 获取服务端响应数据
            InputStream socketInput = client.getInputStream();
            byte[] bytes = new byte[1024];
            StringBuilder sb = new StringBuilder("服务端响应数据:");
            int len = 0;
            while ((len = socketInput.read(bytes)) > 0){
                sb.append(new String(bytes,0,len));
            }
            if("bye".equalsIgnoreCase(sb.toString())){
                flag = false;
            }else{
                System.err.println(sb.toString());
            }
        }while (flag);

    }

}

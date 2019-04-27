package com.yl.socket.tcp.nio;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 基于JAVA NIO的服务端
 * @author alex
 */
@Slf4j
public class NioServer {

    private static final DateFormat SDF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private int port;

    // 读取连接数据的线程
    private ExecutorService read = null;
    // 处理数据业务的线程
    private ExecutorService worker = null;

    public NioServer(int port,int activeConnect,int wokerCount){
        this.port = port;
        read = Executors.newFixedThreadPool(activeConnect);
        worker = Executors.newFixedThreadPool(wokerCount);
    }


    public void start(){
        log.info("begin start nio server in port:"+port);
        ServerSocketChannel serverChannel = null;
        try {
            // 开启服务端
            serverChannel = ServerSocketChannel.open();
            // 显示标识非阻塞
            serverChannel.configureBlocking(false);
            // 绑定端口
            serverChannel.bind(new InetSocketAddress(port));

            /*
             * 开启选择器
             * 1.作用：接收客户端连接
             * 2.效果：可以实现一个线程处理多个网络连接
             * 3.底层实现：JVM对擦偶偶系统底层‘多路复用/事件驱动’机制的封装
             */
            final Selector selector = Selector.open();

            // 服务器注册选择器 让选择器接收新连接并建立ACCEPT类型的通知
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            // 不断接收新连接
            while (true) {
                int count = selector.select();
                log.info("nio server port:"+port+" accept "+count+" num socket in time:"+SDF.format(new Date()));
                // 获取具体连接内容 包含ACCEPT新连接、READ客户端传输的数据
                Set<SelectionKey> results = selector.selectedKeys();
                for (SelectionKey result : results) {
                    if(result.isAcceptable()){
                        // 表示是新连接 未开始传递数据 可作为空连接处理 -> 不开线程处理
                        /*
                         * 获取新连接
                         * 与传统BIO不同之处在于 accept不会阻塞
                         * 因为是在selector发送新连接通知时才去获取新连接
                         */
                        final SocketChannel socketChannel = serverChannel.accept();
                        dealAcceptableConnect(socketChannel,selector);
                    }else if(result.isReadable()){
                        // 表示连接有传递数据
                        // 此步骤标识正在处理当前连接的数据、在数据处理完成之前 不再接受该通道的通知
                        // 此时客户端传递的数据会存放在操作系统的TCP缓冲区
                        SocketChannel socketChannel = (SocketChannel) result.channel();
//						result.cancel();
                        socketChannel.configureBlocking(false);
                        dealReadableConnect(socketChannel, selector);
                    }
                    // 清空 重新开始处理连接
                    results.clear();
                    selector.selectNow();
                }
            }
        } catch (Exception e) {
            log.error("--nio server exception--",e);
        }
    }

    private void dealAcceptableConnect(SocketChannel channel, Selector selector) throws Exception{
        // 显示标识为非阻塞连接
        channel.configureBlocking(false);
        /*
         * 新连接注册选择器 因为接收的新连接不会开启线程去处理
         * 故需要选择器建立该连接的READ类型的通知
         * 一旦该连接传输数据过来、selector就会发送READ数据的通知给服务端
         */
        channel.register(selector, SelectionKey.OP_READ);
    }

    private void dealReadableConnect(SocketChannel channel, Selector selector){
        /*
         * 创建新线程 获取连接数据
         * 注意：该线程应只作为获取数据只用、具体的业务操作应放在其它线程处理
         */
        log.info("nio server start a runable");
        Runnable r = new Runnable() {
            @Override
            public void run() {
                log.info("--nio server start deal data--");
                ByteBuffer buf = null;
                try {
                    buf = ByteBuffer.allocate(1024);
                    StringBuilder sb = new StringBuilder();
                    channel.read(buf);
                    /*
                     * 将ByteBuffer从写模式转成读模式
                     */
                    buf.flip();
                    sb.append(new String(buf.array(),"UTF-8"));
                    buf.compact();
                    buf.clear();
                    // 创建线程处理具体业务逻辑
                    WorkerTask task = new WorkerTask(channel, sb.toString());
                    worker.submit(task);
                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_READ);
                } catch (Exception e) {
                    log.error("--nio server deal readable connet exception--", e);
                }finally{
                    buf.clear();
                }
                log.info("nio server end a runable");
            }
        };
        read.submit(r);
    }

    private class WorkerTask implements Runnable{

        private SocketChannel channel;
        private String content;

        WorkerTask(SocketChannel channel,String content){
            this.channel = channel;
            this.content = content;
        }

        @Override
        public void run() {
            log.info("--worker deal data--");
            System.err.println(content);
            ByteBuffer buf = null;
            try {
                buf = ByteBuffer.wrap("deal over".getBytes("UTF-8"));
                channel.write(buf);
            } catch (Exception e) {
                log.error("--worker deal date exception--",e);
            }finally{
                if(buf != null){
                    buf.clear();
                }
            }
            log.info("--worker deal data end--");
        }
    }
}

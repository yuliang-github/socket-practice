package com.yl.socket.tcp.netty.time;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author Alex
 * @since 2019/5/8 11:02
 */
@Slf4j
public class TimeServer {

    /**
     * 关于Netty是如何解决Tcp粘/拆包的问题？
     *  1.TCP粘/拆包是如何产生的？
     *      1.由Nagle算法造成的发送端的粘包:Nagle算法是一种改善网络传输效率的算法.简单的说,当我们提交一段数据给TCP发送时
     *        TCP并不立刻发送此段数据,而是等待一小段时间,看看在等待期间是否还有要发送的数据,若有则会一次把这两段数据发送出
     *        去(若发送端TCP缓冲区满了,则会立即发送)
     *      2.接收端接收不及时造成的接收端粘包:TCP会把接收到的数据存在自己的缓冲区中,然后通知应用层取数据.当应用层由于某些原
     *        因不能及时的把TCP的数据取出来,就会造成TCP缓冲区中存放了几段数据
     *  2.Netty是如何解决的呢？
     *      1.Netty提供了一系列解码器来进行消息解码
     *          1.LineBasedFrameDecoder解码器:以换行符为结束标志的解码器。
     *              注意:参数为最大消息长度,若超过该长度还没找到结束标识,则抛出异常,防止消息过长导致内存溢出的补偿策略。
     *          2.DelimiterBasedFrameDecoder解码器:支持自定义结束标识的解码器。
     *              注意:参数1为消息最大长度,防止内存溢出的补偿策略,参数2为自定义的结束标识字符。
     *          3.FixedLengthBasedFrameDecoder解码器:固定长度的解码器,每次读取相同长度的字节数组作为一个消息。
     *              注意:参数为固定的消息长度。
     */

    public static void main(String[] args) {
        start(9091);
    }

    public static void start(int port){
        EventLoopGroup boss = null;
        EventLoopGroup worker = null;
        try {
            boss = new NioEventLoopGroup(1);
            worker = new NioEventLoopGroup();
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                        socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            int counter = 0;
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf)msg;
                                byte[] bytes = new byte[buf.readableBytes()];
                                buf.readBytes(bytes);
                                String body = new String(bytes,"UTF-8")
                                    .substring(0, bytes.length - System.getProperty("line.separator").length()+1);
                                System.err.println("server receive msg[" + body + "]; counter is[" + (++counter) + "]。");
                                String response = "query time".equalsIgnoreCase(body)?new Date().toString()  :"bad request";
                                response += System.getProperty("line.separator");
                                ctx.writeAndFlush(Unpooled.copiedBuffer(response.getBytes()));
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                log.error("server connect error", cause);
                                ctx.close();
                            }
                        });
                    }
                });

            ChannelFuture future = sb.bind(port).sync();
            future.channel().closeFuture().sync();
        }catch (Exception e){
            log.error("Exception occurred", e);
        }finally {
            if(boss != null){
                boss.shutdownGracefully();
            }
            if(worker != null){
                worker.shutdownGracefully();
            }
        }

    }
}

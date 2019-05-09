package com.yl.socket.tcp.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Alex
 * @since 2019/5/8 11:32
 */
@Slf4j
public class TimeClient {

    public static void main(String[] args) {
        start(9091);
    }

    public static void start(int port){
        EventLoopGroup group = null;
        try {
            group = new NioEventLoopGroup(1);
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                        socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            int counter = 0;
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                for(int i=0;i<100;i++){
                                    ByteBuf request = Unpooled.copiedBuffer(("query time"
                                        + System.getProperty("line.separator")).getBytes("UTF-8"));
                                    ctx.writeAndFlush(request);
                                }
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf res = (ByteBuf)msg;
                                byte[] bytes = new byte[res.readableBytes()];
                                res.readBytes(bytes);
                                String body = new String(bytes,"UTF-8");

                                System.err.println("client receive msg[" + body + "]; counter is[" + (++counter) + "]。");

                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                log.error("client connect error", cause);
                                ctx.close();
                            }
                        });
                    }

                });

            ChannelFuture future = b.connect("127.0.0.1", port).sync();
            future.channel().closeFuture().sync();

        }catch (Exception e){
            log.error("Exception occurred", e);
        }finally {
            if(group != null){
                group.shutdownGracefully();
            }
        }

    }
}

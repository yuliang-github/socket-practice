package com.yl.socket.tcp.netty.Product;

import com.yl.socket.tcp.netty.MarshallingCodeCFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * @author Alex
 * @since 2019/5/10 13:56
 */
@Slf4j
public class ProductServer {

    /**
     * 本例程用于演示Netty中自带的对象解码器、以及jboss.marshalling提供的对象解码器
     * 传输的对象必须实现java.io.Serializable序列化接口
     */


    public static void main(String[] args) {
        start(9091);
    }

    private static void start(int port){

        EventLoopGroup boss = null;
        EventLoopGroup worker = null;
        try {
            boss = new NioEventLoopGroup(1);
            worker = new NioEventLoopGroup();

            ServerBootstrap sb = new ServerBootstrap();
            sb.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        /*
                          对象解码器:对序列化的对象进行解码,参数1:码流不得超过1M
                          参数2:创建线程安全的WeakReferenceMap缓存类加载器
                          */
                        //ch.pipeline().addLast(new ObjectDecoder(1024*1024,
                        //    ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                        // 对象编码器:发送消息时将对象序列化为二进制码
                        //ch.pipeline().addLast(new ObjectEncoder());

                        // 使用JBoss的Marshalling序列化方式
                        ch.pipeline().addLast(MarshallingCodeCFactory.buildDecoder());
                        ch.pipeline().addLast(MarshallingCodeCFactory.buildEncoder());

                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            Random random = new Random();
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ProductRequest request = (ProductRequest) msg;
                                System.err.println("ProductServer 收到商品订单:[" + request + "],开始处理...");

                                ctx.writeAndFlush(new ProductResponse(0, random.nextInt(), request.getUserName()+ "您好,下单成功。"));
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
            if(boss != null){boss.shutdownGracefully();}
            if(worker != null){worker.shutdownGracefully();}
        }


    }

}

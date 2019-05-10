package com.yl.socket.tcp.netty.Product;

import com.yl.socket.tcp.netty.MarshallingCodeCFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * @author Alex
 * @since 2019/5/10 13:56
 */
@Slf4j
public class ProductClient {

    public static void main(String[] args) {
        connect("127.0.0.1", 9091);
    }

    private static void connect(String host,int port){
        EventLoopGroup group = null;
        try {
            group = new NioEventLoopGroup(1);

            Bootstrap b = new Bootstrap();
            b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        /**
                         * 对象解码器,负责将收到的二进制码流序列化成对象
                         * cacheDisabled表示不进行类加载器的缓存
                         */
                        //ch.pipeline().addLast(new ObjectDecoder(1024*1024,
                        //   ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
                        // 对象编码器,负责将发送的对象序列化成二进制码流进行发送
                        //ch.pipeline().addLast(new ObjectEncoder());

                        // 使用JBoss的Marshalling序列化方式
                        ch.pipeline().addLast(MarshallingCodeCFactory.buildDecoder());
                        ch.pipeline().addLast(MarshallingCodeCFactory.buildEncoder());

                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                Random random = new Random();
                                for(int i=0;i<10;i++){
                                    ctx.write(new ProductRequest(random.nextInt(), "春树", "龙猫公仔"));
                                }
                                // 统一发送
                                ctx.flush();
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ProductResponse response = (ProductResponse)msg;
                                System.err.println("ProductClient 收到订单应答:[" + response + "]。");
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                log.error("client connect error", cause);
                                ctx.close();
                            }
                        });

                    }
                });

            ChannelFuture future = b.connect(host, port).sync();
            future.channel().closeFuture().sync();

        }catch (Exception e){
            log.error("Exception occurred", e);
        }finally {
            if(group != null){group.shutdownGracefully();}
        }

    }

}

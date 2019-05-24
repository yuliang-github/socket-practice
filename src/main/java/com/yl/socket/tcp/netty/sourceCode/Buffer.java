package com.yl.socket.tcp.netty.sourceCode;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * @author Alex
 * @since 2019/5/17 17:43
 */
public class Buffer {

    public static void main(String[] args) throws Exception{

        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.buffer(1024 * 1024);
        buffer.writeInt(10);

        ByteBuf buf = ByteBufUtil.encodeString(new UnpooledByteBufAllocator(false), CharBuffer.wrap("中华人民共和国"), Charset.forName("UTF-8"));

        System.err.println(buf.getClass().getName());

        byte[] bytes = ByteBufUtil.getBytes(buf);

        System.err.println(new String(bytes,"UTF-8"));

    }

}

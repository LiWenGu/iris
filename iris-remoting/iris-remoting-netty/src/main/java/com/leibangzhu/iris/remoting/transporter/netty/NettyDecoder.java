package com.leibangzhu.iris.remoting.transporter.netty;

import com.leibangzhu.coco.ExtensionLoader;
import com.leibangzhu.iris.serialization.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class NettyDecoder extends ByteToMessageDecoder {
    private Class<?> clazz;

    public NettyDecoder(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 4) {
            return;
        }
        byteBuf.markReaderIndex();
        int dataLength = byteBuf.readInt();
        /*if (dataLength <= 0) {
            ctx.close();
        }*/
        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);

        Object obj = ExtensionLoader.getExtensionLoader(Serialization.class).getDefaultExtension().deserialize(data, clazz);
        list.add(obj);
    }
}

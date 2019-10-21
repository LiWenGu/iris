package com.leibangzhu.iris.remoting.netty;

import com.leibangzhu.coco.ExtensionLoader;
import com.leibangzhu.iris.serialization.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyEncoder extends MessageToByteEncoder {
    private Class<?> clazz;

    public NettyEncoder(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if (clazz.isInstance(o)){
            byte[] data = ExtensionLoader.getExtensionLoader(Serialization.class).getDefaultExtension().serialize(o);
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);
        }
    }
}

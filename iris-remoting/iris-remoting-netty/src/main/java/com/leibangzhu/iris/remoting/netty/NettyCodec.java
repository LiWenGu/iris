package com.leibangzhu.iris.remoting.netty;

import com.leibangzhu.coco.ExtensionLoader;
import com.leibangzhu.iris.remoting.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.List;

public class NettyCodec {

    static Codec codec = ExtensionLoader.getExtensionLoader(Codec.class).getDefaultExtension();

    private final ChannelHandler encoder = new InternalEncoder();

    private final ChannelHandler decoder = new InternalDecoder();


    public ChannelHandler getEncoder() {
        return encoder;
    }

    public ChannelHandler getDecoder() {
        return decoder;
    }

    public static class InternalEncoder extends MessageToByteEncoder {

        @Override
        protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
            codec.encode(ctx, msg, out);
        }
    }

    public static class InternalDecoder extends ByteToMessageDecoder {

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf input, List<Object> out) throws Exception {
            codec.decode(ctx, input, out);
        }
    }
}

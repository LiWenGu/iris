package com.leibangzhu.iris.remoting.netty;

import com.leibangzhu.iris.remoting.Codec;
import com.leibangzhu.iris.remoting.URL;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.List;

public class NettyCodecAdapter {

    private Codec codec;

    private final URL url;

    private final com.leibangzhu.iris.remoting.ChannelHandler handler;

    private final ChannelHandler encoder = new InternalEncoder();

    private final ChannelHandler decoder = new InternalDecoder();


    public NettyCodecAdapter(Codec codec, URL url, com.leibangzhu.iris.remoting.ChannelHandler handler) {
        this.codec = codec;
        this.url = url;
        this.handler = handler;
    }

    public ChannelHandler getEncoder() {
        return encoder;
    }

    public ChannelHandler getDecoder() {
        return decoder;
    }

    private class InternalEncoder extends MessageToByteEncoder {

        @Override
        protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
            Channel ch = ctx.channel();
            NettyChannel channel = NettyChannel.getOrAddChannel(ch, url, handler);
            codec.encode(channel, msg, out);
        }
    }

    private class InternalDecoder extends ByteToMessageDecoder {

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf input, List<Object> out) throws Exception {
            Channel ch = ctx.channel();
            NettyChannel channel = NettyChannel.getOrAddChannel(ch, url, handler);
            codec.decode(channel, input, out);
        }
    }
}

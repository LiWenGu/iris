package com.leibangzhu.iris.remoting.netty;

import com.leibangzhu.iris.remoting.URL;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClientHandler extends ChannelDuplexHandler {

    private final URL url;

    private final com.leibangzhu.iris.remoting.ChannelHandler handler;

    public NettyClientHandler(URL url, com.leibangzhu.iris.remoting.ChannelHandler handler) {
        if (url == null) {
            throw new IllegalArgumentException("url == null");
        }
        if (handler == null) {
            throw new IllegalArgumentException("handler == null");
        }
        this.url = url;
        this.handler = handler;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        try {
            handler.connected(channel);
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }

        if (log.isInfoEnabled()) {
            log.info("The connection of " + channel.getLocalAddress() + " -> " + channel.getRemoteAddress() + " is established.");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel(), url, handler);
        try {
            handler.caught(channel, cause);
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

}

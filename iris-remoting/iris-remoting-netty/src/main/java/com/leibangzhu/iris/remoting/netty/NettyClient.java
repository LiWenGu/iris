package com.leibangzhu.iris.remoting.netty;

import com.leibangzhu.iris.remoting.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.net.InetSocketAddress;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class NettyClient implements Client, ChannelHandler {

    private Bootstrap bootstrap;

    private static final NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(Constants.DEFAULT_IO_THREADS, new DefaultThreadFactory("NettyClientWorker", true));

    private final ChannelHandler handler;

    private volatile URL url;

    public NettyClient(ChannelHandler handler, URL url) {
        this.handler = handler;
        this.url = url;
    }

    private URL getUrl() {
        return url;
    }

    @Override
    public void doOpen() throws Throwable {
        final NettyClientHandler nettyClientHandler = new NettyClientHandler(getUrl(), this);
        bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class);

        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Math.max(3000, url.getNumbers().getOrDefault(Constants.CONNECT_TIMEOUT_KEY, Constants.DEFAULT_CONNECT_TIMEOUT)));
        bootstrap.handler(new ChannelInitializer() {

            @Override
            protected void initChannel(Channel ch) throws Exception {
                int heartbeatInterval = url.getNumbers().getOrDefault(Constants.HEARTBEAT_KEY, Constants.DEFAULT_HEARTBEAT);
                NettyCodecAdapter codec = new NettyCodecAdapter(CodecUtil.getCodec(url), url, NettyClient.this);
                ch.pipeline()//.addLast("logging",new LoggingHandler(LogLevel.INFO))//for debug
                        .addLast("decoder", codec.getDecoder())
                        .addLast("encoder", codec.getEncoder())
                        .addLast("client-idle-handler", new IdleStateHandler(heartbeatInterval, 0, 0, MILLISECONDS))
                        .addLast("handler", nettyClientHandler);
                String socksProxyHost = ConfigUtils.getProperty(SOCKS_PROXY_HOST);
                if (socksProxyHost != null) {
                    int socksProxyPort = Integer.parseInt(ConfigUtils.getProperty(SOCKS_PROXY_PORT, DEFAULT_SOCKS_PROXY_PORT));
                    Socks5ProxyHandler socks5ProxyHandler = new Socks5ProxyHandler(new InetSocketAddress(socksProxyHost, socksProxyPort));
                    ch.pipeline().addFirst(socks5ProxyHandler);
                }
            }
        });
    }

    @Override
    public void doConnect() throws Throwable {
        long start = System.currentTimeMillis();
        ChannelFuture future = bootstrap.connect(getConnectAddress());
        try {
            boolean ret = future.awaitUninterruptibly(getConnectTimeout(), MILLISECONDS);

            if (ret && future.isSuccess()) {
                Channel newChannel = future.channel();
                try {
                    // Close old channel
                    // copy reference
                    Channel oldChannel = NettyClient.this.channel;
                    if (oldChannel != null) {
                        try {
                            if (logger.isInfoEnabled()) {
                                logger.info("Close old netty channel " + oldChannel + " on create new netty channel " + newChannel);
                            }
                            oldChannel.close();
                        } finally {
                            NettyChannel.removeChannelIfDisconnected(oldChannel);
                        }
                    }
                } finally {
                    if (NettyClient.this.isClosed()) {
                        try {
                            if (logger.isInfoEnabled()) {
                                logger.info("Close new netty channel " + newChannel + ", because the client closed.");
                            }
                            newChannel.close();
                        } finally {
                            NettyClient.this.channel = null;
                            NettyChannel.removeChannelIfDisconnected(newChannel);
                        }
                    } else {
                        NettyClient.this.channel = newChannel;
                    }
                }
            } else if (future.cause() != null) {
                throw new RemotingException(this, "client(url: " + getUrl() + ") failed to connect to server "
                        + getRemoteAddress() + ", error message is:" + future.cause().getMessage(), future.cause());
            } else {
                throw new RemotingException(this, "client(url: " + getUrl() + ") failed to connect to server "
                        + getRemoteAddress() + " client-side timeout "
                        + getConnectTimeout() + "ms (elapsed: " + (System.currentTimeMillis() - start) + "ms) from netty client "
                        + NetUtils.getLocalHost() + " using dubbo version " + Version.getVersion());
            }
        } finally {
            // just add new valid channel to NettyChannel's cache
            if (!isConnected()) {
                //future.cancel(true);
            }
        }
    }

    @Override
    public com.leibangzhu.iris.remoting.Channel getChannel() {
        return null;
    }

    @Override
    public void connected(com.leibangzhu.iris.remoting.Channel channel) throws RemotingException {

    }

    @Override
    public void disconnected(com.leibangzhu.iris.remoting.Channel channel) throws RemotingException {

    }

    @Override
    public void sent(com.leibangzhu.iris.remoting.Channel channel, Object message) throws RemotingException {

    }

    @Override
    public void received(com.leibangzhu.iris.remoting.Channel channel, Object message) throws RemotingException {

    }

    @Override
    public void caught(com.leibangzhu.iris.remoting.Channel channel, Throwable exception) throws RemotingException {

    }
}

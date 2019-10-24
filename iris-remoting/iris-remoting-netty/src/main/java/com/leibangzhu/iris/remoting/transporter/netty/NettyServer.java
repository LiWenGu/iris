package com.leibangzhu.iris.remoting.transporter.netty;

import com.leibangzhu.iris.core.NameThreadFactory;
import com.leibangzhu.iris.registry.Registry;
import com.leibangzhu.iris.registry.RegistryTypeEnum;
import com.leibangzhu.iris.remoting.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class NettyServer implements Server {

    private Registry registry;
    private int port = 2017;
    private Map<String, Object> handlerMap = new LinkedHashMap<>();
    private Channel channel;
    ServerBootstrap bootstrap;
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    @Override
    public void init(Registry registry, int port) {
        this.registry = registry;
        this.port = port;
    }

    @Override
    public void export(Class<?> clazz, Object handler) throws Exception {

        handlerMap.put(clazz.getName(), handler);
        registry.keepAlive();
        for (String className : handlerMap.keySet()) {
            try {
                registry.register(className, port, RegistryTypeEnum.providers);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        registry.keepAlive();
        Executors.newSingleThreadExecutor(new NameThreadFactory("rpc-server")).submit(() -> {

            bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new NettyCodec.InternalDecoder())
                                    .addLast(new NettyCodec.InternalEncoder())
                                    .addLast(new NettyServerHandler(handlerMap));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = bootstrap.bind(port);
            channelFuture.syncUninterruptibly();
            channel = channelFuture.channel();
        });
    }

    @Override
    public void destory() {
        if (channel != null) {
            channel.close();
        }
        if (bootstrap != null) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


}

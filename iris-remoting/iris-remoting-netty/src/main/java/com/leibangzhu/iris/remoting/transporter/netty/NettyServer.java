package com.leibangzhu.iris.remoting.transporter.netty;

import com.leibangzhu.iris.core.NameThreadFactory;
import com.leibangzhu.iris.registry.IRegistry;
import com.leibangzhu.iris.registry.RegistryTypeEnum;
import com.leibangzhu.iris.remoting.RpcRequest;
import com.leibangzhu.iris.remoting.RpcResponse;
import com.leibangzhu.iris.remoting.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class NettyServer implements Server {

    private String host = "127.0.0.1";
    private IRegistry registry;
    private int port = 2017;

    private Map<String, Object> handlerMap = new LinkedHashMap<>();

    @Override
    public void init(IRegistry registry, int port) {
        this.registry = registry;
        this.port = port;
    }

    @Override
    public void export(Class<?> clazz, Object handler) throws Exception {

        handlerMap.put(clazz.getName(), handler);
//        registry.register(clazz.getName(),port);
        registry.keepAlive();
    }

    @Override
    public void run() {
        registry.keepAlive();
        Executors.newSingleThreadExecutor(new NameThreadFactory("rpc-server")).submit(() -> {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0))
                                    .addLast(new NettyDecoder(RpcRequest.class))
                                    .addLast(new NettyEncoder(RpcResponse.class))
                                    .addLast(new NettyServerHandler(handlerMap));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = null;
            try {
                future = bootstrap.bind(port).sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (String className : handlerMap.keySet()) {
                try {
                    registry.register(className, port, RegistryTypeEnum.providers);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
    }
}

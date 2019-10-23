package com.leibangzhu.iris.remoting.transporter.netty;

import com.leibangzhu.coco.ExtensionLoader;
import com.leibangzhu.iris.core.CollectionUtil;
import com.leibangzhu.iris.core.Endpoint;
import com.leibangzhu.iris.core.IrisConfig;
import com.leibangzhu.iris.core.loadbalance.ILoadBalance;
import com.leibangzhu.iris.registry.IEventCallback;
import com.leibangzhu.iris.registry.Registry;
import com.leibangzhu.iris.registry.RegistryEvent;
import com.leibangzhu.iris.registry.RegistryTypeEnum;
import com.leibangzhu.iris.remoting.ClientChannelWrapper;
import com.leibangzhu.iris.remoting.ClientConnectManager;
import com.leibangzhu.iris.remoting.RpcRequest;
import com.leibangzhu.iris.remoting.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class NettyClientConnectManager implements ClientConnectManager, IEventCallback {

    private Registry registry;
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
    private AtomicInteger roundRobin = new AtomicInteger(0);
    private Map<String, List<ClientChannelWrapper>> channelsByService = new LinkedHashMap<>();

    @Override
    public void registry(Registry registry, List<String> serivceNames) {
        this.registry = registry;
        // 连接前先订阅服务
        for (String serivceName : serivceNames) {
            this.registry.subscribe(serivceName, RegistryTypeEnum.providers, this);
        }
    }

    public Channel getChannel(String serviceName) throws Exception {
        if (!channelsByService.containsKey(serviceName)) {
            List<Endpoint> endpoints = registry.find(serviceName, RegistryTypeEnum.providers);
            List<ClientChannelWrapper> channels = new CollectionUtil.NoDuplicatesList<>();
            for (Endpoint endpoint : endpoints) {
                channels.add(connect(endpoint.getHost(), endpoint.getPort()));
            }
            channelsByService.put(serviceName, channels);
        }

        // select one channel from all available channels
        int size = channelsByService.get(serviceName).size();
        ILoadBalance loadBalance = ExtensionLoader.getExtensionLoader(ILoadBalance.class).getAdaptiveInstance();
        if (0 == size) {
            System.out.println("NO available providers for service: " + serviceName);
            throw new RuntimeException("没有可用的服务提供者！");
        }
        //int index = (roundRobin.getAndAdd(1) + size) % size;
        String loadbalance = IrisConfig.get("iris.loadbalance");
        Map<String, String> map = new LinkedHashMap<>();
        map.put("loadbalance", loadbalance);
        int index = loadBalance.select(map, size);
        ClientChannelWrapper channelWrapper = channelsByService.get(serviceName).get(index);
        log.info("Load balance:" + loadbalance + "; Selected endpoint: " + channelWrapper.toString());
        return channelWrapper.getChannel();
    }

    private ClientChannelWrapper connect(String host, int port) throws Exception {

        Bootstrap b = new Bootstrap()
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new NettyEncoder(RpcRequest.class))
                                .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0))
                                .addLast(new NettyDecoder(RpcResponse.class))
                                .addLast(new NettyClientHandler());
                    }
                });

        Channel channel = b.connect(host, port).sync().channel();
        ClientChannelWrapper channelWrapper = new ClientChannelWrapper(new Endpoint(host, port), channel);
        return channelWrapper;
    }

    @Override
    public void execute(RegistryEvent event) {
        if (event.getEventType() == RegistryEvent.EventType.DELETE) {

            // key:   /iris/com.leibangzhu.iris.bytebuddy.IHelloService/192.168.41.215:2017

            String s = event.getKeyValue().getKey();
            String serviceName = s.split("/")[2];             // com.leibangzhu.iris.bytebuddy.IHelloService
            String endpointStr = s.split("/")[4];

            String host = endpointStr.split(":")[0];          //  192.168.41.215
            int port = Integer.valueOf(endpointStr.split(":")[1]);    // 2017
            if (channelsByService.isEmpty()) {
                return;
            }
            Iterator<ClientChannelWrapper> iterator = channelsByService.get(serviceName).iterator();
            while (iterator.hasNext()) {
                Endpoint endpoint = iterator.next().getEndpoint();
                if (endpoint.getHost().equals(host) && (endpoint.getPort() == port)) {
                    iterator.remove();
                }
            }
        }

        if (event.getEventType() == RegistryEvent.EventType.PUT) {

            // key:   /iris/com.leibangzhu.iris.bytebuddy.IHelloService/192.168.41.215:2017

            String s = event.getKeyValue().getKey();
            String serviceName = s.split("/")[2];             // com.leibangzhu.iris.bytebuddy.IHelloService
            String endpointStr = s.split("/")[4];

            String host = endpointStr.split(":")[0];          //  192.168.41.215
            int port = Integer.valueOf(endpointStr.split(":")[1]);    // 2017

            try {
                channelsByService.get(serviceName).add(connect(host, port));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

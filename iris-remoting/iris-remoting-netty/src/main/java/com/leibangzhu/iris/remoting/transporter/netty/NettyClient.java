package com.leibangzhu.iris.remoting.transporter.netty;

import com.leibangzhu.iris.registry.Registry;
import com.leibangzhu.iris.remoting.Client;
import com.leibangzhu.iris.remoting.ClientConnectManager;
import com.leibangzhu.iris.remoting.RpcInvokeInterceptor;
import io.netty.channel.Channel;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;

import java.util.LinkedHashMap;
import java.util.Map;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;

public class NettyClient implements Client {

    private Map<String, Object> proxyByClass = new LinkedHashMap<>();
    private ClientConnectManager connectManager;
    private Registry registry;


    @Override
    public <T> T ref(Class<T> clazz) {
        connectManager.registry(registry, clazz.getName());
        if (!proxyByClass.containsKey(clazz.getName())) {
            try {
                T proxy = new ByteBuddy()
                        .subclass(clazz)
                        .method(isDeclaredBy(clazz)).intercept(MethodDelegation.to(new RpcInvokeInterceptor(connectManager)))
                        .make()
                        .load(getClass().getClassLoader())
                        .getLoaded()
                        .newInstance();
                proxyByClass.put(clazz.getName(), proxy);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("反射出错");
            }
        }

        return (T) proxyByClass.get(clazz.getName());
    }

    @Override
    public void destroy() {
        for (Channel channel : connectManager.getAllChannel()) {
            if (channel != null) {
                channel.close();
            }
        }
        if (connectManager.getEventLoopGroup() != null) {
            connectManager.getEventLoopGroup().shutdownGracefully();
        }
    }

    public NettyClient(Registry registry) {
        this.connectManager = new NettyClientConnectManager();
        this.registry = registry;
    }

}

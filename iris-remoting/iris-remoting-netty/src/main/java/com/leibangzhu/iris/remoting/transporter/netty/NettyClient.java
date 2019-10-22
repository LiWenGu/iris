package com.leibangzhu.iris.remoting.transporter.netty;

import com.leibangzhu.iris.core.Endpoint;
import com.leibangzhu.iris.registry.IRegistry;
import com.leibangzhu.iris.registry.RegistryTypeEnum;
import com.leibangzhu.iris.remoting.Client;
import com.leibangzhu.iris.remoting.ClientConnectManager;
import com.leibangzhu.iris.remoting.RpcInvokeInterceptor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;

import java.util.*;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;

public class NettyClient implements Client {

    private Map<String, Object> proxyByClass = new LinkedHashMap<>();
    private ClientConnectManager connectManager;
    // 用于存储当前消费者订阅的服务提供者信息
    private Map<String, List<Endpoint>> map = new HashMap<>();

    @Override
    public <T> T ref(Class<T> clazz) {
        if (map.get(clazz.getName()) == null) {
            throw new RuntimeException("没有注册该接口" + clazz.getName() + "对应服务");
        }
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
    public void init(IRegistry registry, List<String> serviceNames) {
        // 启动时就检查服务提供者存不存在
        if (false) {
            for (String serivceName : serviceNames) {
                List<Endpoint> list = null;
                try {
                    list = registry.find(serivceName, RegistryTypeEnum.providers);
                } catch (Exception e) {
                    throw new RuntimeException("寻找服务失败");
                }
                if (list.isEmpty()) {
                    throw new RuntimeException("服务提供者不存在");
                }
            }
        }
        this.connectManager = new NettyClientConnectManager();
        connectManager.registry(registry, serviceNames);
        for (String serivceName : serviceNames) {
            map.put(serivceName, new ArrayList<>());
        }
    }
}

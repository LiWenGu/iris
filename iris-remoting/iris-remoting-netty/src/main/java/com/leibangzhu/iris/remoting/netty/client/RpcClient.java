package com.leibangzhu.iris.remoting.netty.client;

import com.leibangzhu.iris.core.Endpoint;
import com.leibangzhu.iris.registry.IRegistry;
import com.leibangzhu.iris.registry.RegistryTypeEnum;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;

import java.util.*;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;

public class RpcClient implements IRpcClient {

    private IRegistry registry;
    private Map<String, Object> proxyByClass = new LinkedHashMap<>();
    ConnectManager connectManager;
    // 用于存储当前消费者订阅的服务提供者信息
    private Map<String, List<Endpoint>> map = new HashMap<>();

    public RpcClient(IRegistry registry) {
        this.registry = registry;
        //this.registry.watch();
    }

    @Override
    public <T> T create(Class<T> clazz) throws Exception {
        if (map.get(clazz.getName()) == null) {
            throw new RuntimeException("没有注册该接口" + clazz.getName() + "对应服务");
        }
        if (!proxyByClass.containsKey(clazz.getName())) {
            T proxy = new ByteBuddy()
                    .subclass(clazz)
                    .method(isDeclaredBy(clazz)).intercept(MethodDelegation.to(new RpcInvokeInterceptor(connectManager)))
                    .make()
                    .load(getClass().getClassLoader())
                    .getLoaded()
                    .newInstance();

            proxyByClass.put(clazz.getName(), proxy);
        }

        return (T) proxyByClass.get(clazz.getName());
    }

    @Override
    public void run(List<String> serviceNames) throws Exception {
        // 启动时就检查服务提供者存不存在
        if (false) {
            for (String serivceName : serviceNames) {
                List<Endpoint> list = registry.find(serivceName, RegistryTypeEnum.providers);
                if (list.isEmpty()) {
                    throw new RuntimeException("服务提供者不存在");
                }
            }
        }
        this.connectManager = new ConnectManager(registry, serviceNames);
        for (String serivceName : serviceNames) {
            map.put(serivceName, new ArrayList<>());
        }
    }
}

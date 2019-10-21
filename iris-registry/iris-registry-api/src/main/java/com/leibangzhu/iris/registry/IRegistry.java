package com.leibangzhu.iris.registry;


import com.leibangzhu.coco.Extension;
import com.leibangzhu.iris.core.Endpoint;

import java.util.List;

@Extension(defaultValue = "etcd")
public interface IRegistry {

    // 注册服务
    void register(String serviceName, int port, RegistryTypeEnum type) throws Exception;

    void register(String serviceName, int port) throws Exception;

    // 取消注册服务
    void unRegistered(String serviceName, int port, RegistryTypeEnum type) throws Exception ;

    // 取消注册服务
    void unRegistered(String serviceName) throws Exception ;

    // 注册服务
    void subscribe(String serviceName, RegistryTypeEnum registryTypeEnum, IEventCallback iEventCallback);

    // 取消注册服务
    void unsubscribe(String serviceName, IEventCallback iEventCallback) throws Exception;

    // 找到服务
    List<Endpoint> find(String serviceName) throws Exception;

    List<Endpoint> find(String serviceName, RegistryTypeEnum type) throws Exception;

    void watch(IEventCallback callback);

    void keepAlive();

    //void watch();
}

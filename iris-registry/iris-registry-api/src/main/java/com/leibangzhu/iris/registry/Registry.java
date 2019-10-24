package com.leibangzhu.iris.registry;


import com.leibangzhu.coco.Extension;
import com.leibangzhu.iris.core.Endpoint;

import java.util.List;

@Extension(defaultValue = "etcd")
public interface Registry {

    // 注册服务
    void register(String serviceName, int port, RegistryTypeEnum type) throws Exception;

    // 取消注册服务
    void unRegistered(String serviceName, int port, RegistryTypeEnum type) throws Exception ;

    // 注册服务
    void subscribe(String serviceName, RegistryTypeEnum registryTypeEnum, IEventCallback iEventCallback);

    List<Endpoint> find(String serviceName, RegistryTypeEnum type) throws Exception;

    void keepAlive();

    void destroy();
}

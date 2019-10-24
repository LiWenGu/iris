package com.leibangzhu.iris.remoting;

import com.leibangzhu.iris.registry.Registry;

public interface Server {

    void init(Registry registry, int port);

    void export(Class<?> clazz, Object handler) throws Exception;

    void run();

    void destory();
}

package com.leibangzhu.iris.remoting;

import com.leibangzhu.iris.registry.IRegistry;

public interface Server {

    void init(IRegistry registry, int port);

    void export(Class<?> clazz, Object handler) throws Exception;

    void run();
}

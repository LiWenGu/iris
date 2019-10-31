package com.leibangzhu.iris.remoting;

public interface Server {

    void export(Class<?> clazz, Object handler) throws Exception;

    void run();

    void destory();
}

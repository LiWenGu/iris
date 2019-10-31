package com.leibangzhu.iris.remoting;

public interface Client {
    <T> T ref(Class<T> clazz);

    void destroy();
}

package com.leibangzhu.iris.remoting;

import com.leibangzhu.iris.registry.Registry;

import java.util.List;

public interface Client {
    void init(Registry registry, List<String> serviceNames);

    <T> T ref(Class<T> clazz);
}

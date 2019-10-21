package com.leibangzhu.iris.remoting;

import com.leibangzhu.iris.registry.IRegistry;

import java.util.List;

public interface Client {
    void init(IRegistry registry, List<String> serviceNames);

    <T> T ref(Class<T> clazz);
}

package com.leibangzhu.iris.remoting;

import com.leibangzhu.coco.Extension;
import com.leibangzhu.iris.registry.IRegistry;

import java.util.List;

@Extension(defaultValue = "netty")
public interface Client {
    void init(IRegistry registry, List<String> serviceNames);

    <T> T ref(Class<T> clazz);
}

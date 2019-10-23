package com.leibangzhu.iris.registry.etcd;

import com.leibangzhu.iris.registry.Registry;
import com.leibangzhu.iris.registry.RegistryFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EtcdRegistryFactory implements RegistryFactory {

    @Override
    public Registry getRegistry(String url) throws Exception {
        return new EtcdRegistry(url);
    }
}

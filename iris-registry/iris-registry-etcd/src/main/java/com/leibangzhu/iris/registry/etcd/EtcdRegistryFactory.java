package com.leibangzhu.iris.registry.etcd;

import com.leibangzhu.iris.registry.Registry;
import com.leibangzhu.iris.registry.RegistryFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class EtcdRegistryFactory implements RegistryFactory {

    List<Registry> list = new LinkedList<>();

    @Override
    public Registry getRegistry(String url) throws Exception {
        Registry registry = new EtcdRegistry(url);
        list.add(registry);
        return registry;
    }

    @Override
    public List<Registry> getAllRegistry() {
        return list;
    }

}

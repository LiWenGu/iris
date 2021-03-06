package com.leibangzhu.iris.registry.etcd.etcd;

import com.leibangzhu.iris.core.Endpoint;
import com.leibangzhu.iris.registry.RegistryTypeEnum;
import com.leibangzhu.iris.registry.etcd.EtcdRegistry;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

@Ignore
public class EtcdRegistryTest {

    @Test
    public void test() throws Exception {
        EtcdRegistry registry = new EtcdRegistry("http://127.0.0.1:2379");
        registry.register("com.leibangzhu.IHelloService",2000, RegistryTypeEnum.consumers);
        registry.register("com.leibangzhu.IHelloService",2100, RegistryTypeEnum.providers);
        registry.register("com.leibangzhu.IHelloService",2200, RegistryTypeEnum.configurators);
        Thread.sleep(3 * 1000);
        //List<Endpoint> endpoints = registry.find("com.leibangzhu.IHelloService");
        Thread.sleep(100 * 1000);
    }

    @Test
    public void test002() throws Exception {

        EtcdRegistry registry = new EtcdRegistry("http://127.0.0.1:2379");
        for (int i =0;i<50;i++){
            List<Endpoint> endpoints = registry.find("com.leibangzhu.IHelloService", RegistryTypeEnum.providers);
            System.out.println(endpoints);
            Thread.sleep(2 * 1000);
        }
    }

    @Test
    public void test003() throws Exception {

        EtcdRegistry registry = new EtcdRegistry("http://127.0.0.1:2379");
        registry.unRegistered("com.leibangzhu.IHelloService",2100, RegistryTypeEnum.providers);
    }


}

package com.leibangzhu.iris.demoa.service;

//import com.leibangzhu.iris.core.HelloService;

import com.leibangzhu.iris.demoa.api.IHelloService;
import com.leibangzhu.iris.registry.Registry;
import com.leibangzhu.iris.registry.etcd.EtcdRegistry;
import com.leibangzhu.iris.server.RpcServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Bean
    public Registry registry() throws Exception {
        Registry registry = new EtcdRegistry("http://127.0.0.1:2379");
        return registry;
    }

    @Bean
    public RpcServer rpcServer(Registry registry) throws Exception {

        RpcServer server = new RpcServer(registry)
                .port(2017)
                .exposeService(IHelloService.class,new HelloService());
        server.run();
        return server;
    }
}

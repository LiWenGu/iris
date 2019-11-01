package com.leibangzhu.iris.demo.springboot.server;

import com.leibangzhu.iris.demo.api.IHelloService;
import com.leibangzhu.iris.springboot.annotation.Service;

@Service
public class ServerService implements IHelloService {

    @Override
    public String sayHello(String name) {
        return "Hello, " + name + ", from server";
    }

    @Override
    public String sayHello2(String name) {
        return "Hello, " + name + ", from server";
    }
}

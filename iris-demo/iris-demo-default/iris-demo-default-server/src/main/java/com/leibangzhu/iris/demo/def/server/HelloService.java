package com.leibangzhu.iris.demo.def.server;


import com.leibangzhu.iris.demo.api.IHelloService;

public class HelloService implements IHelloService {

    @Override
    public String sayHello(String name) {
        return "Hello, " + name;
    }

    @Override
    public String sayHello2(String name) {
        return "Hello, " + name;
    }

}

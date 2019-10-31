package com.leibangzhu.iris.springboot.demo.server;

import com.leibangzhu.iris.springboot.annotation.Service;
import com.leibangzhu.iris.springboot.demo.service.IHelloService;

@Service
public class ServerService implements IHelloService {
    @Override
    public String hello(String name) throws Exception {
        return "Hello, " + name + ", from com.leibangzhu.iris.springboot.HelloService";
    }
}

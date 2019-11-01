package com.leibangzhu.iris.demo.springboot.client;

import com.leibangzhu.iris.demo.api.IHelloService;
import com.leibangzhu.iris.springboot.annotation.Reference;
import org.springframework.stereotype.Component;


@Component
public class ClientBean {

    @Reference
    private IHelloService helloService;

    public String hello(String name) throws Exception {
        return helloService.sayHello(name);
    }
}

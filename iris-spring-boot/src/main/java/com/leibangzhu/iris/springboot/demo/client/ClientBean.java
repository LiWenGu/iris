package com.leibangzhu.iris.springboot.demo.client;

import com.leibangzhu.iris.springboot.annotation.Reference;
import com.leibangzhu.iris.springboot.demo.service.IHelloService;
import org.springframework.stereotype.Service;

@Service
public class ClientBean {

    @Reference
    private IHelloService helloService;

    public String hello(String name) throws Exception {
        return helloService.hello(name);
    }
}

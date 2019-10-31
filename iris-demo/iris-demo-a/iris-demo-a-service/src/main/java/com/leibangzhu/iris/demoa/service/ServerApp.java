package com.leibangzhu.iris.demoa.service;


import com.leibangzhu.iris.demoa.api.IHelloService;
import com.leibangzhu.iris.demoa.service.service.HelloService;
import com.leibangzhu.iris.protocol.Protocol;
import com.leibangzhu.iris.protocol.ProtocolFactory;

import java.util.concurrent.TimeUnit;

public class ServerApp {
    public static void main(String[] args) throws Exception {
        Protocol protocol = ProtocolFactory.getProtocol();
        protocol.export(IHelloService.class, new HelloService());
        TimeUnit.MINUTES.sleep(1);
    }
}

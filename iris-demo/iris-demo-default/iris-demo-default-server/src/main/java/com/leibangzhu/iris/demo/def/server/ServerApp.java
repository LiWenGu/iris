package com.leibangzhu.iris.demo.def.server;


import com.leibangzhu.iris.demo.api.IHelloService;
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

package com.leibangzhu.iris.demo.def.client;

import com.leibangzhu.iris.demo.api.IHelloService;
import com.leibangzhu.iris.protocol.Protocol;
import com.leibangzhu.iris.protocol.ProtocolFactory;

import java.util.concurrent.TimeUnit;

public class DefaultClient {

    public static void main(String[] args) throws Exception {
        Protocol protocol = ProtocolFactory.getProtocol();
        while (true) {
            IHelloService iHelloService = protocol.ref(IHelloService.class);
            System.out.println(iHelloService.sayHello("liwenguang"));
            TimeUnit.SECONDS.sleep(1);
        }
    }
}

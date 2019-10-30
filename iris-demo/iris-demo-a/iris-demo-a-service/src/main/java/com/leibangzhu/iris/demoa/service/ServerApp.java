package com.leibangzhu.iris.demoa.service;


import com.leibangzhu.coco.ExtensionLoader;
import com.leibangzhu.iris.demoa.api.IHelloService;
import com.leibangzhu.iris.demoa.service.service.HelloService;
import com.leibangzhu.iris.protocol.Protocol;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class ServerApp {
    public static void main(String[] args) throws Exception {
        Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getDefaultExtension(Arrays.asList("filter"));
        protocol.export(IHelloService.class, new HelloService());
        TimeUnit.MINUTES.sleep(1);
    }
}

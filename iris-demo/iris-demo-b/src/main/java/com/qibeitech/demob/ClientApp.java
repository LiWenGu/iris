package com.qibeitech.demob;

import com.leibangzhu.coco.ExtensionLoader;
import com.leibangzhu.iris.demoa.api.IHelloService;
import com.leibangzhu.iris.protocol.Protocol;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class ClientApp {

    public static void main(String[] args) throws Exception {
        Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getDefaultExtension(Arrays.asList("filter"));
        while (true) {
            IHelloService iHelloService = protocol.ref(IHelloService.class);
            System.out.println(iHelloService.sayHello("liwenguang"));
            TimeUnit.SECONDS.sleep(1);
        }
    }
}

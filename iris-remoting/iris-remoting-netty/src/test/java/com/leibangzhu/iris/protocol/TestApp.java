package com.leibangzhu.iris.protocol;

import com.leibangzhu.coco.ExtensionLoader;
import com.leibangzhu.iris.core.HelloService;
import com.leibangzhu.iris.core.IHelloService;
import com.leibangzhu.iris.registry.Registry;
import com.leibangzhu.iris.registry.RegistryFactory;
import com.leibangzhu.iris.remoting.Client;
import com.leibangzhu.iris.remoting.Server;
import com.leibangzhu.iris.remoting.Transporter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestApp {

    @Test
    public void asd() throws Exception {
        Transporter transporter = ExtensionLoader.getExtensionLoader(Transporter.class).getDefaultExtension();
        RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getDefaultExtension();
        Registry registry = registryFactory.getRegistry("http://127.0.0.1:2379");

        Server server = transporter.bind(registry, 0);
        server.init(registry, 2017);
        server.export(IHelloService.class, new HelloService());
        server.run();
        TimeUnit.SECONDS.sleep(1);

        Client client = transporter.connect(registry);
        List<String> serviceNames = new ArrayList<>();
        serviceNames.add(IHelloService.class.getName());
        client.init(registry, serviceNames);
        IHelloService helloService = client.ref(IHelloService.class);
        String s = helloService.hello("leo");
        System.out.println("====" + s);
        TimeUnit.MINUTES.sleep(1000);

    }
}

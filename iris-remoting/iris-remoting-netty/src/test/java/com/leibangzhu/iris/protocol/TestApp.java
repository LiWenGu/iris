package com.leibangzhu.iris.protocol;

import com.leibangzhu.coco.ExtensionLoader;
import com.leibangzhu.iris.registry.Registry;
import com.leibangzhu.iris.registry.RegistryFactory;
import com.leibangzhu.iris.remoting.Client;
import com.leibangzhu.iris.remoting.IrisShutdownHook;
import com.leibangzhu.iris.remoting.Server;
import com.leibangzhu.iris.remoting.Transporter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestApp {

    @Test
    public void clientServer() throws Exception {

        IrisShutdownHook.getIrisShutdownHook().register();


        Transporter transporter = ExtensionLoader.getExtensionLoader(Transporter.class).getDefaultExtension();
        RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getDefaultExtension();

        Registry registry = registryFactory.getRegistry("http://127.0.0.1:2379");
        Server server = transporter.bind(registry, 2017);
        server.export(IHelloService.class, new HelloService());
        server.run();
        TimeUnit.SECONDS.sleep(1);

        Client client = transporter.connect(registry);
        List<String> serviceNames = new ArrayList<>();
        serviceNames.add(IHelloService.class.getName());
        IHelloService helloService = client.ref(IHelloService.class);
        String s = helloService.sayHello("leo");
        System.out.println("====" + s);
    }

    @Test
    public void destroy() throws Exception {
        IrisShutdownHook.getIrisShutdownHook().register();
        Transporter transporter = ExtensionLoader.getExtensionLoader(Transporter.class).getDefaultExtension();
        RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getDefaultExtension();

        Registry registry = registryFactory.getRegistry("http://127.0.0.1:2379");
        Server server = transporter.bind(registry, 2017);
        server.run();
        server.export(IHelloService.class, new HelloService());
        TimeUnit.SECONDS.sleep(100);
    }
}

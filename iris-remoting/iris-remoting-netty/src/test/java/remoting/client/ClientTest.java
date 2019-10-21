package remoting.client;

import com.leibangzhu.iris.core.IHelloService;
import com.leibangzhu.iris.registry.IRegistry;
import com.leibangzhu.iris.registry.etcd.EtcdRegistry;
import com.leibangzhu.iris.remoting.netty.client.ConnectManager;
import com.leibangzhu.iris.remoting.netty.client.RpcClient;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Ignore
public class ClientTest {

    @Test
    public void test() throws Exception {
        IRegistry registry = new EtcdRegistry("http://127.0.0.1:2379");
        RpcClient client = new RpcClient(registry);
        List<String> serviceNames = new ArrayList<>();
        serviceNames.add(IHelloService.class.getName());
        client.run(serviceNames);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    IHelloService helloService = client.create(IHelloService.class);
                    String s = helloService.hello("leo");
                    System.out.println("====" + s);

                    String s2 = helloService.hello("tom");
                    System.out.println("====" + s2);

                    String s3 = helloService.hello("jerry");
                    System.out.println("====" + s3);

                    System.out.println("==== rpc invoke finished...");
                    Thread.sleep(2 * 1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        },5,3, TimeUnit.SECONDS);

        Thread.sleep(3000 * 1000);
    }


    @Test
    public void test2() throws Exception {
        IRegistry registry = new EtcdRegistry("http://127.0.0.1:2379");
        RpcClient client = new RpcClient(registry);

        com.leibangzhu.iris.core.IHelloService helloService = client.create(com.leibangzhu.iris.core.IHelloService.class);
        String s = helloService.hello("haha");
        System.out.println(s);
    }

    @Test
    public void test3() throws Exception {
        IRegistry registry = new EtcdRegistry("http://127.0.0.1:2379");
        List<String> serviceNames = new ArrayList<>();
        serviceNames.add("abc");
        serviceNames.add("abc2");
        ConnectManager connectManager = new ConnectManager(registry, serviceNames);
        TimeUnit.MINUTES.sleep(3);
    }
}

package remoting;


import com.leibangzhu.iris.core.HelloService;
import com.leibangzhu.iris.core.IHelloService;
import com.leibangzhu.iris.registry.IRegistry;
import com.leibangzhu.iris.registry.etcd.EtcdRegistry;
import com.leibangzhu.iris.remoting.Server;
import com.leibangzhu.iris.remoting.netty.NettyServer;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ServerTest {

    @Test
    public void test() throws Exception {
        IRegistry registry = new EtcdRegistry("http://127.0.0.1:2379");
        Server server = new NettyServer();
        server.init(registry, 2017);
        server.export(IHelloService.class, new HelloService());
        server.run();
        Thread.sleep(100 * 1000);
    }
}

package protocol;

import com.leibangzhu.coco.ExtensionLoader;
import com.leibangzhu.iris.core.HelloService;
import com.leibangzhu.iris.core.IHelloService;
import com.leibangzhu.iris.protocol.Protocol;
import com.leibangzhu.iris.remoting.IrisShutdownHook;
import org.junit.Test;

public class TestApp {

    @Test
    public void test() throws Exception {
        IrisShutdownHook.getIrisShutdownHook().register();
        Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getDefaultExtension();
        protocol.export(IHelloService.class, new HelloService());
        IHelloService iHelloService = protocol.ref(IHelloService.class);
        System.out.println(iHelloService.hello("liwenguang"));
    }
}

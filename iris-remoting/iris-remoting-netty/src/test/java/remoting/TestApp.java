package remoting;

import com.leibangzhu.coco.ExtensionLoader;
import com.leibangzhu.iris.core.IHelloService;
import com.leibangzhu.iris.remoting.Client;
import com.leibangzhu.iris.remoting.Transporter;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TestApp {

    @Test
    public void asd() throws Exception {
        Transporter transporter = ExtensionLoader.getExtensionLoader(Transporter.class).getDefaultExtension();
        transporter.bind(null, 0);
        TimeUnit.SECONDS.sleep(1);
        Client client = transporter.connect(null);
        IHelloService helloService = client.ref(IHelloService.class);
        String s = helloService.hello("leo");
        System.out.println("====" + s);
        TimeUnit.MINUTES.sleep(1000);

    }
}

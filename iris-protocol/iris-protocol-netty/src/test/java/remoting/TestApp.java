package remoting;

import com.leibangzhu.coco.ExtensionLoader;
import com.leibangzhu.iris.remoting.RpcResponse;
import com.leibangzhu.iris.serialization.Serialization;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestApp {

    @Test
    public void asd() {
        Serialization serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getDefaultExtension();
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId("1");
        List<String> test = new ArrayList<>();
        test.add("11");
        rpcResponse.setResult(test);
        byte[] www = serialization.serialize(rpcResponse);
        RpcResponse z = (RpcResponse) serialization.deserialize(www, RpcResponse.class);
        System.out.println(z);
    }
}

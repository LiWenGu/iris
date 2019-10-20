package protobuf;

import com.leibangzhu.iris.serialization.Serialization;
import com.leibangzhu.iris.serialization.protobuf.ProtobufSerialization;

import java.util.ArrayList;
import java.util.List;

public class Test {

    @org.junit.Test
    public void ad() {
        Serialization<RpcResponse> serialization = new ProtobufSerialization();
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId("1");
        List<String> test = new ArrayList<>();
        test.add("11");
        rpcResponse.setResult(test);
        byte[] www = serialization.serialize(rpcResponse);
        RpcResponse z = serialization.deserialize(www, RpcResponse.class);
        System.out.println(z);
    }
}

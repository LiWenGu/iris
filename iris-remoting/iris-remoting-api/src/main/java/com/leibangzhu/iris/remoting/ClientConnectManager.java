package com.leibangzhu.iris.remoting;

import com.leibangzhu.iris.registry.IRegistry;
import io.netty.channel.Channel;

import java.util.List;

public interface ClientConnectManager {

    void registry(IRegistry registry, List<String> serivceNames);

    Channel getChannel(String serviceName) throws Exception;
}

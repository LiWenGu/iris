package com.leibangzhu.iris.remoting.netty.client;

import io.netty.channel.Channel;

public interface IConnectManager {
    Channel getChannel(String serviceName) throws Exception;
}

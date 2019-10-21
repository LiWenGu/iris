package com.leibangzhu.iris.remoting.netty.client;

import java.util.List;

public interface IRpcClient {

    <T> T create(Class<T> clazz) throws Exception;

    void run(List<String> serviceNames) throws Exception;
}

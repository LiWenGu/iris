package com.leibangzhu.iris.remoting.netty.client;

public interface IRpcClient {

    <T> T create(Class<T> clazz) throws Exception;
}

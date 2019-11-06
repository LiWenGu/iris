package com.leibangzhu.iris.remoting;

public interface Client {

    void doOpen() throws Throwable;

    void doConnect() throws Throwable;

    Channel getChannel();
}

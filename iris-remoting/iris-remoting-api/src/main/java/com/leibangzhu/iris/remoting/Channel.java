package com.leibangzhu.iris.remoting;

import java.net.InetSocketAddress;

public interface Channel {

    void send(Object message, boolean sent) throws RemotingException;

    InetSocketAddress getLocalAddress();

    InetSocketAddress getRemoteAddress();

    URL getUrl();
}

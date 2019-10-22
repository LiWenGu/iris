package com.leibangzhu.iris.remoting;

import com.leibangzhu.iris.core.Endpoint;
import io.netty.channel.Channel;

import java.util.Objects;

/**
 * todo 暂时和 netty channel 耦合
 * 存储在客户端的服务提供者相关信息
 */
public class ClientChannelWrapper {
    private Endpoint endpoint;
    private Channel channel;

    public ClientChannelWrapper(Endpoint endpoint, Channel channel) {
        this.endpoint = endpoint;
        this.channel = channel;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public String toString() {
        return endpoint.getHost() + ":" + endpoint.getPort();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientChannelWrapper that = (ClientChannelWrapper) o;
        return Objects.equals(endpoint.getHost(), that.endpoint.getHost()) &&
                Objects.equals(endpoint.getPort(), that.getEndpoint().getPort());
    }

    @Override
    public int hashCode() {
        return Objects.hash(endpoint.getHost(), endpoint.getPort());
    }
}
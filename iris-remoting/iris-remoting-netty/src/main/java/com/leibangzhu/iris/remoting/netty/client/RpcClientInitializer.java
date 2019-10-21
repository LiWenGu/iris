package com.leibangzhu.iris.remoting.netty.client;

import com.leibangzhu.iris.remoting.RpcRequest;
import com.leibangzhu.iris.remoting.RpcResponse;
import com.leibangzhu.iris.remoting.netty.RpcDecoder;
import com.leibangzhu.iris.remoting.netty.RpcEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class RpcClientInitializer extends ChannelInitializer<SocketChannel>{
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new RpcEncoder(RpcRequest.class));
        pipeline.addLast(new LengthFieldBasedFrameDecoder(65536,0,4,0,0));
        pipeline.addLast(new RpcDecoder(RpcResponse.class));
        pipeline.addLast(new RpcClientHandler());
    }
}

package com.leibangzhu.iris.remoting.transporter.netty;

import com.leibangzhu.iris.remoting.RpcFuture;
import com.leibangzhu.iris.remoting.RpcRequestHolder;
import com.leibangzhu.iris.remoting.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) throws Exception {
        String requestId = response.getRequestId();
        // 客户端读取服务器响应信息
        RpcFuture future = RpcRequestHolder.get(requestId);
        if(null != future){
            RpcRequestHolder.remove(requestId);
            future.done(response);
        }
    }
}

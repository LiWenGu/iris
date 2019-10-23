package com.leibangzhu.iris.protocol;

import com.leibangzhu.coco.Extension;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

@Extension(defaultValue = "iris")
public interface Codec {

    void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out);

    void decode(ChannelHandlerContext ctx, ByteBuf input, List<Object> out);

}

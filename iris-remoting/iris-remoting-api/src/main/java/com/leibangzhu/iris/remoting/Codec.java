package com.leibangzhu.iris.remoting;

import com.leibangzhu.coco.Extension;
import io.netty.buffer.ByteBuf;

import java.util.List;

// FIXME  需要对 ByteBuf 抽象
@Extension(defaultValue = "iris")
public interface Codec {

    void encode(Channel ctx, Object msg, ByteBuf out);

    void decode(Channel ctx, ByteBuf input, List<Object> out);

}

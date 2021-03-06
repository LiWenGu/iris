package com.leibangzhu.iris.protocol;

import com.leibangzhu.coco.ExtensionLoader;
import com.leibangzhu.iris.remoting.Channel;
import com.leibangzhu.iris.remoting.Codec;
import com.leibangzhu.iris.serialization.Serialization;
import io.netty.buffer.ByteBuf;

import java.util.List;

public class IrisCodec implements Codec {

    @Override
    public void encode(Channel channel, Object msg, ByteBuf out) {
        if (msg instanceof RpcRequest) {
            RpcRequest req = (RpcRequest) msg;
            Serialization serialization = CodecUtil.getSerialization(channel.getUrl());
            byte[] content = serialization.serialize(req);
            // 1.写入消息的开头的信息标志(int类型)
            out.writeInt(IrisProtocol.head_data);
            out.writeInt(IrisProtocol.req);
            // 2.写入消息的长度(int 类型)
            out.writeInt(content.length);
            // 3.写入消息的内容(byte[]类型)
            out.writeBytes(content);
        } else if (msg instanceof RpcResponse) {
            RpcResponse res = (RpcResponse) msg;
            Serialization serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getDefaultExtension();
            byte[] content = serialization.serialize(res);
            // 1.写入消息的开头的信息标志(int类型)
            out.writeInt(IrisProtocol.head_data);
            out.writeInt(IrisProtocol.res);
            // 2.写入消息的长度(int 类型)
            out.writeInt(content.length);
            // 3.写入消息的内容(byte[]类型)
            out.writeBytes(content);
        }
    }

    @Override
    public void decode(Channel channel, ByteBuf input, List<Object> out) {
        if (input.readableBytes() < 4) {
            return;
        }
        int beginReader;

        while (true) {
            // 获取包头开始的index
            beginReader = input.readerIndex();
            // 标记包头开始的index
            input.markReaderIndex();
            // 读到了协议的开始标志，结束while循环
            if (input.readInt() == IrisProtocol.head_data) {
                break;
            }

            // 未读到包头，略过一个字节
            // 每次略过，一个字节，去读取，包头信息的开始标记
            input.resetReaderIndex();
            input.readByte();

            // 当略过，一个字节之后，
            // 数据包的长度，又变得不满足
            // 此时，应该结束。等待后面的数据到达
            if (input.readableBytes() < 4) {
                return;
            }
        }

        // 消息的长度

        int reqOrres = input.readInt();
        int length = input.readInt();
        // 判断请求数据包数据是否到齐
        if (input.readableBytes() < length) {
            // 还原读指针
            input.readerIndex(beginReader);
            return;
        }

        // 读取data数据
        byte[] data = new byte[length];
        input.readBytes(data);
        if (reqOrres == 1) {
            Serialization serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getDefaultExtension();
            Object msg = serialization.deserialize(data, RpcRequest.class);
            out.add(msg);
        } else if (reqOrres == 0) {
            Serialization serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getDefaultExtension();
            Object msg = serialization.deserialize(data, RpcResponse.class);
            out.add(msg);
        }
    }

}

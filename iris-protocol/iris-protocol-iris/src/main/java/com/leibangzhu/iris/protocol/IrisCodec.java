package com.leibangzhu.iris.protocol;

import com.leibangzhu.coco.ExtensionLoader;
import com.leibangzhu.iris.serialization.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.List;

public class IrisCodec implements Codec {

    @Override
    public byte[] encode() {
        return new byte[0];
    }

    @Override
    public Object decode() {
        return null;
    }

    public static class InternalEncoder extends MessageToByteEncoder {

        @Override
        protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
            if (msg instanceof RpcRequest) {
                RpcRequest req = (RpcRequest) msg;
                Serialization serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getDefaultExtension();
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
    }

    public static class InternalDecoder extends ByteToMessageDecoder {

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf input, List<Object> out) throws Exception {
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


}

package com.leibangzhu.iris.serialization;

import com.leibangzhu.coco.Extension;

@Extension(defaultValue = "protubuf")
public interface Serialization<T> {

    /**
     * 序列化 ( Java对象 -> 字节数组)
     */
    byte[] serialize(T obj);

    /**
     * 反序列化 (字节数组 -> Java对象)
     */
    T deserialize(byte[] data, Class<T> cls);
}

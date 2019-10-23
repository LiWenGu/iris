package com.leibangzhu.iris.protocol;

import com.leibangzhu.coco.Extension;

@Extension(defaultValue = "iris")
public interface Codec {
    byte[] encode();

    Object decode();
}

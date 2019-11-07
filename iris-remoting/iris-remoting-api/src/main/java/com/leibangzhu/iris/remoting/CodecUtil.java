package com.leibangzhu.iris.remoting;

import com.leibangzhu.coco.ExtensionLoader;
import com.leibangzhu.iris.serialization.Serialization;

public class CodecUtil {

    public static Serialization getSerialization(URL url) {
        return ExtensionLoader.getExtensionLoader(Serialization.class).getExtension(
                url.getParams().getOrDefault(Constants.SERIALIZATION_KEY, Constants.DEFAULT_REMOTING_SERIALIZATION));
    }

    public static Codec getCodec(URL url) {
        return ExtensionLoader.getExtensionLoader(Codec.class).getExtension(
                url.getParams().getOrDefault(Constants.CODEC_KEY, "telnet"));
    }
}

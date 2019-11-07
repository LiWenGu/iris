package com.leibangzhu.iris.protocol;

import com.leibangzhu.coco.ExtensionLoader;
import com.leibangzhu.iris.remoting.Constants;
import com.leibangzhu.iris.remoting.URL;
import com.leibangzhu.iris.serialization.Serialization;

public class CodecUtil {

    public static Serialization getSerialization(URL url) {
        return ExtensionLoader.getExtensionLoader(Serialization.class).getExtension(
                url.getParams().getOrDefault(Constants.SERIALIZATION_KEY, Constants.DEFAULT_REMOTING_SERIALIZATION));
    }
}

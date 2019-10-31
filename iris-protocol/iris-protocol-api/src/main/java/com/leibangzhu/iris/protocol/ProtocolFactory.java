package com.leibangzhu.iris.protocol;


import com.leibangzhu.coco.ExtensionLoader;

import java.util.Arrays;

public class ProtocolFactory {

    private static Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getDefaultExtension(Arrays.asList("filter"));

    public static Protocol getProtocol() {
        return protocol;
    }

}

package com.leibangzhu.iris.protocol;

import com.leibangzhu.coco.Extension;

@Extension
public interface Filter {
    Object invoke(Object o) throws Exception;
}
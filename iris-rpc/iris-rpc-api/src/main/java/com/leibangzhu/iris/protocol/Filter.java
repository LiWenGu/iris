package com.leibangzhu.iris.protocol;

import com.leibangzhu.coco.Extension;

@Extension
public interface Filter {
    Object invoke(Class<?> clazz, Object handler) throws Exception;
}
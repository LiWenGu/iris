package com.leibangzhu.iris.protocol;

import com.leibangzhu.coco.Extension;

@Extension(defaultValue = "iris")
public interface Protocol {

    void export(Class<?> clazz, Object handler) throws Exception;

    <T> T ref(Class<T> clazz) throws Exception;

    void destroy();

}

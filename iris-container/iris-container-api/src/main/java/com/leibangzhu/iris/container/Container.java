package com.leibangzhu.iris.container;

import com.leibangzhu.coco.Extension;

@Extension(defaultValue = "spring")
public interface Container {

    void start();

    void stop();
}

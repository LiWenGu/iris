package com.leibangzhu.iris.protocol;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TraceFilter implements Filter {
    @Override
    public Object invoke(Class<?> clazz, Object handler) throws Exception {
        log.info("日志过滤器开启" + clazz.getName() + "," + handler);
        return "";
    }
}

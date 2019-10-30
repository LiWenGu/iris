package com.leibangzhu.iris.protocol;

public class TraceFilter implements Filter {
    @Override
    public Object invoke(Object o) throws Exception {
        return o + "traceFilter";
    }
}

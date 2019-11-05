package com.leibangzhu.iris.cluster;

import java.util.List;

public interface LoadBalance {

    /**
     * 负载均衡
     *
     * @param invokers
     * @return
     * @throws Exception
     */
    public Object select(List<Object> invokers) throws Exception;

}
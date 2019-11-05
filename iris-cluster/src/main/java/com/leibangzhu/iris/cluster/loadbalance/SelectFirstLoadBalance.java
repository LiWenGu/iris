package com.leibangzhu.iris.cluster.loadbalance;

import com.leibangzhu.iris.cluster.LoadBalance;

import java.util.List;

public class SelectFirstLoadBalance implements LoadBalance {

    @Override
    public Object select(List<Object> invokers) throws Exception {
        System.out.println("SelectFirstLoadBalance to select ...");
        return invokers.get(0);
    }

}

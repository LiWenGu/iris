package com.leibangzhu.iris.cluster.loadbalance;

import com.leibangzhu.iris.cluster.LoadBalance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance implements LoadBalance {

    private Random random = new Random();

    @Override
    public Object select(List<Object> invokers) throws Exception {
        int amount = invokers.size();
        if (amount <= 0){
            throw new Exception("RandomLoadBalance: no available items to select");
        }else if (amount == 1){
            return 0;
        }else {
            return random.nextInt(amount);
        }
    }
}

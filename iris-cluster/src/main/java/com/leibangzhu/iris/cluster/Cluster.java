package com.leibangzhu.iris.cluster;

public interface Cluster {

    Object join(Directory directory) throws Exception;

}
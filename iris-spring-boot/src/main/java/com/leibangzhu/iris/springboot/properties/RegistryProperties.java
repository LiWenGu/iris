package com.leibangzhu.iris.springboot.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "iris.registry")
public class RegistryProperties {

    private String address = "http://127.0.0.1:2379";
    private String protocol = "etcd";
}

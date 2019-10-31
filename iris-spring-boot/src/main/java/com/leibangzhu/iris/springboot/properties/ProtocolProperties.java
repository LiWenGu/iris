package com.leibangzhu.iris.springboot.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "iris.protocol")
public class ProtocolProperties {

    private int port = 2017;
}

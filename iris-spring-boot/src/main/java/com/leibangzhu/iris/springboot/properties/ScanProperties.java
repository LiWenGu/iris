package com.leibangzhu.iris.springboot.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "iris.scan")
public class ScanProperties {

    private String basePackages;
}

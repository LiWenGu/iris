package com.leibangzhu.iris.springboot.config;

import com.leibangzhu.iris.core.IrisConfig;
import com.leibangzhu.iris.protocol.Protocol;
import com.leibangzhu.iris.protocol.ProtocolFactory;
import com.leibangzhu.iris.springboot.properties.ProtocolProperties;
import com.leibangzhu.iris.springboot.properties.RegistryProperties;
import com.leibangzhu.iris.springboot.properties.ScanProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({RegistryProperties.class, ProtocolProperties.class, ScanProperties.class})
@ComponentScan(basePackages = {"com.leibangzhu.iris.springboot.annotation"})
public class IrisAutoConfiguration {

    @Autowired
    private ProtocolProperties protocolProperties;

    @Autowired
    private RegistryProperties registryProperties;

    @Autowired
    private ScanProperties scanProperties;

    @Bean
    public Protocol protocol() {
        IrisConfig.set("iris.protocol.port", protocolProperties.getPort());
        IrisConfig.set("iris.registry.protocol", registryProperties.getProtocol());
        IrisConfig.set("iris.registry.address", registryProperties.getAddress());
        IrisConfig.set("iris.scan.basePackages", scanProperties.getBasePackages());
        return ProtocolFactory.getProtocol();
    }

}

package com.leibangzhu.iris.demo.springboot.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class ServerApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ServerApplication.class);
        ApplicationContext ctx = app.run(args);
        String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
        TimeUnit.MINUTES.sleep(5);
    }
}

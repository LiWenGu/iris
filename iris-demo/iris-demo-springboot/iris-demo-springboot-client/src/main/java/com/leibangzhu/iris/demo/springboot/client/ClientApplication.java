package com.leibangzhu.iris.demo.springboot.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ClientApplication {

    @Autowired
    private static ClientBean clientBean;

    @Autowired
    public void setClientBean(ClientBean clientBean) {
        this.clientBean = clientBean;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ClientApplication.class);
        ApplicationContext ctx = app.run(args);
        String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
        System.out.println("xxx");

        System.out.println(clientBean.hello("来自client的请求"));
    }
}

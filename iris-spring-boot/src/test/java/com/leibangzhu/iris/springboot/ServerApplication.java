package com.leibangzhu.iris.springboot;

import com.leibangzhu.iris.springboot.demo.client.ClientBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.leibangzhu.iris")
public class ServerApplication {

    @Autowired
    private static ClientBean clientBean;

    @Autowired
    public void setClientBean(ClientBean clientBean) {
        this.clientBean = clientBean;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ServerApplication.class);
        ApplicationContext ctx = app.run(args);
        String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
        System.out.println("xxx");

        System.out.println(clientBean.hello("asdasd"));
    }
}

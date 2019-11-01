package com.leibangzhu.iris.springboot.annotation;

import com.leibangzhu.iris.protocol.Protocol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 扫描 Service，注入 Reference
 */
@Slf4j
@Configuration
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private Protocol protocol;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 根容器为Spring容器  
        if (event.getApplicationContext().getParent() == null) {
            Map<String, Object> server = event.getApplicationContext().getBeansWithAnnotation(Service.class);
            for (Map.Entry<String, Object> stringObjectEntry : server.entrySet()) {
                Object handle = stringObjectEntry.getValue();
                try {
                    protocol.export(handle.getClass().getInterfaces()[0], handle);
                } catch (Exception e) {
                    log.error(handle + "暴露 Service 失败");
                }
            }
            String[] clients = event.getApplicationContext().getBeanDefinitionNames();
            for (String client : clients) {
                Object bean = event.getApplicationContext().getBean(client);
                Field[] fields = bean.getClass().getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    if (field.isAnnotationPresent(Reference.class)) {
                        field.setAccessible(true);
                        try {
                            field.set(bean, protocol.ref(field.getType()));
                        } catch (Exception e) {
                            log.error(bean + "注入 Reference 失败");
                        }
                    }
                }
            }
            log.info("注解扫描注入完成");
        }
    }
}
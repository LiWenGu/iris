
package com.leibangzhu.iris.springboot.annotation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import java.util.Set;

/**
 * 动态注入 Service 注解类
 */
@Configuration
@Slf4j
public class SpringConfiguration {

    @Bean
    public CustomBeanDefinitionRegistry customBeanDefinitionRegistry(Environment environment) {
        return new CustomBeanDefinitionRegistry(environment.getProperty("iris.scan.base-packages"));
    }

    public class CustomBeanDefinitionRegistry implements BeanDefinitionRegistryPostProcessor {

        private String scanPackages;

        public CustomBeanDefinitionRegistry(String scanPackages) {
            this.scanPackages = scanPackages;
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        }

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            boolean useDefaultFilters = false;//是否使用默认的filter，使用默认的filter意味着只扫描那些类上拥有Component、Service、Repository或Controller注解的类。
            ClassPathScanningCandidateComponentProvider beanScanner = new ClassPathScanningCandidateComponentProvider(useDefaultFilters);
            TypeFilter includeFilter = new AnnotationTypeFilter(Service.class);
            beanScanner.addIncludeFilter(includeFilter);
            if (scanPackages == null || scanPackages.length() == 0) {
                log.info("没有暴露服务，该应用为纯消费者");
                return;
            }
            Set<BeanDefinition> beanDefinitions = beanScanner.findCandidateComponents(scanPackages);
            for (BeanDefinition beanDefinition : beanDefinitions) {
                //beanName通常由对应的BeanNameGenerator来生成，比如Spring自带的AnnotationBeanNameGenerator、DefaultBeanNameGenerator等，也可以自己实现。
                String beanName = beanDefinition.getBeanClassName();
                registry.registerBeanDefinition(beanName, beanDefinition);
            }
        }
    }

}
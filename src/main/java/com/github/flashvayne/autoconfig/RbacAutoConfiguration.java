package com.github.flashvayne.autoconfig;

import com.github.flashvayne.aspect.RbacAuthorizationAspect;
import com.github.flashvayne.property.RbacProperties;
import com.github.flashvayne.service.RbacAuthUserService;
import com.github.flashvayne.service.RbacTokenService;
import com.github.flashvayne.service.impl.DefaultRbacAuthUserServiceImpl;
import com.github.flashvayne.service.impl.DefaultRbacTokenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * Rbac自动装配
 * 装配AOP、用户服务、Token服务
 * 需要配置属性rbac.enable=true 来启用Rbac-spring-boot-start
 *
 * 使用@ConditionalOnMissingBean进行装配，所以以上服务均可被重写并注入。例如：
 * 需要密码加密，只需继承DefaultAuthUserServiceImpl，重写passwordVerification方法并注入容器即可
 *
 * @author flashvayne
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RbacProperties.class)
@ConditionalOnProperty(value = "rbac.enable",havingValue = "true")
public class RbacAutoConfiguration {

    public RbacAutoConfiguration(){
        log.info("rbac-springboot-start loaded.");
    }

    @Autowired
    private RbacProperties rbacProperties;

    @Bean
    @DependsOn("rbacTokenService")
    @ConditionalOnMissingBean(RbacAuthorizationAspect.class)
    public RbacAuthorizationAspect rbacAuthorizationAspect(){
        RbacAuthorizationAspect rbacAuthorizationAspect = new RbacAuthorizationAspect();
        return rbacAuthorizationAspect;
    }

    @Bean
    @DependsOn("rbacTokenService")
    @ConditionalOnMissingBean(RbacAuthUserService.class)
    public RbacAuthUserService rbacAuthUserService(){
        RbacAuthUserService rbacAuthUserService = new DefaultRbacAuthUserServiceImpl();
        return rbacAuthUserService;
    }

    @Bean
    @ConditionalOnMissingBean(RbacTokenService.class)
    public RbacTokenService rbacTokenService(){
        RbacTokenService rbacTokenService = new DefaultRbacTokenServiceImpl(rbacProperties);
        return rbacTokenService;
    }

}

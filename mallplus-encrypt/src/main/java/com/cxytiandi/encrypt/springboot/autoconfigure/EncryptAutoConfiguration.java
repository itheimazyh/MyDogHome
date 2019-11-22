package com.cxytiandi.encrypt.springboot.autoconfigure;

import com.cxytiandi.encrypt.algorithm.EncryptAlgorithm;
import com.cxytiandi.encrypt.core.EncryptionConfig;
import com.cxytiandi.encrypt.core.EncryptionFilter;
import com.cxytiandi.encrypt.springboot.init.ApiEncryptDataInit;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;


/**
 * 加解密自动配置
 *
 * @author zscat
 * @about 2019-04-30
 */
@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties(EncryptionConfig.class)
public class EncryptAutoConfiguration {

    @Resource
    private EncryptionConfig encryptionConfig;

    @Resource
    private EncryptAlgorithm encryptAlgorithm;

    /**
     * 不要用泛型注册Filter,泛型在Spring Boot 2.x版本中才有
     *
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    public FilterRegistrationBean filterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        if (encryptAlgorithm != null) {
            registration.setFilter(new EncryptionFilter(encryptionConfig, encryptAlgorithm));
        } else {
            registration.setFilter(new EncryptionFilter(encryptionConfig));
        }
        registration.addUrlPatterns(encryptionConfig.getUrlPatterns());
        registration.setName("EncryptionFilter");
        registration.setOrder(encryptionConfig.getOrder());
        return registration;
    }

    @Bean
    public ApiEncryptDataInit apiEncryptDataInit() {
        return new ApiEncryptDataInit();
    }
}

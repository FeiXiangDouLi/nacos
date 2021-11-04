package com.example.nacos.demo.service;

import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;

import lombok.Getter;

/**
 * @author honglixiang@tiduyun.com
 * @date 2021/11/3
 */
@Component
public class NacosManager {
    /**
     * 配置管理
     */
    @Getter
    private ConfigService configService;

    /**
     * 服务管理
     */
    @Getter
    private NamingService namingService;

    @Getter
    private NamingMaintainService namingMaintainService;

    @PostConstruct
    private void init() throws NacosException {
        Properties properties = new Properties();
        properties.put("serverAddr", "10.58.0.15:8848");
        properties.put("namespace", "dev");
        this.configService = NacosFactory.createConfigService(properties);
        this.namingService = NacosFactory.createNamingService(properties);
        this.namingMaintainService = NacosFactory.createMaintainService(properties);
    }
}

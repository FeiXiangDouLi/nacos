package com.example.nacos.demo.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.example.nacos.demo.listener.MyListener;
import com.example.nacos.demo.service.NacosManager;

/**
 * @author honglixiang@tiduyun.com
 * @date 2021/11/3
 */
@RestController
public class NacosConfigController {

    Map<String, Listener> cacheConfigListenerMap = new ConcurrentHashMap<>(16);

    @Autowired
    private NacosManager nacosManager;

    @GetMapping("/config")
    public String getConfig(@RequestParam String dataId, @RequestParam String group, @RequestParam Long timeOutMs)
        throws NacosException {
        return nacosManager.getConfigService().getConfig(dataId, group, timeOutMs);
    }

    /**
     * 添加配置，会覆盖掉以前的配置，不是增量添加。
     * 
     * 默认发布是Text格式。nacos-client：1.4.1版本开始支持指定格式发布
     * 
     * 1.原配置格式是Text,如果发布配置时指定YAML格式是无效的。
     * 
     * 2.原配置格式YAML，-如果发布配置时指定Text格式，原配置格式会改为Text. -如果发布时存在格式问题，也会发布成功。
     * 
     * @param dataId
     * @param group
     * @param context
     * @throws NacosException
     */
    @PostMapping("/config")
    public Boolean addConfig(@RequestParam String dataId, @RequestParam String group,
        @RequestParam(required = false) String configType, @RequestBody String context) throws NacosException {
        // nacosManager.getConfigService().publishConfig(dataId, group, context);
        if (configType == null) {
            configType = ConfigType.YAML.getType();
        }
        return nacosManager.getConfigService().publishConfig(dataId, group, context, configType);
    }

    @DeleteMapping("/config")
    public Boolean removeConfig(@RequestParam String dataId, @RequestParam String group) throws NacosException {
        return nacosManager.getConfigService().removeConfig(dataId, group);
    }

    /**
     * 添加监听有效，但是在nacos的控制台中监听查询列表找不到。
     * 
     * @param dataId
     * @param group
     * @throws NacosException
     */
    @PostMapping("/config/listener")
    public void addConfigListener(@RequestParam String dataId, @RequestParam String group) throws NacosException {
        MyListener myListener = new MyListener();
        cacheConfigListenerMap.put(dataId + group, myListener);
        nacosManager.getConfigService().addListener(dataId, group, myListener);
    }

    /**
     * 删除的监听，和添加监听必须是同一个对象，否则删除无效。比如删除是new出来的，则无效，即使是同一个监听实体类。
     * 
     * @param dataId
     * @param group
     * @throws NacosException
     */
    @DeleteMapping("/config/listener")
    public void removeConfigListener(@RequestParam String dataId, @RequestParam String group) throws NacosException {
        Listener listener = cacheConfigListenerMap.get(dataId + group);
        if (listener != null) {
            nacosManager.getConfigService().removeListener(dataId, group, listener);
        }
    }
}

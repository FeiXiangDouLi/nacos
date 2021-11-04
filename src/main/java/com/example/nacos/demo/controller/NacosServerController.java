package com.example.nacos.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.listener.AbstractEventListener;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.example.nacos.demo.listener.MyInstanceSubscribeListener;
import com.example.nacos.demo.service.NacosManager;

/**
 * @author honglixiang@tiduyun.com
 * @date 2021/11/3
 */
@RestController
public class NacosServerController {

    Map<String, AbstractEventListener> caCheEvent = new ConcurrentHashMap<>(16);

    @Autowired
    private NacosManager nacosManager;

    /**
     * 获取全部实例,默认是DEFAULT_GROUP下的实例
     * 
     * @param serviceName
     * @param group
     * @param clusters
     * @param subscribe
     * @return
     * @throws NacosException
     */
    @GetMapping("/instance")
    public List<Instance> getAllInstances(@RequestParam String serviceName,
        @RequestParam(required = false) String group, @RequestParam(required = false) List<String> clusters,
        @RequestParam(required = false) Boolean subscribe) throws NacosException {

        if (group == null) {
            group = Constants.DEFAULT_GROUP;
        }

        if (clusters == null) {
            clusters = new ArrayList<>();
        }

        if (subscribe == null) {
            subscribe = true;
        }

        return nacosManager.getNamingService().getAllInstances(serviceName, group, clusters, subscribe);
    }

    /**
     * 
     * @param serviceName
     * @param group
     *            默认值：DEFAULT_GROUP
     * @param clusters
     *            默认值：空集合
     * @param healthy
     *            是否健康
     * @param subscribe
     * @return
     * @throws NacosException
     */
    @GetMapping("/instance/select")
    public List<Instance> selectHealthyInstances(@RequestParam String serviceName,
        @RequestParam(required = false) String group, @RequestParam(required = false) List<String> clusters,
        @RequestParam(required = false) Boolean healthy, @RequestParam(required = false) Boolean subscribe)
        throws NacosException {
        if (group == null) {
            group = Constants.DEFAULT_GROUP;
        }
        if (clusters == null) {
            clusters = new ArrayList<>();
        }
        if (healthy == null) {
            healthy = true;
        }
        if (subscribe == null) {
            subscribe = true;
        }
        return nacosManager.getNamingService().selectInstances(serviceName, group, clusters, healthy, subscribe);
    }

    /**
     * 注销实例： 被注销后，订阅者也会无效，需要重新订阅。
     * 
     * @param serviceName
     * @param ip
     * @param port
     * @param groupName
     *            默认值：DEFAULT_GROUP
     * @param clusterName
     *            默认值：DEFAULT
     * @throws NacosException
     */
    @DeleteMapping("/instance")
    public void deregisterInstance(@RequestParam String serviceName, @RequestParam String ip, @RequestParam int port,
        @RequestParam(required = false) String groupName, @RequestParam(required = false) String clusterName)
        throws NacosException {

        if (groupName == null) {
            groupName = Constants.DEFAULT_GROUP;
        }

        if (clusterName == null) {
            clusterName = Constants.DEFAULT_CLUSTER_NAME;
        }

        nacosManager.getNamingService().deregisterInstance(serviceName, groupName, ip, port, clusterName);

    }

    /**
     * 注册实例
     * 
     * 可以随便注册一个空实例上去，而且健康状态为true.
     * 
     * @param serviceName
     * @param ip
     * @param port
     * @param groupName
     *            默认值：DEFAULT_GROUP
     * @param clusterName
     *            默认值：DEFAULT
     */
    @PostMapping("/instance")
    public void registerInstance(@RequestParam String serviceName, @RequestParam String ip, @RequestParam int port,
        @RequestParam(required = false) String groupName, @RequestParam(required = false) String clusterName)
        throws NacosException {
        if (groupName == null) {
            groupName = Constants.DEFAULT_GROUP;
        }

        if (clusterName == null) {
            clusterName = Constants.DEFAULT_CLUSTER_NAME;
        }
        nacosManager.getNamingService().registerInstance(serviceName, groupName, ip, port, clusterName);
    }

    /**
     * 监听服务：监听服务下的实例列表变化。
     * 
     * 1.回调需要大概5-10秒钟，才会收到事件。2.多次订阅会触发多次回调。
     * 
     * @param serviceName
     * @param groupName
     *            默认值：DEFAULT_GROUP
     * @param clusters
     *            默认值：空集合
     * @throws NacosException
     */
    @PostMapping("/instance/subscribe")
    public void subscribe(@RequestParam String serviceName, @RequestParam(required = false) String groupName,
        @RequestParam(required = false) List<String> clusters) throws NacosException {
        if (groupName == null) {
            groupName = Constants.DEFAULT_GROUP;
        }

        if (clusters == null) {
            clusters = new ArrayList<>();
        }
        MyInstanceSubscribeListener subscribeListener = new MyInstanceSubscribeListener();
        caCheEvent.put(serviceName + groupName, subscribeListener);

        nacosManager.getNamingService().subscribe(serviceName, groupName, clusters, subscribeListener);
    }

    /**
     * 取消监听服务：取消监听服务下的实例列表变化。
     * 
     * 取消订阅后，nacos控制台中的，订阅者列表还能查询到，但是确实服务上线，下线都不会进行监听回调了。
     * 
     * @param serviceName
     * @param groupName
     *            默认值：DEFAULT_GROUP
     * @param clusters
     *            默认值：空集合
     * @throws NacosException
     */
    @PostMapping("/instance/unsubscribe")
    public void unsubscribe(@RequestParam String serviceName, @RequestParam(required = false) String groupName,
        @RequestParam(required = false) List<String> clusters) throws NacosException {
        if (groupName == null) {
            groupName = Constants.DEFAULT_GROUP;
        }

        if (clusters == null) {
            clusters = new ArrayList<>();
        }

        AbstractEventListener listener = caCheEvent.get(serviceName + groupName);
        if (listener != null) {
            nacosManager.getNamingService().unsubscribe(serviceName, groupName, clusters, listener);
        }
    }
}

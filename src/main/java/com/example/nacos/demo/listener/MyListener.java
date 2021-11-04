package com.example.nacos.demo.listener;

import java.util.concurrent.Executor;

import com.alibaba.nacos.api.config.listener.Listener;

/**
 * @author honglixiang@tiduyun.com
 * @date 2021/11/3
 */
public class MyListener implements Listener {

    @Override
    public Executor getExecutor() {
        return null;
    }

    @Override
    public void receiveConfigInfo(String configInfo) {
        System.err.println("=========配置已更新=========");
        System.err.println(configInfo);
    }
}

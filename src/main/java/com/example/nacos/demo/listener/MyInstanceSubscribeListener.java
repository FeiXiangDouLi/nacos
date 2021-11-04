package com.example.nacos.demo.listener;

import com.alibaba.nacos.api.naming.listener.AbstractEventListener;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.NamingEvent;

/**
 * @author honglixiang@tiduyun.com
 * @date 2021/11/3
 */
public class MyInstanceSubscribeListener extends AbstractEventListener {

    @Override
    public void onEvent(Event event) {
        System.err.println(event);
        NamingEvent namingEvent = (NamingEvent)event;
        System.err.println(namingEvent.getServiceName());
        System.err.println(namingEvent.getInstances());
    }
}

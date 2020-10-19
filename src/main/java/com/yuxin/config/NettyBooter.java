package com.yuxin.config;

import com.yuxin.netty.WebSocketServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class NettyBooter implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            // 启动服务器
            try {
                WebSocketServer.getInstance().start();
            } catch (Exception e) {
                System.err.println("服务器启动失败。。。");
                e.printStackTrace();
            }

        }
    }
}

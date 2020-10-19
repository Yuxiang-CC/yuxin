package com.yuxin.netty;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户id和channel关联类
 */
public class UserChannelRel {
    private static Map<String, Channel> manage = new ConcurrentHashMap<>(16);

    public static void put(String senderId, Channel channel) {
        manage.put(senderId, channel);
    }

    public static Channel get(String senderId) {
        return manage.get(senderId);
    }

    public static void output() {
        Set<Map.Entry<String, Channel>> entries = manage.entrySet();

        for (Map.Entry<String, Channel> entry : entries) {
            System.out.println("UserId" + entry.getKey() + "，channel：" + entry.getValue().id().asLongText());
        }

    }
}

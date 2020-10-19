package com.yuxin.enums;

public enum MsgActionEnum {

    CONNECT(1, "第一次（或重连）初始化连接"),
    CHAT(2, "聊天消息"),
    SIGNED(3, "消息签收"),
    KEEPALIVE(4, "客户端保持心跳"),
    PULL_FREIND(5, "拉取好友信息");


    MsgActionEnum(Integer type, String content) {
        this.type = type;
        this.content = content;
    }

    public final Integer type;
    public final String content;
}

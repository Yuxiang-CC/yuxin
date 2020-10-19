package com.yuxin.netty;

import lombok.Data;

@Data
public class ChatMsg {

    private String sendUserId;//发送者id

    private String receiverId;//接收者id

    private String msg;//聊天内容

    private String msgId;//消息的签收

}
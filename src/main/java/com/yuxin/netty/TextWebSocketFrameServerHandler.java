package com.yuxin.netty;

import com.alibaba.fastjson.JSON;
import com.yuxin.enums.MsgActionEnum;
import com.yuxin.service.UserService;
import com.yuxin.utils.SpringUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * 因为WebSocket是以帧的形式发送数据
 * TextWebSocketFrame 表示一个文本帧
 */
public class TextWebSocketFrameServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //定义一个channel，管理所有的channel
    // GlobalEventExecutor.INSTANCE 是一个全局事件执行器，是一个单例
    //此处也可以自定义一个 集合存储所有的channel，但是比较麻烦，我们可以用netty提供的channelGroup
    public static ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 1.获取客户端传输消息
        String content = msg.text();
        // 2.判断消息类型，根据不同的类型来处理不同的业务
        DataContent dataContent = JSON.parseObject(content, DataContent.class);
        Integer action = dataContent.getAction();

        if (action == MsgActionEnum.CONNECT.type) {
            // 2.1 当websocket 第一次open时候，初始化channel，将channel与用户id关联
            UserChannelRel.put(dataContent.getChatMsg().getSendUserId(), ctx.channel());

            System.out.println("*********************测试*********************");
            //测试
            for (Channel user : users) {
                System.out.println(user.id().asLongText());
            }
            UserChannelRel.output();
            System.out.println("*********************测试*********************");


        } else if (action == MsgActionEnum.CHAT.type) {
            // 2.2 聊天类型的消息，把聊天记录保存到数据库，同时标记消息的签收状态【未签收】
            ChatMsg chatMsg = dataContent.getChatMsg();
            String msgText  = chatMsg.getMsg();
            String receiverId = chatMsg.getReceiverId();
            String sendUserId = chatMsg.getSendUserId();
            // 保存消息到数据库，并且标记为未签收
            UserService userService = (UserService) SpringUtils.getApplicationContext().getBean("userServiceImpl");
            String msgId = userService.saveMsg(chatMsg);
            chatMsg.setMsgId(msgId);

            DataContent dataContent1Msg = new DataContent();
            dataContent1Msg.setChatMsg(chatMsg);

            // 发送消息
            // 从全局用户Channel 关系中获取接收方的channel
            Channel receiverChannel = UserChannelRel.get(receiverId);
            if (receiverChannel == null) {
                // TODO channel为空代表用户离线，推送消息
                System.out.println("为空:" + receiverId);

            } else {
                // 当receiverChannel 不为空的时候，从ChannelGroup去查找对象的channel是否存在
                Channel findChannel = users.find(receiverChannel.id());
                if (findChannel != null) {
                    // 用户在线
                    receiverChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(dataContent1Msg)));

                } else {
                    // 用户离线 TODO 推送
                    System.out.println("离线：");
                }
            }

        } else if (action == MsgActionEnum.SIGNED.type) {
            // 2.3 签收消息类型，针对具体的消息进行签收，【已签收】
            UserService userService = (UserService) SpringUtils.getApplicationContext().getBean("userServiceImpl");
            // 扩展字段在signed类型的消息种，代表需要去签收的消息id，逗号间隔
            String[] msgIdsStr = dataContent.getExtand().split(",");
            List<String> msgIdList = new ArrayList<>();
            for (String mid : msgIdsStr) {
                if (mid != null && !"".equals(mid)) {
                    msgIdList.add(mid);
                }
            }
            System.err.println("签收消息id：" + msgIdList.toString());
            if (msgIdList != null && !msgIdList.isEmpty() && msgIdList.size() > 0) {
                // 批量签收
                userService.updateMsgSigned(msgIdList);
            }

        } else if (action == MsgActionEnum.KEEPALIVE.type) {
            // 2.4 心跳类型的消息
            System.err.println("接收到心跳包!");

        }
    }

    /**
     * 当web 客户端连接后触发该方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      Channel channel = ctx.channel();
      System.out.println("客户端连接:" + channel.id().asLongText());
      users.add(channel);
    }

    /**
     * 当手动清理到客户端后台进程时，会触发该方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 当触发handlerRemoved，channelGroup会自动移除channel
        System.out.println("移除客户端连接：" + ctx.channel().id().asLongText());
        users.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 发生异常之后关闭连接，随后从channelgorup中移除
        ctx.close();
        users.remove(ctx.channel());
    }
}

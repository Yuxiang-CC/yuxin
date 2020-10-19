package com.yuxin.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            //将 evt向下转型
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    System.out.println("进入读空闲...");
                    break;
                case WRITER_IDLE:
                    System.out.println("进入写空闲...");
                    break;
                case ALL_IDLE:
                    System.out.println("关闭前数量:" + TextWebSocketFrameServerHandler.users.size());
                    Channel channel = ctx.channel();
                    // 关闭不用的channel，以防资源浪费
                    channel.close();
                    System.out.println("关闭后数量:" + TextWebSocketFrameServerHandler.users.size());
                    break;
            }


        }
    }


    /**
     * 发生异常时，会执行该方法
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}

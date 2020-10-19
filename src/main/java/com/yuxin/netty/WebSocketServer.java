package com.yuxin.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class WebSocketServer {

    private static class SingletionWebSocketServer {
        static final WebSocketServer instance = new WebSocketServer();
    }

    public static WebSocketServer getInstance() {
        return SingletionWebSocketServer.instance;
    }

    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
    private ServerBootstrap serverBootstrap = new ServerBootstrap();
    private ChannelFuture future;
/*    private final String host;
    private final int port;*/

/*
    public WebSocketServer(String host, int port) {
        this.host = host;
        this.port = port;
    }*/

    public WebSocketServer() {
        this.bossGroup = new NioEventLoopGroup();
        this.workGroup = new NioEventLoopGroup();
        this.serverBootstrap.group(bossGroup, workGroup)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    ChannelPipeline pipeline = ch.pipeline();
                                    /**
                                     * 因为我们是http协议所以需要HTTP协议的编解码器
                                     */
                                    pipeline.addLast(new HttpServerCodec());
                                    //以块的方式，添加一个处理器
                                    pipeline.addLast(new ChunkedWriteHandler());
                                    /**
                                     * 当使用http协议发送大数据包时，会请求多次
                                     * 使用 可以将包聚合
                                     */
                                    pipeline.addLast(new HttpObjectAggregator(1024 * 64));
                                    /**
                                     * 对于webSocket 数据是以帧(frame)的方式传递，
                                     * 浏览器请求时：ws://localhost:7000/请求url,跟以下路径一样
                                     * 核心功能:将http协议，升级为ws协议长连接
                                     */
                                    pipeline.addLast(new WebSocketServerProtocolHandler("/websocket"));

                                    //自定义handler，处理业务逻辑
                                    pipeline.addLast(new TextWebSocketFrameServerHandler());

                                    /**
                                     * readerdletime: 当多少秒服务器没有读取，就会发送一个心跳检测包
                                     * writerldleTime: 当多少秒服务器没有写操作，就会发送一个心跳检测包
                                     * alldelTime: 当多少时间即没有读取，也没有写，就会发送一个心跳检测包
                                     */
                                    pipeline.addLast(new IdleStateHandler(30, 40, 60 * 30, TimeUnit.SECONDS));

                                    // 自定义心跳机制检测handler
                                    pipeline.addLast(new HeartBeatServerHandler());

                                }
                            });
    }

    public void start() {
        this.future = serverBootstrap.bind(8088);
        System.err.println("netty websocket server start...port : 8080 " );

    }

}

package com.ctrip.framework.traffic.netty.server;

import com.ctrip.framework.traffic.controller.ServerVO;
import com.ctrip.framework.traffic.netty.codec.PacketDecoder;
import com.ctrip.framework.traffic.netty.codec.PacketEncoder;
import com.ctrip.framework.traffic.netty.codec.Splitter;
import com.ctrip.framework.traffic.netty.server.handler.MessageRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jixinwang on 2023/9/6
 */
public class NettyServer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String host;
    private int port;

    public NettyServer(ServerVO serverVO) {
        this.host = serverVO.getHost();
        this.port = serverVO.getPort();
    }

    private ServerBootstrap getServerBootstrap() {
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(boosGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(new Splitter());
                        ch.pipeline().addLast(new PacketDecoder());
                        ch.pipeline().addLast(new MessageRequestHandler());
                        ch.pipeline().addLast(new PacketEncoder());
                    }
                });
        return serverBootstrap;
    }

    public void start() {
        ServerBootstrap serverBootstrap = getServerBootstrap();
        serverBootstrap.bind(host, port).addListener(future -> {
            if (future.isSuccess()) {
                logger.info("[server] start listen, host: {}, port: {} bind success", host, port);
            } else {
                logger.error("[server] start listen, host: {}, port: {} bind error", host, port);
            }
        });
    }
}

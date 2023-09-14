package com.ctrip.framework.traffic.netty.client;

import com.ctrip.framework.traffic.controller.ClientVO;
import com.ctrip.framework.traffic.netty.client.handler.MessageResponseHandler;
import com.ctrip.framework.traffic.netty.codec.PacketDecoder;
import com.ctrip.framework.traffic.netty.codec.PacketEncoder;
import com.ctrip.framework.traffic.netty.codec.Splitter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by jixinwang on 2023/9/6
 */
public class NettyClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    public static final AttributeKey<NettyClient> CLIENT_KEY = AttributeKey.valueOf("client");

    private Bootstrap bootstrap;

    private ClientVO clientVO;
    private String serverHost;
    private int serverPort;

    private int clientId;

    public NettyClient(ClientVO clientVO, int clientId) {
        this.clientVO = clientVO;
        this.serverHost = clientVO.getServerHost();
        this.serverPort = clientVO.getServerPort();
        this.clientId = clientId;
    }

    public void start() {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new Splitter());
                        ch.pipeline().addLast(new PacketDecoder());
                        ch.pipeline().addLast(new IdleStateHandler(10, 0, 0));
                        ch.pipeline().addLast(new MessageResponseHandler(clientVO, clientId));
                        ch.pipeline().addLast(new PacketEncoder());
                    }
                });
        connect();
    }

    public void connect() {
        logger.info("[client][{}] connect start, host: {}, port: {} ......", clientId, serverHost, serverPort);
        ChannelFuture channelFuture = bootstrap.connect(serverHost, serverPort).addListener(future -> {
            if (future.isSuccess()) {
                logger.info("[client][{}] connect success, host: {}, port: {} ......", clientId, serverHost, serverPort);
            } else {
                logger.info("[client][{}] connect retry, host: {}, port: {} ......", clientId, serverHost, serverPort);
                bootstrap.config().group().schedule(this::connect, 2, TimeUnit.SECONDS);
            }
        });
        channelFuture.channel().attr(CLIENT_KEY).set(this);
    }
}

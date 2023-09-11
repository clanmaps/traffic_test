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
    private String host;
    private int port;
    private int bandWidth;
    private int period;

    public NettyClient(ClientVO clientVO) {
        this.host = clientVO.getServerHost();
        this.port = clientVO.getServerPort();
        this.bandWidth = clientVO.getBandWidth();
        this.period = clientVO.getPeriod();
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
                        ch.pipeline().addLast(new IdleStateHandler(5, 0, 0));
                        ch.pipeline().addLast(new MessageResponseHandler(bandWidth, period));
                        ch.pipeline().addLast(new PacketEncoder());
                    }
                });
        connect();
    }

    public void connect() {
        logger.info("[client] connect start, host: {}, port: {} ......", host, port);
        ChannelFuture channelFuture = bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                logger.info("[client] connect success, host: {}, port: {} ......", host, port);
            } else {
                logger.info("[client] connect retry, host: {}, port: {} ......", host, port);
                bootstrap.config().group().schedule(this::connect, 2, TimeUnit.SECONDS);
            }
        });
        channelFuture.channel().attr(CLIENT_KEY).set(this);
    }
}

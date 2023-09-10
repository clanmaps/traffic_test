package com.ctrip.framework.traffic.netty.client;

import com.ctrip.framework.traffic.controller.ClientVO;
import com.ctrip.framework.traffic.netty.client.handler.MessageResponseHandler;
import com.ctrip.framework.traffic.netty.codec.PacketDecoder;
import com.ctrip.framework.traffic.netty.codec.PacketEncoder;
import com.ctrip.framework.traffic.netty.codec.Splitter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by jixinwang on 2023/9/6
 */
public class NettyClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final int MAX_RETRY = 5;
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

        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new Splitter());
                        ch.pipeline().addLast(new PacketDecoder());
                        ch.pipeline().addLast(new MessageResponseHandler(bandWidth, period));
                        ch.pipeline().addLast(new PacketEncoder());
                    }
                });

        connect(bootstrap, host, port, MAX_RETRY);
    }

    private void connect(Bootstrap bootstrap, String host, int port, int retry) {
        logger.info("[client] connect use, host: {}, port: {} ......", host, port);
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                logger.info("[client] connect success, host: {}, port: {} ......", host, port);
            } else if (retry == 0) {
                logger.error("[client] connect error for up to retry time, host: {}, port: {} ......", host, port);
            } else {
                // 第几次重连
                int order = (MAX_RETRY - retry) + 1;
                // 本次重连的间隔
                int delay = 1 << order;
                logger.info("[client] connect retry {} time, host: {}, port: {} ......", order, host, port);
                bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit
                        .SECONDS);
            }
        });
    }
}

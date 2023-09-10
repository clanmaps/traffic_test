package com.ctrip.framework.traffic.netty.client.handler;

import com.ctrip.framework.traffic.netty.client.Message;
import com.ctrip.framework.traffic.netty.client.MessageImpl;
import com.ctrip.framework.traffic.netty.protocol.response.MessageResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jixinwang on 2023/9/7
 */
public class MessageResponseHandler extends SimpleChannelInboundHandler<MessageResponsePacket> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Message message;

    public MessageResponseHandler(int bandWidth, int period) {
        message = new MessageImpl(bandWidth, period);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        message.start();
        message.send(ctx.channel());
        ctx.fireChannelActive();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageResponsePacket messageResponsePacket) {
        logger.info("[client] received seq: {}", messageResponsePacket.getSeq());
        message.receive(messageResponsePacket);
    }
}

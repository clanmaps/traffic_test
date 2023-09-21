package com.ctrip.framework.traffic.netty.server.handler;

import com.ctrip.framework.traffic.netty.protocol.request.MessageRequestPacket;
import com.ctrip.framework.traffic.netty.protocol.response.MessageResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jixinwang on 2023/9/7
 */
public class MessageRequestHandler extends SimpleChannelInboundHandler<MessageRequestPacket> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageRequestPacket messageRequestPacket) {
        MessageResponsePacket messageResponsePacket = new MessageResponsePacket();
        logger.debug("[server] received seq: {}", messageRequestPacket.getSeq());
        messageResponsePacket.setSeq(messageRequestPacket.getSeq());

        ctx.channel().writeAndFlush(messageResponsePacket);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("[server] remote client connected, {} -> {}", ctx.channel().remoteAddress(), ctx.channel().localAddress());
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("[server] remote client disconnected, {} -> {}", ctx.channel().remoteAddress(), ctx.channel().localAddress());
        ctx.fireChannelInactive();
    }
}

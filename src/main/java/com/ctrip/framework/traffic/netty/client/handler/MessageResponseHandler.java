package com.ctrip.framework.traffic.netty.client.handler;

import com.ctrip.framework.traffic.netty.client.Message;
import com.ctrip.framework.traffic.netty.client.MessageImpl;
import com.ctrip.framework.traffic.netty.client.NettyClient;
import com.ctrip.framework.traffic.netty.protocol.response.MessageResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ctrip.framework.traffic.netty.client.NettyClient.CLIENT_KEY;

/**
 * Created by jixinwang on 2023/9/7
 */
public class MessageResponseHandler extends SimpleChannelInboundHandler<MessageResponsePacket> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Message message;

    private boolean sendStarted;

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
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        message.stop();
        sendStarted = false;
        ctx.fireChannelInactive();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageResponsePacket messageResponsePacket) {
        logger.info("[client] received seq: {}", messageResponsePacket.getSeq());
        message.receive(messageResponsePacket);
        if (!sendStarted) {
            sendStarted = true;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("[Caught] exception", cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE && sendStarted) {
                logger.error("[READER_IDLE] fire and close channel");
                ctx.channel().close();
                Attribute<NettyClient> clientAttribute = ctx.channel().attr(CLIENT_KEY);
                NettyClient client = clientAttribute.get();
                client.connect();
            }
        } else {
            logger.info("receive {} event for {}", evt.toString(), ctx.channel().toString());
        }
        ctx.fireUserEventTriggered(evt);
    }
}

package com.ctrip.framework.traffic.netty.client;

import com.ctrip.framework.traffic.netty.protocol.request.MessageRequestPacket;
import com.ctrip.framework.traffic.netty.protocol.response.MessageResponsePacket;
import io.netty.channel.Channel;

/**
 * Created by jixinwang on 2023/9/8
 */
public interface Message {

    void start();
    void send(Channel channel);
    void receive(MessageResponsePacket response);
    void stop();
}

package com.ctrip.framework.traffic.netty.protocol.response;

import com.ctrip.framework.traffic.netty.protocol.Packet;

import static com.ctrip.framework.traffic.netty.protocol.command.Command.MESSAGE_RESPONSE;

/**
 * Created by jixinwang on 2023/9/6
 */
public class MessageResponsePacket extends Packet {

    private int seq;

    private String message;

    @Override
    public Byte getCommand() {

        return MESSAGE_RESPONSE;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}

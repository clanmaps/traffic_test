package com.ctrip.framework.traffic.netty.protocol.request;

import com.ctrip.framework.traffic.netty.protocol.Packet;

import static com.ctrip.framework.traffic.netty.protocol.command.Command.MESSAGE_REQUEST;

/**
 * Created by jixinwang on 2023/9/6
 */
public class MessageRequestPacket extends Packet {

    private int seq;

    private String message;

    public MessageRequestPacket() {
    }

    public MessageRequestPacket(String message) {
        this.message = message;
    }

    public MessageRequestPacket(int seq, String message) {
        this.seq = seq;
        this.message = message;
    }

    @Override
    public Byte getCommand() {
        return MESSAGE_REQUEST;
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

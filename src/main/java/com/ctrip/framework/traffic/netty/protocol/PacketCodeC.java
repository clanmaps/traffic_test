package com.ctrip.framework.traffic.netty.protocol;

import com.ctrip.framework.traffic.netty.protocol.request.MessageRequestPacket;
import com.ctrip.framework.traffic.netty.protocol.response.MessageResponsePacket;
import com.ctrip.framework.traffic.netty.serialize.Serializer;
import com.ctrip.framework.traffic.netty.serialize.impl.JSONSerializer;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

import static com.ctrip.framework.traffic.netty.protocol.command.Command.*;

/**
 * Created by jixinwang on 2023/9/6
 */
public class PacketCodeC {

    public static final PacketCodeC INSTANCE = new PacketCodeC();

    private final Map<Byte, Class<? extends Packet>> packetTypeMap;
    private final Serializer serializer;


    private PacketCodeC() {
        packetTypeMap = new HashMap<>();
        packetTypeMap.put(MESSAGE_REQUEST, MessageRequestPacket.class);
        packetTypeMap.put(MESSAGE_RESPONSE, MessageResponsePacket.class);

        serializer = new JSONSerializer();
    }

    public void encode(ByteBuf byteBuf, Packet packet) {
        // 1. 序列化 java 对象
        byte[] bytes = Serializer.DEFAULT.serialize(packet);

        // 2. 实际编码过程
        byteBuf.writeByte(packet.getCommand());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }


    public Packet decode(ByteBuf byteBuf) {

        // 指令
        byte command = byteBuf.readByte();

        // 数据包长度
        int length = byteBuf.readInt();

        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        Class<? extends Packet> requestType = getRequestType(command);

        if (requestType != null && serializer != null) {
            return serializer.deserialize(requestType, bytes);
        }

        return null;
    }

    private Class<? extends Packet> getRequestType(byte command) {

        return packetTypeMap.get(command);
    }
}

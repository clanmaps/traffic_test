package com.ctrip.framework.traffic.netty.protocol;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by jixinwang on 2023/9/6
 */
public abstract class Packet {

    @JSONField(serialize = false)
    public abstract Byte getCommand();
}

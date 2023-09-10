package com.ctrip.framework.traffic.netty.serialize.impl;

import com.alibaba.fastjson.JSON;
import com.ctrip.framework.traffic.netty.serialize.Serializer;

/**
 * Created by jixinwang on 2023/9/6
 */
public class JSONSerializer implements Serializer {

    @Override
    public byte[] serialize(Object object) {

        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {

        return JSON.parseObject(bytes, clazz);
    }
}

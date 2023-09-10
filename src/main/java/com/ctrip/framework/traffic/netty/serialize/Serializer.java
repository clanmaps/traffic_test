package com.ctrip.framework.traffic.netty.serialize;


import com.ctrip.framework.traffic.netty.serialize.impl.JSONSerializer;

/**
 * Created by jixinwang on 2023/9/6
 */
public interface Serializer {

    Serializer DEFAULT = new JSONSerializer();

    /**
     * java 对象转换成二进制
     */
    byte[] serialize(Object object);

    /**
     * 二进制转换成 java 对象
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);

}

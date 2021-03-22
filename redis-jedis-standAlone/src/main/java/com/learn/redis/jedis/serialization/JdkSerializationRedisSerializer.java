package com.learn.redis.jedis.serialization;

import com.sun.xml.internal.ws.encoding.soap.SerializationException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;

public class JdkSerializationRedisSerializer  {
    private static final byte[] EMPTY_ARRAY = new byte[0];

    private final Converter<Object, byte[]> serializer = new SerializingConverter();
    private final Converter<byte[], Object> deserializer = new DeserializingConverter();

    public Object deserialize(byte[] bytes) {
        if (null == bytes || bytes.length == 0) {
            return null;
        }
        try {
            return deserializer.convert(bytes);
        } catch (Exception ex) {
            throw new SerializationException("Cannot deserialize", ex);
        }
    }

    public byte[] serialize(Object object) {
        if (object == null) {
            return EMPTY_ARRAY;
        }
        try {
            return serializer.convert(object);
        } catch (Exception ex) {
            throw new SerializationException("Cannot serialize", ex);
        }
    }
}
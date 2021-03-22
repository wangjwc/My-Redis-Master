package com.learn.redis.jedis.bloom.bitarr;

import redis.clients.jedis.BitOP;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jiangtiteng
 */
public class JedisBitArray extends AbstractRedisBitArray {
    @FunctionalInterface
    public interface JedisRunable<T> {
        T run(Jedis jedis) throws Exception;
    }

    private JedisPool jedisPool;

    private JedisBitArray() {
        super(null);
    }

    public JedisBitArray(JedisPool jedisPool, String prefix) {
        super(prefix);
        this.jedisPool = jedisPool;
    }

    private <T> T execute(JedisRunable<T> runnable) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            return runnable.run(jedis);
        }
    }

    @Override
    public long bitCount() throws Exception {
        return execute(jedis -> {
            return jedis.bitcount(key);
        });
    }

    @Override
    public boolean set(long index) throws Exception {
        boolean result;
        result = execute(jedis -> {
            // setbit命令返回bit位原本的值，如果为false说明是第一次设置
            boolean oldValue = jedis.setbit(key, index, true);
            return !oldValue;
        });
        return result;
    }

    @Override
    public boolean get(long index) throws Exception {
        return (Boolean) execute(jedis -> jedis.getbit(key, index));
    }

    @Override
    public List<Boolean> batchGet(List<Long> indexs) throws Exception {
        if (null == indexs || indexs.isEmpty()) {
            return Collections.emptyList();
        }

        return execute(jedis -> {
            Pipeline pipeline = jedis.pipelined();
            indexs.forEach(index -> pipeline.getbit(key, index));
            List<Object> result = pipeline.syncAndReturnAll();
            return result.stream().map(o -> {
                if (o instanceof Boolean) {
                    return (Boolean)o;
                }
                return false;
            }).collect(Collectors.toList());
        });
    }

    @Override
    public List<Boolean> batchSet(List<Long> indices) throws Exception {
        if (null == indices || indices.isEmpty()) {
            return Collections.emptyList();
        }

        return execute(jedis -> {
            Pipeline pipeline = jedis.pipelined();
            indices.forEach(index -> pipeline.setbit(key, index, true));
            List<Object> result = pipeline.syncAndReturnAll();

            return result.stream().map(o -> {
                if (o instanceof Boolean) {
                    return (Boolean)o;
                }
                return false;
            }).collect(Collectors.toList());
        });
    }
}

package com.learn.redis.jedis.bloom.bitarr;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.util.List;

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
    public boolean batchSupport() {
        return true;
    }

    @Override
    public void reset() {
        try {
            execute(jedis -> jedis.del(key));
        } catch (Exception e) {
            throw new RuntimeException("del bit error", e);
        }
    }

    @Override
    public boolean set(long index) throws Exception {
//        boolean result;
//        result = execute(jedis -> {
//            // setbit命令返回bit位原本的值，如果为false说明原本是0，返回true
//            boolean oldValue = jedis.setbit(key, index, true);
//            return !oldValue;
//        });
//        return result;
        throw new RuntimeException("not support");
    }

    @Override
    public boolean get(long index) throws Exception {
        //return (Boolean) execute(jedis -> jedis.getbit(key, index));
        throw new RuntimeException("not support");
    }

    @Override
    public boolean[] batchSet(long[] indices) throws Exception {
        if (null == indices || indices.length == 0) {
            return new boolean[0];
        }

        return execute(jedis -> {
            Pipeline pipeline = jedis.pipelined();
            for (long index : indices) {
                pipeline.setbit(key, index, true);
            }
            boolean[] arr = bitOpResult(pipeline.syncAndReturnAll());
            for (int i = 0; i < arr.length; i++) {
                // setbit命令返回bit位原本的值，如果为false说明原本是0，返回true
                arr[i] = !arr[i];
            }
            return arr;
        });
    }

    @Override
    public boolean[] batchGet(long[] indices) throws Exception {
        if (null == indices || indices.length == 0) {
            return new boolean[0];
        }

        return execute(jedis -> {
            Pipeline pipeline = jedis.pipelined();
            for (long index : indices) {
                pipeline.getbit(key, index);
            }
            return bitOpResult(pipeline.syncAndReturnAll());
        });
    }



    boolean[] bitOpResult(List<Object> list) {
        boolean[] res = new boolean[list.size()];
        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            res[i] = (o instanceof Boolean) ? (Boolean)o : false;
        }
        return res;
    }
}

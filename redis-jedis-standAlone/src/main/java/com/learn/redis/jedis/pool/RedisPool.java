package com.learn.redis.jedis.pool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author wangjingwang
 * @version v1.0
 */
public class RedisPool {
    public static final JedisPool pool = new JedisPool(new JedisPoolConfig(), "127.0.0.1", 6379, 1000, null);

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("RedisPool shutdown hook");
            pool.close();
            System.out.println("RedisPool shutdown hook success");
        }));
    }

    public static Jedis getResource() {
        return pool.getResource();
    }
}

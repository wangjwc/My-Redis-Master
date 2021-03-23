package com.learn.redis.jedis.pool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author wangjingwang
 * @version v1.0
 */
public class RedisPool {
    private static final String host = "127.0.0.1";
    private static final int port = 6379;
    private static final int connectionTimeout = 1000; // 毫秒
    private static final int soTimeout = 1000; // 毫秒
    private static final int infiniteSoTimeout = 1000;
    private static final String user = null;
    private static final String password = null;
    private static final int database = 0;
    private static final String clientName = "";

    public static final JedisPool pool = new JedisPool(new JedisPoolConfig(),
            host, port, connectionTimeout, soTimeout, user, password, database, clientName);

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




    /*
     public JedisPool(final GenericObjectPoolConfig poolConfig, final String host, int port,
      final int connectionTimeout, final int soTimeout, final String password, final int database,
      final String clientName) {
        super(poolConfig, new JedisFactory(host, port, connectionTimeout, soTimeout, password,
        database, clientName));
    }
     */
}

package com.learn.redis.jedis.cluster;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * @author wangjingwang
 * @version v1.0
 */
public class Cluster {
    private static int MAXTOTAL = 300;
    private static int MAXIDLE = 200;
    private static int MINIDEL = 10;
    private static int MAXWAIRMILLIS = 1000;
    private static Boolean TESTONBORROW = true;
    private static Boolean TESTONRETURN = false;
    private static Boolean TESTWHILEIDLE = false;

    private static int connectionTimeout = 1000;
    private static int soTimeout = 1500;
    private static int maxAttempts = 5;
    private static JedisPoolConfig poolConfig = initConfig();

    public static final JedisCluster instance;
    static {
        /*
         * 1、jedisClusterNode用于初始化连接（执行cluster slots）其实没必要把所有节点都列出来，但为了可用性更高，也不能只写一个
         * 2、JedisCluster内部维护了各节点的连接池和slot-pool映射关系，不需要再额外使用连接池
         * 3、JedisCluster封装了命令执行，不需要手工关闭连接和连接池
         */
        Set<HostAndPort> jedisClusterNode = new HashSet<>();
        // 主
        jedisClusterNode.add(new HostAndPort("127.0.0.1", 7001));
        jedisClusterNode.add(new HostAndPort("127.0.0.1", 7002));
        jedisClusterNode.add(new HostAndPort("127.0.0.1", 7003));
        // 从
        jedisClusterNode.add(new HostAndPort("127.0.0.1", 7004));
        jedisClusterNode.add(new HostAndPort("127.0.0.1", 7005));
        jedisClusterNode.add(new HostAndPort("127.0.0.1", 7006));
        instance = new JedisCluster(jedisClusterNode, connectionTimeout, soTimeout, maxAttempts, poolConfig);
    }

    public static JedisPoolConfig initConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(MAXTOTAL);
        config.setMaxIdle(MAXIDLE);
        config.setMinIdle(MINIDEL);
        config.setMaxWaitMillis(MAXWAIRMILLIS);
        config.setTestOnBorrow(TESTONBORROW);
        config.setTestOnReturn(TESTONRETURN);
        config.setTestWhileIdle(TESTWHILEIDLE);
        return config;
    }
}

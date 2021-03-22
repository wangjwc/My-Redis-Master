package com.learn.redis.jedis;

import redis.clients.jedis.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestCluster {

    private static ShardedJedisPool pool;
    private static int MAXTOTAL = 300;
    private static int MAXIDLE = 200;
    private static int MINIDEL = 10;
    private static int MAXWAIRMILLIS = 1000;
    private static Boolean TESTONBORROW = true;
    private static Boolean TESTONRETURN = false;
    private static Boolean TESTWHILEIDLE = false;

    private static List<JedisShardInfo> initShardInfos(String hostsStr) {
        if (null == hostsStr || (hostsStr = hostsStr.trim()).isEmpty()) {
            throw new NullPointerException("redis host not found");
        }
        Set<String> hosts = new HashSet<String>();
        String[] sentinelArray = hostsStr.split(",");
        for (String str : sentinelArray) {
            hosts.add(str);
        }

        List<JedisShardInfo> shardInfos = new ArrayList<JedisShardInfo>();
        for (String hs : hosts) {
            String[] values = hs.split(":");
            JedisShardInfo shard = new JedisShardInfo(values[0], Integer.parseInt(values[1]));
            if (values.length > 2) {
                shard.setPassword(values[2]);
            }
            shardInfos.add(shard);
        }
        return shardInfos;
    }

    private static JedisPoolConfig initConfig() {
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


    public static void main(String[] args) {
        String host = "localhost:6379:testredis,localhost:6380:testredis,localhost:6381:testredis";//服务器地址,密码
        JedisPoolConfig config = initConfig();
        List<JedisShardInfo> shardInfos = initShardInfos(host);
        pool = new ShardedJedisPool(config, shardInfos);
        ShardedJedis jedis = pool.getResource();
        jedis.get("a");
        jedis.close();
        pool.destroy();

        /**
         * 非连接池
         */

        // 第一步：使用JedisCluster对象。需要一个Set<HostAndPort>参数。Redis节点的列表。
        Set<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort("192.168.00.000", 7001));
        nodes.add(new HostAndPort("192.168.00.000", 7002));
        nodes.add(new HostAndPort("192.168.00.000", 7003));
        nodes.add(new HostAndPort("192.168.00.000", 7004));
        nodes.add(new HostAndPort("192.168.00.000", 7005));
        nodes.add(new HostAndPort("192.168.00.000", 7006));
        JedisCluster jedisCluster = new JedisCluster(nodes);
        // 第二步：直接使用JedisCluster对象操作redis。在系统中单例存在。
        jedisCluster.set("a", "1");

        String result = jedisCluster.get("a");
        // 第三步：打印结果
        System.out.println(result);
        // 第四步：系统关闭前，关闭JedisCluster对象。
        jedisCluster.close();
    }
}

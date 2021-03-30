package com.learn.redis.jedis.cluster;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.util.StringUtils;
import redis.clients.jedis.*;
import redis.clients.jedis.util.JedisClusterCRC16;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestCluster {
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




    public static void main(String[] args) {
        int connectionTimeout = 1000;
        int soTimeout = 1500;
        int maxAttempts = 5;
        JedisPoolConfig poolConfig = Cluster.initConfig();

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
        JedisCluster jedisCluster = new JedisCluster(jedisClusterNode, connectionTimeout, soTimeout, maxAttempts, poolConfig);
        for (int i = 0; i < 100; i++) {
            String k = "k_" + i;
            System.out.println("slot ==> " + JedisClusterCRC16.getSlot(k));
            System.out.println(jedisCluster.set("k", "cluster test1"));
            System.out.println(jedisCluster.get("k"));
        }

        ScanParams scanParams = new ScanParams();
        scanParams.match("{a}*");
        scanParams.count(1);
        String cursor = "0";
        ScanResult<String> result = jedisCluster.scan(cursor, scanParams);
        do {
            System.out.println("---------------------------cursor:" + cursor + "----------------------");
            for (String s : result.getResult()) {
                System.out.println(s + " :  " + jedisCluster.get(s));
            }
            cursor = result.getCursor();
            result = jedisCluster.scan(result.getCursor(), scanParams);
        } while (!result.isCompleteIteration());


        System.out.println("\n\n---------------------------scan direct----------------------\n");
        Jedis jedis = jedisCluster.getConnectionFromSlot(JedisClusterCRC16.getSlot("{a}"));
        cursor = "0";
        result = jedis.scan(cursor, scanParams);
        do {
            System.out.println("---------------------------cursor:" + cursor + "----------------------");
            for (String s : result.getResult()) {
                System.out.println(s + " :  " + jedis.get(s));
            }
            cursor = result.getCursor();
            result = jedis.scan(result.getCursor(), scanParams);
        } while (!result.isCompleteIteration());

        jedisCluster.close();
    }
}

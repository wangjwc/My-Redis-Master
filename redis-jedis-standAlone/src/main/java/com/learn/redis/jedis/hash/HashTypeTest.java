package com.learn.redis.jedis.hash;

import com.learn.redis.jedis.pool.RedisPool;
import redis.clients.jedis.Jedis;

import java.util.stream.Collectors;

/**
 * hash field个数小于等于512时 类型是ziplist，否则是hashtable
 * @author wangjingwang
 * @version v1.0
 */
public class HashTypeTest {
    private static final String key = "test_hash";
    public static void main(String[] args){
        Jedis jedis = RedisPool.getResource();

        jedis.del(key);
        String type = null;
        for (int i = 0; i < 513; i++) {
            jedis.hset(key, "f" + i, String.valueOf(i));

            String newType = jedis.objectEncoding(key);
            if (!newType.equals(type)) {
                type = newType;
                System.out.println(jedis.hlen(key) + ":" + type);
            }
        }

        System.out.println("-------------------------------------");
        jedis.del(key);
        type = null;
        jedis.hset(key, "f1", "012345678901234567890123456789");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 65; i++) {
            sb.append("a");
            jedis.hset(key, "f2", sb.toString());

            String newType = jedis.objectEncoding(key);
            if (!newType.equals(type)) {
                type = newType;
                System.out.println(jedis.hgetAll(key).values().stream().map(String::length).reduce(Integer::sum).orElse(0) + ":"
                        + type);
            }
        }

        jedis.close();
    }
}

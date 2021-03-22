package com.learn.redis.jedis;

import com.learn.redis.jedis.pool.RedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.Set;

public class HsetAndHget {
    public static void main(String[] args) throws InterruptedException {
        Jedis jedis = RedisPool.getResource();

        jedis.hset("User","user_01","{id:user_01,name:user_1}");
        jedis.hset("User","user_02","{id:user_02,name:user_2}");

        Map<String, String> map =  jedis.hgetAll("User");
        System.out.println(map.toString());

        Set<String> keys = jedis.hkeys("User");
        System.out.println(keys.toString());

        jedis.expire("User", 2); // 过期时间只能设置给一级key
        System.out.println(jedis.hkeys("User"));
    }
}

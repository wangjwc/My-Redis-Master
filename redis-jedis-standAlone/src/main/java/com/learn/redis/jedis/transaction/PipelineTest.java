package com.learn.redis.jedis.transaction;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

import java.util.List;

public class PipelineTest {
    static JedisPool jedisPool = null;
    static {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        jedisPool = new JedisPool(poolConfig,"192.168.8.101", 6379, 2, "123456");
    }

    static void pipelined() {
        Jedis jedis = jedisPool.getResource();
        Pipeline pipeline = jedis.pipelined();

        jedis.keys("*").forEach(k -> pipeline.get(k)); // 使用管道批量执行命令（ps：这里只有get，但实际上可以有其他命令）

        List<Object> results = pipeline.syncAndReturnAll();// 返回管道中所有命令的执行结果
        results.forEach(result -> {
            System.out.println(String.valueOf(result));
        });
    }

    public static void main(String[] args) {
        pipelined();
    }
}

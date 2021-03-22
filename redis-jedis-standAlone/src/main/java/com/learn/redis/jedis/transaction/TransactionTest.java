package com.learn.redis.jedis.transaction;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.List;

public class TransactionTest {
    static JedisPool jedisPool = null;
    static {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        jedisPool = new JedisPool(poolConfig,"192.168.8.101", 6379, 2, "123456");
    }

    public static void main(String[] args) throws IOException {
        Jedis jedis = jedisPool.getResource();

        /**
         * watch的作用是：
         * 监控指定的key（key不存在不会报错），如果在事务期间，有别的事务修改了被监视key，则当前开启（或将要开启！）的事务，不会执行
         */
        jedis.watch("name","age"); // 监控name和age
        Transaction transaction = jedis.multi(); // multi开启事务

        transaction.set("name", "WangJingWang");
        transaction.incrBy("age", 20);

        System.out.print("等待输入---");
        System.in.read();// 这里等待输入，期间我们可以在命令行对name或age修改，从而使事务无效
        List<Object> results = transaction.exec(); // 执行事务中的命令列表

        System.out.println("result.size = " + results.size()); // 如果中间有别的事务修改了name或age，则这里返回空列表
        results.forEach(System.out::println);
    }
}

package com.learn.redis.jedis;

import com.learn.redis.jedis.pool.RedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

public class Publish_Subscribe {
    public static void main(String[] args) throws InterruptedException {
        JedisPoolConfig poolConfig = new JedisPoolConfig();


        final Jedis jedis = RedisPool.getResource();
        JedisPubSub pubSub = new JedisPubSub() {
            public void onMessage(String channel, String message) {
                //TODO:接收订阅频道消息后，业务处理逻辑
                System.out.println(channel + "=" + message);
            }
        };

        new Thread(() -> {
            System.out.println("订阅");
            jedis.subscribe(pubSub, "publish_test");
            System.out.println("这里是阻塞的，因此该输出不会执行");
        }).start();
        Thread.sleep(1000); // 注意，这里要等订阅线程启动，否则消息丢失

        System.out.println("发布1");
        try {
            jedis.publish("publish_test", "测试消息2");
        } catch (Exception e) {
            System.out.println("订阅中的连接无法再执行其他命令：" + e.getMessage());
        }


        System.out.println("发布2");
        RedisPool.getResource().publish("publish_test", "测试消息2");

        Thread.sleep(5000L);

        System.out.println("取消订阅");
        pubSub.unsubscribe();



    }
}

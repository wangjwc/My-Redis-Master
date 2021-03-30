package com.learn.redis.jedis;

import com.learn.redis.jedis.pool.RedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * @author wangjingwang
 * @version v1.0
 */
public class InitData {
    public static void main(String[] args){
        Jedis jedis = RedisPool.getResource();
        for (int i = 0; i < 1000; i++) {
            Pipeline pipeline = jedis.pipelined();
            for (int j = 0; j < 100; j++) {
                String k = i + "_" + j;
                pipeline.set("key_2_" + k , "value_" + k);
            }
            pipeline.syncAndReturnAll();
        }
        jedis.close();
    }
}

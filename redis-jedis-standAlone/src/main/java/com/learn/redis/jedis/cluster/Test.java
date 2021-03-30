package com.learn.redis.jedis.cluster;

import redis.clients.jedis.JedisCluster;

import java.util.Random;

/**
 * @author wangjingwang
 * @version v1.0
 */
public class Test {
    private static class Ls {
        private byte[] bytes = new byte[100000000];

        public int size() {
            return bytes.length;
        }
    }

    public static void main(String[] args){
        Ls ls = new Ls();
        long t1 = System.currentTimeMillis();
        int c = ls.size();
        for (int i = 0; i < c; i++) {
            ;
        }

        System.out.println(System.currentTimeMillis() - t1);

        t1 = System.currentTimeMillis();
        for (int j = 0; j < ls.size(); j++) {
            ;
        }
        System.out.println(System.currentTimeMillis() - t1);
    }
}

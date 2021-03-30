package com.learn.redis.jedis;

import java.nio.charset.StandardCharsets;

/**
 * @author wangjingwang
 * @version v1.0
 */
public class Test {
    public static void main(String[] args){
        // 14
        System.out.println(5 & 2);
    }

    private static void print(String s) {
        System.out.println(s + "==> utf-8 " + s.getBytes(StandardCharsets.UTF_8).length);
        System.out.println(s + "==> iso " + s.getBytes(StandardCharsets.ISO_8859_1).length);
        System.out.println(s + "==> utf-16 " + s.getBytes(StandardCharsets.UTF_16).length);
    }
}

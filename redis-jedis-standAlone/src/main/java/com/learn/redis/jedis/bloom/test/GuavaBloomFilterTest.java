package com.learn.redis.jedis.bloom.test;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author wangjingwang
 * @version v1.0
 */
public class GuavaBloomFilterTest {
    public static void main(String[] args){
        /*
         * 预估数量为100万
         * fpp=0.0000001
         */
        int expectedInsertions = 100_0000;
        double fpp = 0.000_0001;

        BloomFilter<String> bloomFilter = BloomFilter.create((from, into) -> {
            into.putString(from, StandardCharsets.UTF_8);
        },  expectedInsertions, fpp);

        String testElement1 = "123";
        String testElement2 = "456";
        String testElement3 = "789";

        bloomFilter.put(testElement1);
        bloomFilter.put(testElement2);
        System.out.println(bloomFilter.mightContain(testElement1));
        System.out.println(bloomFilter.mightContain(testElement2));
        System.out.println(bloomFilter.mightContain(testElement3));

    }
}

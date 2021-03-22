package com.learn.redis.jedis.bloom.test;

import com.learn.redis.jedis.bloom.BloomFilterStrategies;
import com.learn.redis.jedis.bloom.BloomFilter;
import com.learn.redis.jedis.bloom.bitarr.BitArray;
import com.learn.redis.jedis.bloom.bitarr.JedisBitArray;
import com.learn.redis.jedis.pool.RedisPool;

import java.nio.charset.StandardCharsets;

/**
 * @author wangjingwang
 * @version v1.0
 */
public class RedisBloomFilterTest {

    public static void main(String[] args) throws Exception {
        /*
         * 预估数量为100万
         * fpp=0.0000001
         */
        int expectedInsertions = 100_0000;
        double fpp = 0.000_0001;

        BitArray bitArray = new JedisBitArray(RedisPool.pool, "test");

        BloomFilter<String> bloomFilter = BloomFilter.create(bitArray, (from, into) -> {
            into.putString(from, StandardCharsets.UTF_8);
        },  expectedInsertions, fpp, BloomFilterStrategies.MURMUR128_MITZ_32);

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

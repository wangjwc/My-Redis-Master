package com.learn.redis.jedis.bloom.test;

import com.learn.redis.jedis.bloom.BloomFilterStrategies;
import com.learn.redis.jedis.bloom.BloomFilter;
import com.learn.redis.jedis.bloom.bitarr.BitArray;
import com.learn.redis.jedis.bloom.bitarr.JedisBitArray;
import com.learn.redis.jedis.bloom.bitarr.LockFreeBitArray;
import com.learn.redis.jedis.pool.RedisPool;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author wangjingwang
 * @version v1.0
 */
public class RedisBloomFilterTest {

    public static List<String> randomList(int size) {
        List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(UUID.randomUUID().toString());
        }
        return list;
    }

    public static void main(String[] args) throws Exception {
        /*
         * 预估数量为100万
         * fpp=0.0000001
         */
        int expectedInsertions = 100_0000;
        double fpp = 0.000_0001;
        BitArray bitArray = new JedisBitArray(RedisPool.pool, "test");
        //BitArray bitArray = new LockFreeBitArray();

        BloomFilter<String> bloomFilter = BloomFilter.create(bitArray, (from, into) -> {
            into.putString(from, StandardCharsets.UTF_8);
        },  expectedInsertions, fpp, BloomFilterStrategies.MURMUR128_MITZ_32);
        bloomFilter.reset();

        int totalElement = 10000;
        List<String> list = randomList(totalElement);
        System.out.println("prepare data");

        long t1 = System.currentTimeMillis();
        for (String s : list) {
            boolean result = bloomFilter.put(s);
            if (!result) {
                System.out.println("repeat element find ==> " + s);
            }
        }
        double duration = (System.currentTimeMillis() - t1);
        System.out.printf("put time total=%s, avg=%s \n", duration, (duration/totalElement));

        /*
         * 已添加过的元素，必须返回true
         */
        t1 = System.currentTimeMillis();
        for (String s : list) {
            if (!bloomFilter.mightContain(s)) {
                throw new RuntimeException("error");
            }
        }
        duration = (System.currentTimeMillis() - t1);
        System.out.printf("mightContain time total=%s, avg=%s \n", duration, (duration/totalElement));


        /*
         * 未添加过的元素，返回true的概率
         */
        int fp = 0;
        List<String> notInList = randomList(totalElement);

        t1 = System.currentTimeMillis();
        for (String s : notInList) {
            if (bloomFilter.mightContain(s)) {
                fp++;
            }
        }
        duration = (System.currentTimeMillis() - t1);
        System.out.printf("mightContain time total=%s, avg=%s \n", duration, (duration/totalElement));

        System.out.printf("totalElement=%s, fp = %s, fpp=%s \n\n", totalElement, fp, (((double)fp)/totalElement + "%"));
        System.out.println(bloomFilter.bitCount());
    }
}

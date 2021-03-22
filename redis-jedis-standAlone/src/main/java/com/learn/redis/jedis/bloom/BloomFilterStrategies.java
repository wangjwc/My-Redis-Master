package com.learn.redis.jedis.bloom;

import com.google.common.hash.Funnel;
import com.google.common.hash.Hashing;
import com.learn.redis.jedis.bloom.bitarr.BitArray;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author wangjingwang
 * @version v1.0
 */
public enum BloomFilterStrategies implements Strategy {
    /**
     * See "Less Hashing, Same Performance: Building a Better Bloom Filter" by Adam Kirsch and Michael
     * Mitzenmacher. The paper argues that this trick doesn't significantly deteriorate the
     * performance of a Bloom filter (yet only needs two 32bit hash functions).
     */
    MURMUR128_MITZ_32() {
        /**
         * 计算numHashFunctions次hash，交给op函数处理，当处理结果为false时直接返回false
         * @param funnel 序列化
         * @param object 元素
         * @param numHashFunctions hash次数
         * @param bitSize bit数组大小
         * @param op 操作
         * @param <T>
         * @return true or false
         */
        private <T> boolean computeHashIndex(Funnel<? super T> funnel, T object, int numHashFunctions, long bitSize, BiFunction<Integer, Long, Boolean> op) {
            // 获取一个64位long型的hash值，然后分别取高低32位获取两个32位hash值（一次得到两个hash值，效率高）
            long hash64 = Hashing.murmur3_128().hashObject(object, funnel).asLong();
            int hash1 = (int) hash64;
            int hash2 = (int) (hash64 >>> 32);

            /*
             * K次hash
             */
            for (int i = 1; i <= numHashFunctions; i++) {
                // 计算hash值得规则（
                int combinedHash = hash1 + (i * hash2);
                // 结果小于0时，通过翻转各bit位转为正数
                // Flip all the bits if it's negative (guaranteed positive number)
                if (combinedHash < 0) {
                    combinedHash = ~combinedHash;
                }

                // 根据本次计算出的hash计算bit位index
                if (!op.apply(i-1, combinedHash % bitSize)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public <T> boolean put(
                T object, Funnel<? super T> funnel, int numHashFunctions, BitArray bits) throws Exception {
            // bit数组大小（总bit位数）
            long bitSize = bits.bitSize();

            // 获取一个64位long型的hash值，然后分别取高低32位获取两个32位hash值（一次得到两个hash值，效率高）
            long hash64 = Hashing.murmur3_128().hashObject(object, funnel).asLong();
            int hash1 = (int) hash64;
            int hash2 = (int) (hash64 >>> 32);

            /*
             * K次hash
             */
            boolean bitsChanged = false;
            for (int i = 1; i <= numHashFunctions; i++) {
                // 计算hash值得规则（
                int combinedHash = hash1 + (i * hash2);
                // 结果小于0时，通过翻转各bit位转为正数
                // Flip all the bits if it's negative (guaranteed positive number)
                if (combinedHash < 0) {
                    combinedHash = ~combinedHash;
                }
                // 根据本次计算出的hash修改指定bit位，如果更新值（指定位原本为0改为了2）则设置bitsChanged=true
                bitsChanged |= bits.set(combinedHash % bitSize);
            }

            long[] indies = new long[numHashFunctions];
            computeHashIndex(funnel, object, numHashFunctions, bitSize, (i, hashIndex) -> {
                indies[i] = hashIndex;
                return true;
            });

            return bitsChanged;
        }

        @Override
        public <T> boolean mightContain(
                T object, Funnel<? super T> funnel, int numHashFunctions, BitArray bits) throws Exception {
            long bitSize = bits.bitSize();
            long hash64 = Hashing.murmur3_128().hashObject(object, funnel).asLong();
            int hash1 = (int) hash64;
            int hash2 = (int) (hash64 >>> 32);

            for (int i = 1; i <= numHashFunctions; i++) {
                int combinedHash = hash1 + (i * hash2);
                // Flip all the bits if it's negative (guaranteed positive number)
                if (combinedHash < 0) {
                    combinedHash = ~combinedHash;
                }

                // 只要有一个不符合条件的便返回false
                if (!bits.get(combinedHash % bitSize)) {
                    return false;
                }
            }
            return true;
        }
    };
}

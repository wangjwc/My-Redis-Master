package com.learn.redis.jedis.bloom;

import com.google.common.hash.Funnel;
import com.learn.redis.jedis.bloom.bitarr.BitArray;

import java.util.List;

/**
 * @author wangjingwang
 * @version v1.0
 */
public interface Strategy extends java.io.Serializable {

    /**
     * 批量添加数据（必须指定一次传输的字节大小）
     * @param dataList
     * @param batchBytes
     * @param funnel
     * @param numHashFunctions
     * @param bits
     * @param <T>
     */
    <T> void batchPut(List<T> dataList, int batchBytes, Funnel<? super T> funnel, int numHashFunctions, BitArray bits) throws Exception;

    /**
     * Sets {@code numHashFunctions} bits of the given bit array, by hashing a user element.
     *
     * <p>Returns whether any bits changed as a result of this operation.
     */
    <T> boolean put(
            T object, Funnel<? super T> funnel, int numHashFunctions, BitArray bits) throws Exception;

    /**
     * Queries {@code numHashFunctions} bits of the given bit array, by hashing a user element;
     * returns {@code true} if and only if all selected bits are set.
     */
    <T> boolean mightContain(
            T object, Funnel<? super T> funnel, int numHashFunctions, BitArray bits) throws Exception;

    /**
     * Identifier used to encode this strategy, when marshalled as part of a BloomFilter. Only
     * values in the [-128, 127] range are valid for the compact serial form. Non-negative values
     * are reserved for enums defined in BloomFilterStrategies; negative values are reserved for any
     * custom, stateful strategy we may define (e.g. any kind of strategy that would depend on user
     * input).
     */
    int ordinal();
}

package com.learn.redis.jedis.bloom.bitarr;

import java.util.List;

/**
 * Created by jiangtiteng
 */
public interface BitArray {
    void initBitArray(long bitSize);

    long bitSize();

    long bitCount() throws Exception;

    /**
     * 设置指定bit位为1，如果原本bit位是0则返回true，否则返回false
     * @param index
     * @return
     */
    boolean set(long index) throws Exception;

    /**
     * 如果指定bit为为1，返回true，否则返回false
     * @param index
     * @return
     */
    boolean get(long index) throws Exception;

    List<Boolean> batchSet(List<Long> indices) throws Exception;

    List<Boolean> batchGet(List<Long> indices) throws Exception;
}

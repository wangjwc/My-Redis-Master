package com.learn.redis.jedis.bloom.bitarr;

/**
 * Created by jiangtiteng
 */
public interface BitArray {
    void initBitArray(long bitSize);

    long bitSize();

    long bitCount() throws Exception;

    boolean batchSupport();

    void reset();

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

    /**
     * 设置指定bit位为1，如果原本bit位是0则返回true，否则返回false
     * @param indices
     * @return
     * @throws Exception
     */
    boolean[] batchSet(Long[] indices) throws Exception;

    /**
     * 如果指定bit为为1，返回true，否则返回false
     * @param indices
     * @return
     * @throws Exception
     */
    boolean[] batchGet(Long[] indices) throws Exception;
}

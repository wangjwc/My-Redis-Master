package com.learn.redis.jedis.bloom;

import com.google.common.hash.Funnel;
import com.google.common.math.DoubleMath;
import com.learn.redis.jedis.bloom.bitarr.BitArray;

import java.math.RoundingMode;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author wangjingwang
 * @version v1.0
 */
public class BloomFilter<T> {
    public static <T> BloomFilter<T> create(BitArray bitArray, Funnel<? super T> funnel, long expectedInsertions, double fpp,
                                            Strategy strategy) {
        checkNotNull(funnel);
        checkArgument(
                expectedInsertions >= 0, "Expected insertions (%s) must be >= 0", expectedInsertions);
        checkArgument(fpp > 0.0, "False positive probability (%s) must be > 0.0", fpp);
        checkArgument(fpp < 1.0, "False positive probability (%s) must be < 1.0", fpp);
        checkNotNull(strategy);

        if (expectedInsertions == 0) {
            expectedInsertions = 1;
        }

        /*
         * 根据预期插入数量和假阳性概率计算最佳的bit数组大小和最佳hash函数个数
         */
        long numBits = optimalNumOfBits(expectedInsertions, fpp);
        int numHashFunctions = optimalNumOfHashFunctions(expectedInsertions, numBits);

        bitArray.initBitArray(numBits);
        try {
            return new BloomFilter<T>(bitArray, numHashFunctions, funnel, strategy);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not create BloomFilter of " + numBits + " bits", e);
        }
    }

    /**布隆过滤器的bit数组 */
    private final BitArray bits;

    /** hash函数的个数 */
    private final int numHashFunctions;

    /** 将元素转为bytes The funnel to translate Ts to bytes */
    private final Funnel<? super T> funnel;

    /** The strategy we employ to map an element T to {@code numHashFunctions} bit indexes. */
    private final Strategy strategy;

    /** Creates a BloomFilter. */
    private BloomFilter(
            BitArray bits, int numHashFunctions, Funnel<? super T> funnel, Strategy strategy) {
        checkArgument(numHashFunctions > 0, "numHashFunctions (%s) must be > 0", numHashFunctions);
        checkArgument(
                numHashFunctions <= 255, "numHashFunctions (%s) must be <= 255", numHashFunctions);
        this.bits = checkNotNull(bits);
        this.numHashFunctions = numHashFunctions;
        this.funnel = checkNotNull(funnel);
        this.strategy = checkNotNull(strategy);
    }

    public long bitSize() {
        return bits.bitSize();
    }

    /**
     * 清空过滤器
     * @return
     */
    public void reset() {
        bits.reset();
    }

    /**
     * 向过滤器添加元素（添加后，调用mightContain时传相同元素则肯定返回true）
     * @return 如果返回true，则说明元素肯定是第一次添加，如果返回false则无法确定元素是否已经添加过
     */
    public boolean put(T object) throws Exception {
        return strategy.put(object, funnel, numHashFunctions, bits);
    }

    /**
     * 返回false时，表明元素肯定没有添加过（没有匹配到全部的bit位）
     * 返回true时，无法确定是否添加过（已添加的可能比较大）
     */
    public boolean mightContain(T object) throws Exception {
        return strategy.mightContain(object, funnel, numHashFunctions, bits);
    }

    /**
     * bit数量统计
     * @return
     * @throws Exception
     */
    public long bitCount() throws Exception {
        return bits.bitCount();
    }

    /**
     * 计算对于没有放入过滤器的元素执行{@linkplain #mightContain(Object)}方法时错误返回true的概率
     *
     * 理论上，这个数字应该接近或小于创建过滤器时传入的fpp参数。
     * 如果概率较fpp明显偏高，通常是因为过滤器中放入了超过预期的元素数量
     */
    public double expectedFpp() throws Exception {
        return Math.pow((double) bits.bitCount() / bits.bitSize(), numHashFunctions);
    }

    /**
     * 获取过滤器中的元素总数估算值（大概的数量，无法精确获取）
     * 在真实的总元素数量未超过创建过滤器时传入的预计插入元素数时估值相对准确*
     */
    public long approximateElementCount() throws Exception {
        long bitSize = bits.bitSize();
        long bitCount = bits.bitCount();

        double fractionOfBitsSet = (double) bitCount / bitSize;
        return DoubleMath.roundToLong(
                -Math.log1p(-fractionOfBitsSet) * bitSize / numHashFunctions, RoundingMode.HALF_UP);
    }

    /**
     * 根据预计插入数量（n）和假阳性概率（fpp，bloom判断一个元素在集合中的错误率）计算最佳bit数组的大小（m)
     * <p>See http://en.wikipedia.org/wiki/Bloom_filter#Probability_of_false_positives for the
     * formula.
     *
     * @param n 预计插入数量（必须为正数）expected insertions (must be positive)
     * @param p 假阳性概率 false positive rate (must be 0 < p < 1)
     */
    static long optimalNumOfBits(long n, double p) {
        if (p == 0) {
            p = Double.MIN_VALUE;
        }
        return (long) (-n * Math.log(p) / (Math.log(2) * Math.log(2)));
    }

    /**
     * 计算最佳hash函数的个数
     * Computes the optimal k (number of hashes per element inserted in Bloom filter), given the
     * expected insertions and total number of bits in the Bloom filter.
     *
     * <p>See http://en.wikipedia.org/wiki/File:Bloom_filter_fp_probability.svg for the formula.
     *
     * @param n 预计插入数量 expected insertions (must be positive)
     * @param m bit数组的大小 total number of bits in Bloom filter (must be positive)
     */
    static int optimalNumOfHashFunctions(long n, long m) {
        // (m / n) * log(2), but avoid truncation due to division!
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }
}

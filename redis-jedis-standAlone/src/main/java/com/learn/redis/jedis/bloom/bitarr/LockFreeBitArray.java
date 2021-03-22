package com.learn.redis.jedis.bloom.bitarr;

/**
 * @author wangjingwang
 * @version v1.0
 */

import com.google.common.math.LongMath;
import com.google.common.primitives.Ints;
import com.learn.redis.jedis.bloom.hash.LongAddable;
import com.learn.redis.jedis.bloom.hash.LongAddables;
import org.apache.commons.collections4.CollectionUtils;
import org.checkerframework.checker.units.qual.A;

import javax.annotation.Nullable;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.function.BinaryOperator;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Models a lock-free array of bits.
 *
 * <p>We use this instead of java.util.BitSet because we need access to the array of longs and we
 * need compare-and-swap.
 */
public final class LockFreeBitArray implements BitArray {
    private static final int LONG_ADDRESSABLE_BITS = 6;
    private AtomicLongArray data;
    // 为1的bit位数
    private LongAddable bitCount;

    // Used by serialization
    LockFreeBitArray(long[] data) {
        checkArgument(data.length > 0, "data length is zero!");
        this.data = new AtomicLongArray(data);
        this.bitCount = LongAddables.create();
        long bitCount = 0;
        for (long value : data) {
            bitCount += Long.bitCount(value);
        }
        this.bitCount.add(bitCount);
    }

    @Override
    public void initBitArray(long bitSize) {
        checkArgument(bitSize > 0, "data length is zero!");
        // Avoid delegating to this(long[]), since AtomicLongArray(long[]) will clone its input and
        // thus double memory usage.
        this.data =
                new AtomicLongArray(Ints.checkedCast(LongMath.divide(bitSize, 64, RoundingMode.CEILING)));
        this.bitCount = LongAddables.create();
    }

    @Override
    public boolean batchSupport() {
        return false;
    }

    @Override
    public void reset() {
        if (null != this.data) {
            long bitSize = bitSize();
            this.data =
                    new AtomicLongArray(Ints.checkedCast(LongMath.divide(bitSize, 64, RoundingMode.CEILING)));
        }
    }

    @Override
    public boolean[] batchSet(long[] indices) throws Exception {
        throw new RuntimeException();
    }

    @Override
    public boolean[] batchGet(long[] indices) throws Exception {
        throw new RuntimeException();
    }

    /**
     * 设置指定bit位为1并返回true，如果已经是1了直接返回false
     * @param bitIndex
     * @return Returns true if the bit changed value
     */
    @Override
    public boolean set(long bitIndex) {
        // 如果已经是1了直接（false表示没有修改）
        if (get(bitIndex)) {
            return false;
        }

        int longIndex = (int) (bitIndex >>> LONG_ADDRESSABLE_BITS);
        long mask = 1L << bitIndex; // only cares about low 6 bits of bitIndex

        long oldValue;
        long newValue;
        do {
            oldValue = data.get(longIndex);
            newValue = oldValue | mask; // 设置指定bit位为1
            if (oldValue == newValue) {
                // 可能由于并发导致方法开始时判断失效（bit位已经是1了）
                return false;
            }
            // 更新long数组
        } while (!data.compareAndSet(longIndex, oldValue, newValue));

        // bit数加一
        // We turned the bit on, so increment bitCount.
        bitCount.increment();
        return true;
    }

    /**
     * 如果指定bit为为1，返回true，否则返回false
     * @param bitIndex
     * @return
     */
    @Override
    public boolean get(long bitIndex) {
        // >>>: 无符号右移（右移高位补0）
        // bitIndex >>> 6: 相当于bitIndex/2^6 = bitIndex/64
        //  由于使用AtomicLongArray作为bit数组，则数组中每个元素（long型）代表了64个bit位，因此bitIndex/64即定位bitIndex落在了哪个
        // long块内。
        // 1L << bitIndex：
        // 1左移bitIndex位得到一个仅在bitIndex处为1的64位数字，任何一个数字与其做and运算时，只有该数字的bitIndex位是1，得到的结果才是1，否则是0；
        //
        // lon 1L << bitIndex 即
        // xxxxxxxx xxxxxxxx xxxxxxxx 1xxxxxxx & 00000000 00000000 00000000 10000000 = 1
        // xxxxxxxx xxxxxxxx xxxxxxxx 0xxxxxxx & 00000000 00000000 00000000 10000000 = 0
        //
        return (data.get((int) (bitIndex >>> LONG_ADDRESSABLE_BITS)) & (1L << bitIndex)) != 0;
    }

    /**
     * Careful here: if threads are mutating the atomicLongArray while this method is executing, the
     * final long[] will be a "rolling snapshot" of the state of the bit array. This is usually good
     * enough, but should be kept in mind.
     */
    public static long[] toPlainArray(AtomicLongArray atomicLongArray) {
        long[] array = new long[atomicLongArray.length()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = atomicLongArray.get(i);
        }
        return array;
    }

    /** Number of bits */
    @Override
    public long bitSize() {
        return (long) data.length() * Long.SIZE;
    }

    /**
     * Number of set bits (1s).
     *
     * <p>Note that because of concurrent set calls and uses of atomics, this bitCount is a (very)
     * close *estimate* of the actual number of bits set. It's not possible to do better than an
     * estimate without locking. Note that the number, if not exactly accurate, is *always*
     * underestimating, never overestimating.
     */
    @Override
    public long bitCount() {
        return bitCount.sum();
    }

    LockFreeBitArray copy() {
        return new LockFreeBitArray(toPlainArray(data));
    }

    /**
     * Combines the two BitArrays using bitwise OR.
     *
     * <p>NOTE: Because of the use of atomics, if the other LockFreeBitArray is being mutated while
     * this operation is executing, not all of those new 1's may be set in the final state of this
     * LockFreeBitArray. The ONLY guarantee provided is that all the bits that were set in the other
     * LockFreeBitArray at the start of this method will be set in this LockFreeBitArray at the end
     * of this method.
     */
    void putAll(LockFreeBitArray other) {
        checkArgument(
                data.length() == other.data.length(),
                "BitArrays must be of equal length (%s != %s)",
                data.length(),
                other.data.length());
        for (int i = 0; i < data.length(); i++) {
            long otherLong = other.data.get(i);

            long ourLongOld;
            long ourLongNew;
            boolean changedAnyBits = true;
            do {
                ourLongOld = data.get(i);
                ourLongNew = ourLongOld | otherLong;
                if (ourLongOld == ourLongNew) {
                    changedAnyBits = false;
                    break;
                }
            } while (!data.compareAndSet(i, ourLongOld, ourLongNew));

            if (changedAnyBits) {
                int bitsAdded = Long.bitCount(ourLongNew) - Long.bitCount(ourLongOld);
                bitCount.add(bitsAdded);
            }
        }
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o instanceof LockFreeBitArray) {
            LockFreeBitArray lockFreeBitArray = (LockFreeBitArray) o;
            // TODO(lowasser): avoid allocation here
            return Arrays.equals(toPlainArray(data), toPlainArray(lockFreeBitArray.data));
        }
        return false;
    }

    @Override
    public int hashCode() {
        // TODO(lowasser): avoid allocation here
        return Arrays.hashCode(toPlainArray(data));
    }
}

package com.learn.redis.jedis.cluster;

import com.learn.redis.jedis.lua.DigestUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.exceptions.JedisNoScriptException;

/**
 * @author wangjingwang
 * @version v1.0
 */
public class LuaTest {
    private static final String script = "local lockClientId = redis.call('GET', KEYS[1])\n" +
            "if lockClientId == ARGV[1] then\n" +
            "    redis.call('PEXPIRE', KEYS[1], ARGV[2])\n" +
            "    return 1\n" +
            "elseif not lockClientId then\n" +
            "    redis.call('SET', KEYS[1], ARGV[1], 'PX', ARGV[2])\n" +
            "    return 1\n" +
            "end\n" +
            "return 0\n";

    private static String sha1 = DigestUtils.sha1DigestAsHex(script);

    /**
     * 如果要实现线程维度加锁，结合java ReentrantLock实现（为了重入，unlock时先判断是否重入次数耗尽在执行redis删除）
     * if (!this.localLock.isHeldByCurrentThread()) {
     *      // 保证单线程
     *      throw new IllegalStateException("You do not own lock at " + this.lockKey);
     * }
     * if (this.localLock.getHoldCount() > 1) {
     *  // 减少重入次数
     *  this.localLock.unlock();
     * } else {
     *     redisUnlock()
     * }
     * @param lockKey 锁的标志
     * @param clientId 加锁的机器
     * @param expireSecond 锁过期时间
     * @return
     */
    public static boolean lock(String lockKey, String clientId, int expireSecond) {
        Object object = null;
        try {
            object = Cluster.instance.evalsha(sha1, 1, lockKey, clientId, String.valueOf(expireSecond * 1000));
        } catch (JedisNoScriptException noScriptExp) {
            System.out.println("no script");
            object = Cluster.instance.eval(script, 1, lockKey, clientId, String.valueOf(expireSecond * 1000));
        }

        if (object instanceof Long) {
            return (long)object == 1L;
        }
        return null != object && "1".equals(object.toString());
    }

    public static boolean unlock(String lockKey, String clientId) {
        String res = Cluster.instance.get(lockKey);
        if (!StringUtils.hasText(res)) {
            return true;
        }
        if (res.equals(clientId)) {
            return Cluster.instance.del(lockKey) > 0;
        }
        throw new RuntimeException("You do not own lock at" + lockKey);
    }

    public static void main(String[] args){
        System.out.println("lock 1: " + lock("test_lock", "client1", 60));
        System.out.println("lock 2: " + lock("test_lock", "client2", 60));

        System.out.println("unlock 1: " + unlock("test_lock", "client1"));

        System.out.println("lock 2: " + lock("test_lock", "client2", 60));
        System.out.println("unlock 2: " + unlock("test_lock", "client2"));
    }
}

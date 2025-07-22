package top.huzz.resilix.idempotent;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.ExtBloomFilter;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 基于布隆过滤器的幂等判断，可用于分布式环境。
 * <p/>如果是分布式的布隆过滤器，需要传入{@link RedissonClient}和布隆过滤器的名称，用于实现分布式的幂等判断。
 * <p/>布隆过滤器是一种空间效率高的数据结构，用于判断一个元素是否在一个集合中，如果判断结果为false，则元素一定不在集合中；如果判断结果为true，则元素可能在集合中，存在一定的误判率。
 * <p/>如果业务场景不允许误判，且允许牺牲一定的性能，那么可以考虑重写{@link #preciseJudge(IdempotentKey)}方法实现精确校验（具体牺牲的性能取决于该方法的执行耗时）。
 *
 * @author chenji
 * @since 1.0.0
 */
@SuppressWarnings("UnstableApiUsage")
@Slf4j
public class BloomFilterIdempotentJudge implements PrecedingIdempotentJudge {
    protected final int expectedInsertions;
    protected final double fpp;
    protected final Duration duration;
    protected IdempotentJudge proxy;

    static final int DEFAULT_EXPECTED_INSERTIONS = 50000;
    static final double DEFAULT_FPP = 0.00001;
    static final Duration MAX_DURATION = Duration.ofDays(2);

    /**
     * 创建默认的布隆过滤器
     *
     * @param expectedInsertions 预期插入元素数量
     * @param fpp                误判率
     * @return 本地布隆过滤器
     */
    public static BloomFilterIdempotentJudge newLocal(int expectedInsertions, double fpp) {
        return new BloomFilterIdempotentJudge(expectedInsertions, fpp, null, null, false, MAX_DURATION);
    }

    /**
     * 创建默认的布隆过滤器，预期插入元素数量为10000，误判率为0.0001，hash函数数量为13
     *
     * @param redissonClient  Redisson客户端
     * @param distributedName 分布式布隆过滤器的名称
     * @param cleanIfExists   如果布隆过滤器已经存在，是否清空
     */
    protected BloomFilterIdempotentJudge(@Nullable RedissonClient redissonClient, @Nullable String distributedName, boolean cleanIfExists) {
        this(DEFAULT_EXPECTED_INSERTIONS, DEFAULT_FPP, redissonClient, distributedName, cleanIfExists, MAX_DURATION);
    }

    /**
     * 创建布隆过滤器
     *
     * @param expectedInsertions  预期插入元素数量
     * @param fpp                 误判率
     * @param redissonClient      Redisson客户端，如果不为null，则会使用Redisson实现布隆过滤器，实现分布式的幂等判断
     * @param bloomFilterRedisKey 分布式布隆过滤器的名称，如果redissonClient不为null，则必须指定该参数
     * @param cleanIfExists       如果布隆过滤器已经存在，是否清空
     * @param duration            过期时间，目前只有分布式布隆过滤器支持过期时间
     */
    protected BloomFilterIdempotentJudge(int expectedInsertions, double fpp,
                                         @Nullable RedissonClient redissonClient, @Nullable String bloomFilterRedisKey, boolean cleanIfExists, @Nullable Duration duration) {
        this.expectedInsertions = expectedInsertions;
        this.fpp = fpp;
        duration = this.duration = (duration == null || duration.compareTo(MAX_DURATION) > 0) ? MAX_DURATION : duration;
        if (redissonClient != null) {
            if (StringUtils.isBlank(bloomFilterRedisKey)) {
                throw new IllegalArgumentException("bloomFilterRedisKey must not be null");
            }
            RBloomFilter<String> bf = new ExtBloomFilter<>(((Redisson) redissonClient).getCommandExecutor(), bloomFilterRedisKey);
            boolean hasInit = bf.tryInit(expectedInsertions, fpp);
            if (hasInit && cleanIfExists) {
                bf.delete();
                bf.tryInit(expectedInsertions, fpp);
            }
            // 设置过期时间
            bf.expire(duration);
            proxy = new Distributed(bf);
        } else {
            proxy = new Local(BloomFilter.create(Funnels.stringFunnel(StandardCharsets.UTF_8), expectedInsertions, fpp));
        }
    }


    @Override
    public boolean doJudge(IdempotentKey key) {
        if (key == null || key.idempotentKey() == null) {
            return false;
        }
        if (!proxy.judge(key)) {
            // 如果布隆过滤器判断不在集合中，直接返回false，这个肯定是未执行过的，布隆过滤器的特性决定了这个是准确的
            return false;
        }
        // 如果返回了true，需要做精确校验，用于解决布隆过滤器的误判问题
        return preciseJudge(key);
    }

    @Override
    public void put(@Nonnull IdempotentKey key) {
        if (key.idempotentKey() == null) {
            return;
        }
        proxy.put(key);
    }

    @Override
    public void destroy() {
        proxy.destroy();
    }

    /**
     * 做精确校验，用于解决布隆过滤器的误判问题，一般查询数据库里面的数据进行校验
     *
     * @return true表示已经执行过，false表示未执行过
     */
    protected boolean preciseJudge(IdempotentKey key) {
        return false;
    }

    static class Local implements IdempotentJudge {
        protected BloomFilter<String> bf;

        public Local(BloomFilter<String> bf) {
            this.bf = bf;
        }

        @Override
        public boolean judge(IdempotentKey key) {
            if (key == null) {
                return false;
            }
            return bf.mightContain(key.idempotentKey());
        }

        @Override
        public void put(@Nonnull IdempotentKey key) {
            bf.put(key.idempotentKey());
        }

        @Override
        public void destroy() {
            bf = null;
            System.gc();
        }
    }

    static class Distributed implements IdempotentJudge {
        protected RBloomFilter<String> bf;

        public Distributed(RBloomFilter<String> bf) {
            this.bf = bf;
        }

        @Override
        public boolean judge(IdempotentKey key) {
            if (key == null) {
                return false;
            }
            return bf.contains(key.idempotentKey());
        }

        @Override
        public void put(@Nonnull IdempotentKey key) {
            if (bf instanceof ExtBloomFilter) {
                // ExtBloomFilter重新了containsAsync方法，使其支持在判断后把被判断的值直接插入布隆过滤器，这样可以避免再调用一次put方法，因此在这里put方法不需要再调用
                return;
            }
            bf.add(key.idempotentKey());
        }

        @Override
        public void destroy() {
            try {
                bf.delete();
            } catch (Exception e) {
                log.error("Error deleting bloom filter: ", e);
            }
        }
    }
}

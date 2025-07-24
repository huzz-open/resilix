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
 * Bloom filter-based idempotent judgment, suitable for distributed environments.
 * <p/>For distributed bloom filters, you need to pass in {@link RedissonClient} and the bloom filter name 
 * to implement distributed idempotent judgment.
 * <p/>Bloom filter is a space-efficient data structure used to determine whether an element is in a set. 
 * If the judgment result is false, the element is definitely not in the set; 
 * if the judgment result is true, the element may be in the set, with a certain false positive rate.
 * <p/>If the business scenario does not allow false positives and allows sacrificing some performance, 
 * you can consider overriding the {@link #preciseJudge(IdempotentKey)} method to implement precise verification 
 * (the specific performance sacrifice depends on the execution time of this method).
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
     * Create a default bloom filter
     *
     * @param expectedInsertions expected number of elements to be inserted
     * @param fpp                false positive probability
     * @return local bloom filter
     */
    public static BloomFilterIdempotentJudge newLocal(int expectedInsertions, double fpp) {
        return new BloomFilterIdempotentJudge(expectedInsertions, fpp, null, null, false, MAX_DURATION);
    }

    /**
     * Create a default bloom filter with expected insertions of 10000, false positive probability of 0.0001, and 13 hash functions
     *
     * @param redissonClient  Redisson client
     * @param distributedName name of the distributed bloom filter
     * @param cleanIfExists   whether to clear if the bloom filter already exists
     */
    protected BloomFilterIdempotentJudge(@Nullable RedissonClient redissonClient, @Nullable String distributedName, boolean cleanIfExists) {
        this(DEFAULT_EXPECTED_INSERTIONS, DEFAULT_FPP, redissonClient, distributedName, cleanIfExists, MAX_DURATION);
    }

    /**
     * Create a bloom filter
     *
     * @param expectedInsertions  expected number of elements to be inserted
     * @param fpp                 false positive probability
     * @param redissonClient      Redisson client, if not null, will use Redisson to implement bloom filter for distributed idempotent judgment
     * @param bloomFilterRedisKey name of the distributed bloom filter, must be specified if redissonClient is not null
     * @param cleanIfExists       whether to clear if the bloom filter already exists
     * @param duration            expiration time, currently only distributed bloom filters support expiration time
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
            // Set expiration time
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
            // If bloom filter determines not in the set, directly return false, this is definitely not executed, bloom filter characteristics ensure this is accurate
            return false;
        }
        // If it returns true, precise verification is needed to solve the bloom filter false positive problem
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
     * Perform precise verification to solve bloom filter false positive problem, usually by querying database data for verification
     *
     * @return true indicates already executed, false indicates not executed
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
                // ExtBloomFilter overrides containsAsync method to support directly inserting the judged value into bloom filter after judgment, avoiding another put method call, so put method doesn't need to be called here
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

package org.redisson;

import org.redisson.api.RFuture;
import org.redisson.client.codec.LongCodec;
import org.redisson.client.protocol.RedisCommands;
import org.redisson.command.CommandAsyncExecutor;
import org.redisson.misc.CompletableFutureWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @author chenji
 * @since 1.0.0
 */
public class ExtBloomFilter<T> extends RedissonBloomFilter<T> {

    public ExtBloomFilter(CommandAsyncExecutor commandExecutor, String name) {
        super(commandExecutor, name);
    }

    // 重写containsAsync方法，使其支持在判断后把被判断的值直接插入布隆过滤器，这样可以避免再调用一次put方法
    @Override
    public RFuture<Long> containsAsync(Collection<T> objects) {
        CompletionStage<Long> f = CompletableFuture.completedFuture(null);
        if (size == 0) {
            f = readConfigAsync().handle((r, e) -> {
                if (e instanceof IllegalArgumentException) {
                    return 0L;
                }
                return null;
            });
        }

        f = f.thenCompose(r -> {
            if (r != null) {
                return CompletableFuture.completedFuture(r);
            }
            List<Long> allIndexes = index(objects);

            List<Object> params = new ArrayList<>();
            params.add(size);
            params.add(hashIterations);
            params.add(objects.size());
            params.addAll(allIndexes);

            return commandExecutor.evalWriteAsync(getRawName(), LongCodec.INSTANCE, RedisCommands.EVAL_LONG,
                    """
                            local size = redis.call('hget', KEYS[1], 'size')
                            local hashIterations = redis.call('hget', KEYS[1], 'hashIterations')
                            if size ~= ARGV[1] or hashIterations ~= ARGV[2] then
                              return 0
                            end
                            
                            local k = 0
                            local c = 0
                            local cc = (#ARGV - 3) / ARGV[3]
                            for i = 4, #ARGV, 1 do
                              local r = redis.call('getbit', KEYS[2], ARGV[i]);
                              if r == 0 then
                                k = k + 1
                                redis.call('setbit', KEYS[2], ARGV[i], 1)
                              end
                              if ((i - 4) + 1) % cc == 0 then
                                if k > 0 then
                                  c = c + 1
                                end
                                k = 0
                              end
                            end
                            return ARGV[3] - c;""",
                    Arrays.asList(configName, getRawName()),
                    params.toArray());
        });
        return new CompletableFutureWrapper<>(f);
    }
}

package org.huzz.resilix.api.run.cache;

import org.huzz.resilix.api.run.RunContext;
import org.springframework.stereotype.Component;

/**
 * @author chenji
 * @since 1.0.0
 */
@Component
public class ExecutionUnitAwareCache extends LoadingAwareCache {
    @Override
    public Type getType() {
        return Type.EXECUTION_UNIT;
    }

    @Override
    public void clean(RunContext context) {
        // 覆盖掉父类的方法，不清除数据，因为在异步发送执行详情的时候，需要用到，只利用LoadingCache的清除机制
    }
}

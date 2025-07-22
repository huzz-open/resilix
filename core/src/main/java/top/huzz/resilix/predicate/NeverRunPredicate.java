package top.huzz.resilix.predicate;


import top.huzz.resilix.core.RunContext;

/**
 * 对于那些已经被弃用的阶段，最好不要直接去掉枚举内的值，否则因为对存量数据的解析会出现枚举报错，可以使用该断言来避免运行
 *
 * @param <C> 上下文类型
 * @author chenji
 * @since 1.0.0
 */
public class NeverRunPredicate<C extends RunContext> implements HandlerRunPredicate<C> {
    @Override
    public boolean shouldRun(C context) {
        return false;
    }
}

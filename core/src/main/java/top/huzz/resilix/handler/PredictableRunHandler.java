package top.huzz.resilix.handler;


import jakarta.annotation.Nonnull;
import top.huzz.resilix.predicate.AlwaysRunPredicate;
import top.huzz.resilix.predicate.HandlerRunPredicate;
import top.huzz.resilix.core.RunContext;

import java.util.Comparator;
import java.util.List;

/**
 * 可预测的运行处理器，可以指定运行的条件，只需要继承该类并实现{@link #predicateClasses()}方法即可
 *
 * @param <C> 上下文类型
 * @author chenji
 * @since 1.0.0
 */
public abstract class PredictableRunHandler<C extends RunContext> implements RunHandler<C> {

    protected List<HandlerRunPredicate<C>> predicates = List.of(new AlwaysRunPredicate<>());

    protected PredictableRunHandler(List<HandlerRunPredicate<C>> predicates) {
        List<Class<?>> classes = predicateClasses();
        if (predicates == null || predicates.isEmpty() || classes == null || classes.isEmpty()) {
            return;
        }

        for (Class<?> c : classes) {
            if (!HandlerRunPredicate.class.isAssignableFrom(c)) {
                throw new IllegalArgumentException("predicateClasses()方法返回的类必须是HandlerRunPredicate的子类，不支持的类：" + c);
            }
        }

        this.predicates = predicates.stream()
                .filter(p -> classes.contains(p.getClass()))
                .sorted(Comparator.comparingInt(p -> classes.indexOf(p.getClass())))
                .toList();
    }

    /**
     * @return 返回需要使用的断言类
     */
    protected List<Class<?>> predicateClasses() {
        return List.of(AlwaysRunPredicate.class);
    }

    @Nonnull
    @Override
    public List<HandlerRunPredicate<C>> runPredicate() {
        return predicates;
    }
}

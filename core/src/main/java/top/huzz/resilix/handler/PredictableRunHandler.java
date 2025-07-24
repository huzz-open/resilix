package top.huzz.resilix.handler;


import jakarta.annotation.Nonnull;
import top.huzz.resilix.predicate.AlwaysRunPredicate;
import top.huzz.resilix.predicate.HandlerRunPredicate;
import top.huzz.resilix.core.RunContext;

import java.util.Comparator;
import java.util.List;

/**
 * Predictable run handler that allows specifying execution conditions. 
 * Simply extend this class and implement the {@link #predicateClasses()} method.
 *
 * @param <C> context type
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
                throw new IllegalArgumentException("Classes returned by predicateClasses() method must be subclasses of HandlerRunPredicate. Unsupported class: " + c);
            }
        }

        this.predicates = predicates.stream()
                .filter(p -> classes.contains(p.getClass()))
                .sorted(Comparator.comparingInt(p -> classes.indexOf(p.getClass())))
                .toList();
    }

    /**
     * @return Returns the list of predicate classes to be used
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

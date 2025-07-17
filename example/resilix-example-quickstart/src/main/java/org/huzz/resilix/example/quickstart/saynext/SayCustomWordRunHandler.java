package org.huzz.resilix.example.quickstart.saynext;

import jakarta.annotation.Nonnull;
import org.huzz.resilix.api.predicate.HandlerRunPredicate;
import org.huzz.resilix.api.run.Phase;
import org.huzz.resilix.api.run.handler.PredictableRunHandler;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author chenji
 * @since 1.0.0
 */
@Component
public class SayCustomWordRunHandler extends PredictableRunHandler<SayCustomNextWordContext> {

    protected SayCustomWordRunHandler(List<HandlerRunPredicate<SayCustomNextWordContext>> handlerRunPredicates) {
        super(handlerRunPredicates);
    }

    @Override
    public void handle(SayCustomNextWordContext context) {
        System.out.println(context.getNextWord());
    }

    @Nonnull
    @Override
    public Phase phase() {
        return SayCustomPhase.SAY_WORLD;
    }
}

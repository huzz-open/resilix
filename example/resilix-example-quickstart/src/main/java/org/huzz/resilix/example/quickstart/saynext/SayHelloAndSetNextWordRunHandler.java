package org.huzz.resilix.example.quickstart.saynext;

import jakarta.annotation.Nonnull;
import org.huzz.resilix.api.predicate.HandlerRunPredicate;
import org.huzz.resilix.api.run.Phase;
import org.huzz.resilix.api.run.handler.PredictableRunHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * @author chenji
 * @since 1.0.0
 */
@Component
public class SayHelloAndSetNextWordRunHandler extends PredictableRunHandler<SayCustomNextWordContext> {

    protected SayHelloAndSetNextWordRunHandler(List<HandlerRunPredicate<SayCustomNextWordContext>> handlerRunPredicates) {
        super(handlerRunPredicates);
    }

    @Override
    public void handle(SayCustomNextWordContext context) {
        System.out.println("hello custom");
        context.setNextWord("world " + UUID.randomUUID());
    }

    @Nonnull
    @Override
    public Phase phase() {
        return SayCustomPhase.SAY_HELLO;
    }
}

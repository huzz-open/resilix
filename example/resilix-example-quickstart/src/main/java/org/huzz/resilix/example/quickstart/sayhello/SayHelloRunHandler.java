package org.huzz.resilix.example.quickstart.sayhello;

import jakarta.annotation.Nonnull;
import org.huzz.resilix.api.predicate.HandlerRunPredicate;
import org.huzz.resilix.api.run.Phase;
import org.huzz.resilix.api.run.SimpleRunContext;
import org.huzz.resilix.api.run.handler.PredictableRunHandler;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author chenji
 * @since 1.0.0
 */
@Component
public class SayHelloRunHandler extends PredictableRunHandler<SimpleRunContext> {

    protected SayHelloRunHandler(List<HandlerRunPredicate<SimpleRunContext>> handlerRunPredicates) {
        super(handlerRunPredicates);
    }

    @Override
    public void handle(SimpleRunContext context) {
        System.out.println("hello");
    }

    @Nonnull
    @Override
    public Phase phase() {
        return SayPhase.SAY_HELLO;
    }
}

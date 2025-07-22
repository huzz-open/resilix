package top.huzz.resilix.example.usectx.saynext;

import jakarta.annotation.Nonnull;
import top.huzz.resilix.predicate.HandlerRunPredicate;
import top.huzz.resilix.core.Phase;
import top.huzz.resilix.handler.PredictableRunHandler;
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

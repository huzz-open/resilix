package top.huzz.resilix.example.quickstart.sayhello;

import jakarta.annotation.Nonnull;
import top.huzz.resilix.predicate.HandlerRunPredicate;
import top.huzz.resilix.core.Phase;
import top.huzz.resilix.core.SimpleRunContext;
import top.huzz.resilix.handler.PredictableRunHandler;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author chenji
 * @since 1.0.0
 */
@Component
public class SayWorldRunHandler extends PredictableRunHandler<SimpleRunContext> {

    protected SayWorldRunHandler(List<HandlerRunPredicate<SimpleRunContext>> handlerRunPredicates) {
        super(handlerRunPredicates);
    }

    @Override
    public void handle(SimpleRunContext context) {
        System.out.println("world");
    }

    @Nonnull
    @Override
    public Phase phase() {
        return SayPhase.SAY_WORLD;
    }
}

package top.huzz.resilix.callback;

import lombok.Getter;
import lombok.Setter;
import top.huzz.resilix.core.Phase;
import top.huzz.resilix.core.RunContext;

/**
 * @author chenji
 * @since 1.0.0
 */
@Getter
@Setter
public class RunHandlerEvent {
    private RunContext context;
    // 附加信息
    private Object extra;

    public RunHandlerEvent(RunContext context, Object extra) {
        this.context = context;
        this.extra = extra;
    }

    public Phase phase() {
        return context.getCurrentPhase();
    }
}

package org.huzz.resilix.api.run;

import org.huzz.resilix.api.constants.EnvType;
import org.huzz.resilix.api.run.handler.RunHandler;
import org.huzz.resilix.api.util.ApplicationBeanUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 运行上下文接口
 *
 * @author chenji
 * @since 1.0.0
 */
public interface RunContext {
    ThreadLocal<Map<Class<? extends RunContext>, RunContext>> CONTEXT = ThreadLocal.withInitial(HashMap::new);

    void setSuccess(boolean success);

    boolean isSuccess();

    void setException(Exception e);

    void setCurrentPhase(Phase phase);

    Phase getCurrentPhase();

    boolean isStopped();

    void setStopped(boolean stopped);

    void setSkipped(boolean skip);

    EnvType getEnvType();

    /**
     * @return 当前运行上下文副本
     */
    RunContext duplicate();

    /**
     * 覆盖当前的上下文数据
     *
     * @param useToCovered 用于去覆盖的上下文
     */
    void cover(RunContext useToCovered);

    Object getExtra();

    default void clean() {
    }

    static RunContext getCurrentCtx(Class<? extends RunContext> ctxClass) {
        return CONTEXT.get().get(ctxClass);
    }

    static void setCurrentCtx(RunContext context) {
        CONTEXT.get().put(context.getClass(), context);
    }

    static void removeCurrentCtx() {
        CONTEXT.remove();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default void manual(Class<? extends RunHandler> handlerClass) throws Exception {
        Objects.requireNonNull(ApplicationBeanUtil.getBean(handlerClass)).handle(this);
    }
}

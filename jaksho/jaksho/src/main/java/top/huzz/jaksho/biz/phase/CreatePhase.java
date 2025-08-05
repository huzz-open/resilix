package top.huzz.jaksho.biz.phase;

import top.huzz.resilix.core.Phase;

/**
 * @author chenji
 * @since 1.0.0
 */
public enum CreatePhase implements Phase {
    /**
     * 创建之前的验证阶段，校验输入数据的合法性
     */
    VALIDATE,

    /**
     * 创建阶段，执行实际的创建操作
     */
    CREATE,

    /**
     * 创建之后的处理阶段，进行必要的后续处理
     */
    POST_PROCESS,
    ;

    @Override
    public Phase[] getValues() {
        return values();
    }
}

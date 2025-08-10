package top.huzz.jaksho.api.phase;

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
     * 创建之前的数据转换阶段，进行必要的数据转换，将输入数据转换为适合创建的格式
     */
    TRANSFORM,

    /**
     * 存储之前的预处理阶段，进行必要的预处理操作
     */
    PRE_SAVE,

    /**
     * 存储阶段，执行实际的创建操作
     */
    SAVE,

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

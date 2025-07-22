package top.huzz.resilix.additional;


import top.huzz.resilix.core.RunContext;

/**
 * 基于context的附加操作
 *
 * @param <C> 上下文
 * @author chenji
 * @since 1.0.0
 */
public interface AdditionalContextAction<C extends RunContext> {

    /**
     * 执行附加操作
     *
     * @param context 上下文
     */
    void action(final C context);

    /**
     * 是否跳过
     *
     * @param context 上下文
     * @return true：跳过；false：不跳过
     */
    default boolean skip(final C context) {
        return false;
    }

    /**
     * @return 返回执行顺序，越小越先执行，相同的情况下，按照Spring注入Bean的顺序执行，建议显性指定
     */
    default int order() {
        return 0;
    }

}

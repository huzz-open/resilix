package top.huzz.resilix.core;

/**
 * @author chenji
 * @since 1.0.0
 */
public interface ExtraInfoProvider {
    /**
     * 获取附加信息
     * @param context 测试计划运行上下文
     * @return 附加信息
     */
    Object getExtraInfo(RunContext context);
}


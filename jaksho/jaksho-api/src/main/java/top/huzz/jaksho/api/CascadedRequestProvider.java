package top.huzz.jaksho.api;

import java.util.List;

/**
 * @param <C> 级联请求的对象类型
 * @author huzz
 * @since 1.0.2
 */
public interface CascadedRequestProvider<C> {
    /**
     * @return 级联请求列表
     */
    List<C> cascadedRequests();
}

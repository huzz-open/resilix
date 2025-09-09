package top.huzz.jaksho.api.helper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import top.huzz.jaksho.common.entity.BasicProperties;
import top.huzz.jaksho.common.entity.CombineResult;
import top.huzz.jaksho.common.mapper.ExtBaseMapper;
import top.huzz.jaksho.common.mapper.QueryAnyMapper;
import top.huzz.resilix.util.ApplicationContextUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author chenji
 * @since 1.0.0
 */
@Configuration
public class QueryHelper {
    @Getter
    private static QueryAnyMapper queryAnyMapper;

    public QueryHelper(QueryAnyMapper queryAnyMapper) {
        QueryHelper.queryAnyMapper = queryAnyMapper;
    }

    public static List<CombineResult> query(String sql) {
        return query(sql, null, null);
    }

    public static List<CombineResult> query(String sql, Page<CombineResult> page) {
        return query(sql, page, null);
    }

    public static List<CombineResult> query(String sql, Map<String, Object> p) {
        return query(sql, null, p);
    }

    public static List<CombineResult> query(String sql, Page<CombineResult> page, Map<String, Object> p) {
        return queryAnyMapper.query(sql, page, p);
    }

    public static <T extends BasicProperties, M extends ExtBaseMapper<T>> List<T> lambdaQuery(Class<M> mapper, IPage<T> page, Consumer<LambdaQueryWrapper<T>> lambdaQueryWrapperConsumer) {
        LambdaQueryWrapper<T> lambdaQuery = buildLambdaQuery(lambdaQueryWrapperConsumer);
        return ApplicationContextUtils.getBean(mapper).selectList(page, lambdaQuery);
    }

    public static <T extends BasicProperties, M extends ExtBaseMapper<T>> List<T> lambdaQuery(Class<M> mapper, Consumer<LambdaQueryWrapper<T>> lambdaQueryWrapperConsumer) {
        LambdaQueryWrapper<T> lambdaQuery = buildLambdaQuery(lambdaQueryWrapperConsumer);
        return ApplicationContextUtils.getBean(mapper).selectList(lambdaQuery);
    }

    public static <T extends BasicProperties, M extends ExtBaseMapper<T>> List<T> query(Class<M> mapper, IPage<T> page, Consumer<QueryWrapper<T>> queryWrapperConsumer) {
        QueryWrapper<T> query = buildQuery(queryWrapperConsumer);
        return ApplicationContextUtils.getBean(mapper).selectList(page, query);
    }

    public static <T extends BasicProperties, M extends ExtBaseMapper<T>> List<T> query(Class<M> mapper, Consumer<QueryWrapper<T>> queryWrapperConsumer) {
        QueryWrapper<T> query = buildQuery(queryWrapperConsumer);
        return ApplicationContextUtils.getBean(mapper).selectList(query);
    }

    private static <T extends BasicProperties> LambdaQueryWrapper<T> buildLambdaQuery(Consumer<LambdaQueryWrapper<T>> lambdaQueryWrapperConsumer) {
        LambdaQueryWrapper<T> query = Wrappers.lambdaQuery();
        if (lambdaQueryWrapperConsumer != null) {
            lambdaQueryWrapperConsumer.accept(query);
        }
        return query;
    }

    private static <T extends BasicProperties> QueryWrapper<T> buildQuery(Consumer<QueryWrapper<T>> queryWrapperConsumer) {
        QueryWrapper<T> query = Wrappers.query();
        if (queryWrapperConsumer != null) {
            queryWrapperConsumer.accept(query);
        }
        return query;
    }
}

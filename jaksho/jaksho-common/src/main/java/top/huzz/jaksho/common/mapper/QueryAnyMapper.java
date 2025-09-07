package top.huzz.jaksho.common.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import top.huzz.jaksho.common.entity.CombineResult;

import java.util.List;
import java.util.Map;

/**
 * 通用多表查询映射器
 * 支持任意表之间的连接查询，结果映射到 CombineResult 对象
 *
 * @author huzz
 * @since 1.0.2
 */
@Mapper
public interface QueryAnyMapper {

    @Select("${sql}")
    @ResultType(CombineResult.class)
    List<CombineResult> query(@Param("sql") String sql, @Param("page") Page<CombineResult> page);

    default List<CombineResult> query(@Param("sql") String sql) {
        return query(sql, (Page<CombineResult>) null);
    }

    @Select("${sql}")
    @ResultType(CombineResult.class)
    List<CombineResult> query(@Param("sql") String sql, @Param("page") Page<CombineResult> page, @Param("params") Map<String, Object> params);

    default List<CombineResult> query(@Param("sql") String sql, @Param("params") Map<String, Object> params) {
        return query(sql, null, params);
    }
}

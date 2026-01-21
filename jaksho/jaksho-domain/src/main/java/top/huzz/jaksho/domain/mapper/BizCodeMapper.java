package top.huzz.jaksho.domain.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.huzz.jaksho.common.entity.CombineResult;
import top.huzz.jaksho.common.mapper.ExtBaseMapper;
import top.huzz.jaksho.domain.entity.BizCode;

import java.util.List;

/**
 * <p>
 * 业务码表 Mapper 接口
 * </p>
 *
 * @author mybatis-plus-generator
 * @since 1.0.2
 */
@Mapper
public interface BizCodeMapper extends ExtBaseMapper<BizCode> {
    
    /**
     * 多表联查（BizCode + Service）
     */
    List<CombineResult> query(@Param("page") Page<CombineResult> page, @Param("p") Object anyParams);
}

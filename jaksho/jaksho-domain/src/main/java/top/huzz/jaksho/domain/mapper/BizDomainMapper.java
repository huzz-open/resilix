package top.huzz.jaksho.domain.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.huzz.jaksho.common.mapper.ExtBaseMapper;
import top.huzz.jaksho.domain.entity.BizDomain;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务领域 Mapper 接口
 * </p>
 *
 * @author mybatis-plus-generator
 * @since 1.0.2
 */
@Mapper
public interface BizDomainMapper extends ExtBaseMapper<BizDomain> {

    /**
     * 分页查询业务领域
     * @param page 分页对象
     * @param params 查询参数
     * @return 业务领域列表
     */
    List<BizDomain> pageQuery(@Param("page") Page<BizDomain> page, @Param("p") Map<String, Object> params);
}

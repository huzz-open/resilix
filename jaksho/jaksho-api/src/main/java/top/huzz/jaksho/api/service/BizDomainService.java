package top.huzz.jaksho.api.service;

import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.remoting.http12.HttpMethods;
import org.apache.dubbo.remoting.http12.rest.Mapping;
import org.apache.dubbo.remoting.http12.rest.Param;
import org.apache.dubbo.remoting.http12.rest.ParamType;
import org.hibernate.validator.constraints.Length;
import top.huzz.jaksho.api.dto.AbstractPageQuery;
import top.huzz.jaksho.common.entity.PageResult;
import top.huzz.jaksho.domain.entity.BizDomain;
import top.huzz.resilix.validation.annotation.BizCheck;

/**
 * 业务域名服务接口
 *
 * @author chenji
 * @since 1.0.2
 */
@Mapping("/sr/biz-domain")
public interface BizDomainService {

    @Mapping("")
    Integer create(CreateBizDomainRequest request);

    @Getter
    @Setter
    class CreateBizDomainRequest {
        @Length(min = 1, max = 100)
        @BizCheck("#__DB_UNIQUE('top.huzz.jaksho.domain.entity.BizDomain', #this)")
        private String name;

        @Length(max = 255)
        private String description;
    }

    @Mapping(path = "/page", method = HttpMethods.POST)
    PageResult<BizDomain> pageQuery(BizDomainService.PageQueryRequest request);

    @Getter
    @Setter
    class PageQueryRequest extends AbstractPageQuery<BizDomain> {
        /**
         * 字段ID，用于过滤已分配给该字段的领域
         */
        private Integer bizFieldId;
    }

    @Mapping(path = "/{id}", method = HttpMethods.DELETE)
    int delete(@Param(value = "id", type = ParamType.PathVariable) Integer id);
}

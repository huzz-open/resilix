package top.huzz.jaksho.api.service;

import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.remoting.http12.HttpMethods;
import org.apache.dubbo.remoting.http12.rest.Mapping;
import org.apache.dubbo.remoting.http12.rest.Param;
import org.apache.dubbo.remoting.http12.rest.ParamType;
import top.huzz.jaksho.api.dto.AbstractPageQuery;
import top.huzz.jaksho.common.constant.BasicFieldType;
import top.huzz.jaksho.common.constant.CollectionType;
import top.huzz.jaksho.common.entity.CombineResult;
import top.huzz.jaksho.common.entity.PageResult;
import top.huzz.jaksho.domain.entity.BizFieldDomain;
import top.huzz.resilix.validation.annotation.BizCheck;

import java.util.List;

/**
 * 业务字段域名服务接口
 *
 * @author chenji
 * @since 1.0.2
 */
@Mapping("/sr/biz-field-domain")
public interface BizFieldDomainService {

    @Mapping("")
    Integer create(CreateBizFieldDomainRequest request);

    @Getter
    @Setter
    @BizCheck.List({
            @BizCheck("#__DB_UNIQUE('top.huzz.jaksho.domain.entity.BizFieldDomain', bizFieldId, bizDomainId, bizFieldTypeId)"),
            @BizCheck("#__DB_UNIQUE('top.huzz.jaksho.domain.entity.BizFieldDomain', bizFieldId, bizDomainId)"),
            @BizCheck("#isBizFieldDomainMatched(bizFieldId, bizFieldTypeId)"),
    })
    class CreateBizFieldDomainRequest {
        @BizCheck("#__DB_EXIST_WITH_ID('top.huzz.jaksho.domain.entity.BizField', #this)")
        private Integer bizFieldId;
        @BizCheck("#__DB_EXIST_WITH_ID('top.huzz.jaksho.domain.entity.BizDomain', #this)")
        private Integer bizDomainId;
        @BizCheck("#__DB_EXIST_WITH_ID('top.huzz.jaksho.domain.entity.BizFieldType', #this)")
        @BizCheck("#mustNotBasicObjectType(#this)")
        private Integer bizFieldTypeId;
    }

    @Mapping(path = "/page", method = HttpMethods.POST)
    PageResult<CombineResult> pageQuery(BizFieldDomainService.PageQueryRequest request);

    @Getter
    @Setter
    class PageQueryRequest extends AbstractPageQuery<CombineResult> {
        private Integer id;
        private Integer bizFieldId;
        /**
         * 是否排除默认字段域。
         * <p>每个业务字段被创建的时候，都会创建创建一个与之对应的{@link BizFieldDomain}，且bizDomainId = -1，当excludeDefaultFieldDomain为true的时候，则排除掉这些数据</p>
         */
        private Boolean excludeDefaultFieldDomain;
        private List<BasicFieldType> notBasicFieldTypes;
        private CollectionType collectionType;
    }

    @Mapping(path = "/{id}", method = HttpMethods.DELETE)
    int delete(@Param(value = "id", type = ParamType.PathVariable) Integer id);

    @Mapping(path = "/{id}", method = HttpMethods.GET)
    CombineResult detail(@Param(value = "id", type = ParamType.PathVariable) Integer id);
}

package top.huzz.jaksho.api.service;

import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.remoting.http12.rest.Mapping;
import top.huzz.resilix.validation.annotation.BizCheck;

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
    @BizCheck("#__DB_UNIQUE('top.huzz.jaksho.domain.entity.BizFieldDomain', bizFieldId, bizDomainId, bizFieldTypeId)")
    class CreateBizFieldDomainRequest {
        @BizCheck("#__DB_EXIST_WITH_ID('top.huzz.jaksho.domain.entity.BizField', #this)")
        private Integer bizFieldId;
        @BizCheck("#__DB_EXIST_WITH_ID('top.huzz.jaksho.domain.entity.BizDomain', #this)")
        private Integer bizDomainId;
        @BizCheck("#__DB_EXIST_WITH_ID('top.huzz.jaksho.domain.entity.BizFieldType', #this)")
        private Integer bizFieldTypeId;
    }
}

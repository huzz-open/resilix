package top.huzz.jaksho.api.service;

import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.remoting.http12.rest.Mapping;
import org.hibernate.validator.constraints.Length;
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
}

package top.huzz.jaksho.service;

import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.remoting.http12.rest.Mapping;
import org.hibernate.validator.constraints.Length;

/**
 * 业务字段类型服务接口
 *
 * @author chenji
 * @since 1.0.2
 */
@Mapping("/sr/biz-field-type")
public interface BizFieldTypeService {

    @Mapping("")
    int create(CreateBizFieldTypeRequest request);

    @Getter
    @Setter
    class CreateBizFieldTypeRequest {
        @Length(max = 5)
        private String name;
    }
}

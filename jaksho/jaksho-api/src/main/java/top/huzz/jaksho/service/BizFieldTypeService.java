package top.huzz.jaksho.service;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.remoting.http12.rest.Mapping;
import org.hibernate.validator.constraints.Length;
import top.huzz.jaksho.common.constant.BasicFieldType;
import top.huzz.jaksho.common.constant.CollectionType;

/**
 * 业务字段类型服务接口
 *
 * @author chenji
 * @since 1.0.2
 */
@Mapping("/sr/biz-field-type")
public interface BizFieldTypeService {

    @Mapping("")
    Integer create(CreateBizFieldTypeRequest request);

    @Getter
    @Setter
    class CreateBizFieldTypeRequest {
        @NotNull
        @Length(min = 1, max = 100)
        private String name;

        @Length(max = 255)
        private String description;

        @NotNull
        private CollectionType collectionType;

        @NotNull
        private BasicFieldType basicFieldType;

        private Integer minimum;

        private Integer maximum;
    }
}

package top.huzz.jaksho.api.service;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.remoting.http12.rest.Mapping;
import org.hibernate.validator.constraints.Length;
import top.huzz.jaksho.api.dto.ObjectBizFieldTypeRefDTO;
import top.huzz.jaksho.common.constant.BasicFieldType;
import top.huzz.jaksho.common.constant.CollectionType;
import top.huzz.resilix.annotation.BizCheck;

import java.util.List;

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
    @BizCheck.List({
            // 如果 basicFieldType 是 OBJECT，则校验objectBizFieldTypeRefDTOList字段
            // 这里有几个特性：
            // 1. 这里使用特殊标识`__VALID`，表示对 objectBizFieldTypeRefDTOList字段进行校验，具体如何校验，得看 ObjectBizFieldTypeRefDTO 的定义
            @BizCheck(when = "basicFieldType == BasicFieldType.OBJECT", value = "#__VALID(objectBizFieldTypeRefDTOList)"),
    })
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

        private List<ObjectBizFieldTypeRefDTO> objectBizFieldTypeRefDTOList;

        @BizCheck(value = "#this.length() > 0")
        private String bcTest;
    }


}

package top.huzz.jaksho.api.service;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.remoting.http12.rest.Mapping;
import org.hibernate.validator.constraints.Length;
import top.huzz.jaksho.api.annotation.BizCheck;
import top.huzz.jaksho.api.dto.ObjectBizFieldTypeRefDTO;
import top.huzz.jaksho.common.constant.BasicFieldType;
import top.huzz.jaksho.common.constant.CollectionType;

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
            // 1. BasicFieldType.OBJECT这种写法其实是不对的，因为类要指定全限定名，这里之所以可以这样简写，是因为通过被检查字段basicFieldType的类型来获取真实类型
            // 2. 这里使用特殊标识`__VALID`，表示对 objectBizFieldTypeRefDTOList字段进行校验，具体如何校验，得看 ObjectBizFieldTypeRefDTO 的定义
            @BizCheck(when = "#basicFieldType == BasicFieldType.OBJECT", value = "#__VALID(objectBizFieldTypeRefDTOList)"),
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
    }


}

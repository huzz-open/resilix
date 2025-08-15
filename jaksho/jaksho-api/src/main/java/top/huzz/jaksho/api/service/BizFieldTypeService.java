package top.huzz.jaksho.api.service;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.remoting.http12.rest.Mapping;
import org.hibernate.validator.constraints.Length;
import top.huzz.jaksho.api.CascadedRequestProvider;
import top.huzz.jaksho.api.dto.ObjectBizFieldTypeRefDTO;
import top.huzz.jaksho.common.constant.BasicFieldType;
import top.huzz.jaksho.common.constant.CollectionType;
import top.huzz.resilix.validation.annotation.BizCheck;

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
    @BizCheck.List(
            @BizCheck(when = "basicFieldType == BasicFieldType.OBJECT", value = "#__VALID(objectBizFieldTypeRefDTOList)")
    )
    class CreateBizFieldTypeRequest implements CascadedRequestProvider<ObjectBizFieldTypeRefDTO> {
        @NotNull
        @Length(min = 1, max = 100)
        @BizCheck("#__UNIQUE")
        private String name;

        @Length(max = 255)
        private String description;

        @NotNull
        @BizCheck("__UNIQUE")
        private CollectionType collectionType;

        @NotNull
        @BizCheck("#__UNIQUE(#this)")
        private BasicFieldType basicFieldType;

        private Integer minimum;

        private Integer maximum;

        private List<ObjectBizFieldTypeRefDTO> objectBizFieldTypeRefDTOList;

        @Override
        public List<ObjectBizFieldTypeRefDTO> cascadedRequests() {
            return objectBizFieldTypeRefDTOList;
        }
    }
}

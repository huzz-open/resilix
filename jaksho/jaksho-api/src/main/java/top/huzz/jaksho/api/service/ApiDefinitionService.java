package top.huzz.jaksho.api.service;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.remoting.http12.HttpMethods;
import org.apache.dubbo.remoting.http12.rest.Mapping;
import org.apache.dubbo.remoting.http12.rest.Param;
import org.apache.dubbo.remoting.http12.rest.ParamType;
import org.hibernate.validator.constraints.Length;
import top.huzz.jaksho.api.CascadedRequestProvider;
import top.huzz.jaksho.api.dto.ApiDefinitionFieldDTO;
import top.huzz.jaksho.common.constant.BodyType;
import top.huzz.jaksho.common.constant.HttpMethod;
import top.huzz.jaksho.common.constant.RawType;
import top.huzz.resilix.validation.annotation.BizCheck;

import java.util.List;

/**
 * 业务字段类型服务接口
 *
 * @author chenji
 * @since 1.0.2
 */
@Mapping("/sr/api-definition")
public interface ApiDefinitionService {

    @Mapping("")
    Integer create(CreateApiDefinitionRequest request);

    @Getter
    @Setter
    @BizCheck.List({
            @BizCheck(when = "bodyType == T(BodyType).RAW", value = "rawType != null", message = "bodyType为RAW时，rawType不能为空"),
            @BizCheck(when = "bodyType == T(BodyType).RAW && rawType == T(RawType).TEXT", value = "#__DB_EXIST_WITH_ID('top.huzz.jaksho.domain.entity.BizFieldType', rawBizFieldTypeId)"),
            @BizCheck("#__DB_UNIQUE('top.huzz.jaksho.domain.entity.ApiDefinition', path, method)"),
            @BizCheck(when = "bodyType != T(BodyType).NONE", value = "#checkApiDefinitionField(apiDefinitionFieldDTOList)")
    })
    class CreateApiDefinitionRequest implements CascadedRequestProvider<ApiDefinitionFieldDTO> {
        @Length(min = 1, max = 100)
        @BizCheck("#__DB_UNIQUE('top.huzz.jaksho.domain.entity.ApiDefinition', #this)")
        private String name;

        @NotEmpty
        @Length(min = 1, max = 100)
        private String path;

        @NotNull
        private HttpMethod method;

        @NotNull
        private BodyType bodyType;

        private RawType rawType;

        private Integer rawBizFieldTypeId;

        @Length(max = 255)
        private String description;

        @Length(max = 255)
        private String remark;

        private List<ApiDefinitionFieldDTO> apiDefinitionFieldDTOList;

        @Override
        public List<ApiDefinitionFieldDTO> cascadedRequests() {
            return apiDefinitionFieldDTOList;
        }
    }

    @Mapping(path = "/{id}", method = HttpMethods.DELETE)
    int delete(@Param(value = "id", type = ParamType.PathVariable) Integer id);
}

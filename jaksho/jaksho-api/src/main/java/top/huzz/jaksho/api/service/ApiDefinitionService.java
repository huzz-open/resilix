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
import top.huzz.jaksho.api.dto.AbstractPageQuery;
import top.huzz.jaksho.api.dto.ApiDefinitionFieldDTO;
import top.huzz.jaksho.common.constant.BodyType;
import top.huzz.jaksho.common.constant.HttpMethod;
import top.huzz.jaksho.common.entity.CombineResult;
import top.huzz.jaksho.common.entity.PageResult;
import top.huzz.jaksho.domain.entity.ApiDefinition;
import top.huzz.jaksho.domain.entity.ApiDefinitionField;
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
    @BizCheck("#__DB_UNIQUE('top.huzz.jaksho.domain.entity.ApiDefinition', path, method)")
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

        @Length(max = 255)
        private String description;

        @Length(max = 255)
        private String remark;

        @BizCheck("#checkApiDefinitionField(#this)")
        private List<ApiDefinitionFieldDTO> apiDefinitionFieldDTOList;

        @Override
        public List<ApiDefinitionFieldDTO> cascadedRequests() {
            return apiDefinitionFieldDTOList;
        }
    }

    @Mapping(path = "/page", method = HttpMethods.POST)
    PageResult<ApiDefinition> pageQuery(ApiDefinitionService.PageQueryRequest request);

    class PageQueryRequest extends AbstractPageQuery<ApiDefinition> {

    }

    @Mapping(path = "/{id}", method = HttpMethods.DELETE)
    int delete(@Param(value = "id", type = ParamType.PathVariable) Integer id);

    @Mapping(value = "/{id}", method = HttpMethods.GET)
    DetailResponse detail(@Param(value = "id", type = ParamType.PathVariable) Integer id);

    @Getter
    @Setter
    class DetailResponse extends ApiDefinition {
        private List<CombineResult> apiDefinitionFields;
    }
}

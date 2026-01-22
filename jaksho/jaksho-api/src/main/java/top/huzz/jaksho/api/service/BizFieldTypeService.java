package top.huzz.jaksho.api.service;

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
import top.huzz.jaksho.api.dto.ObjectBizFieldTypeRefDTO;
import top.huzz.jaksho.common.constant.BasicFieldType;
import top.huzz.jaksho.common.constant.CollectionType;
import top.huzz.jaksho.common.entity.CombineResult;
import top.huzz.jaksho.common.entity.PageResult;
import top.huzz.jaksho.domain.entity.BizFieldType;
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

    @Mapping(path = "", method = HttpMethods.POST)
    Integer create(CreateBizFieldTypeRequest request);

    @Getter
    @Setter
    @BizCheck.List({
            @BizCheck(when = "basicFieldType == T(BasicFieldType).OBJECT", value = "#checkObjectBizFieldTypeRef(objectBizFieldTypeRefDTOList)"),
    })
    class CreateBizFieldTypeRequest implements CascadedRequestProvider<ObjectBizFieldTypeRefDTO> {
        @Length(min = 1, max = 100)
        @BizCheck("#__DB_UNIQUE('top.huzz.jaksho.domain.entity.BizFieldType', #this)")
        private String name;

        @Length(max = 255)
        private String description;

        @NotNull
        private CollectionType collectionType;

        @NotNull
        private BasicFieldType basicFieldType;

        private Integer minimum;

        private Integer maximum;

        private Integer valueDictId;

        private List<ObjectBizFieldTypeRefDTO> objectBizFieldTypeRefDTOList;

        @Override
        public List<ObjectBizFieldTypeRefDTO> cascadedRequests() {
            return objectBizFieldTypeRefDTOList;
        }
    }

    @Mapping(path = "/page", method = HttpMethods.POST)
    PageResult<BizFieldType> pageQuery(PageQueryRequest request);

    @Getter
    @Setter
    class PageQueryRequest extends AbstractPageQuery<BizFieldType> {
        private BasicFieldType basicFieldType;
        private CollectionType collectionType;
    }

    @Mapping(path = "/{id}", method = HttpMethods.DELETE)
    int delete(@Param(value = "id", type = ParamType.PathVariable) Integer id);

    @Mapping(value = "/{id}", method = HttpMethods.GET)
    DetailResponse detail(@Param(value = "id", type = ParamType.PathVariable) Integer id);

    @Mapping(path = "/{id}", method = HttpMethods.PUT)
    int update(@Param(value = "id", type = ParamType.PathVariable) Integer id, @Param(type = ParamType.Body) UpdateBizFieldTypeRequest request);

    @Mapping(path = "/{id}/object-refs", method = HttpMethods.PUT)
    int updateObjectRefs(@Param(value = "id", type = ParamType.PathVariable) Integer id, @Param(type = ParamType.Body) UpdateObjectRefsRequest request);

    @Getter
    @Setter
    class UpdateBizFieldTypeRequest {
        @Length(min = 1, max = 100)
        @BizCheck("#__DB_UNIQUE('top.huzz.jaksho.domain.entity.BizFieldType', #this)")
        private String name;

        @Length(max = 255)
        private String description;

        private Integer valueDictId;
    }

    @Getter
    @Setter
    class UpdateObjectRefsRequest {
        private List<ObjectBizFieldTypeRefDTO> objectBizFieldTypeRefDTOList;
    }

    @Getter
    @Setter
    class DetailResponse extends BizFieldType {
        private List<CombineResult> objectBizFieldTypeRefList;
    }
}

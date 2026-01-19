package top.huzz.jaksho.api.service;

import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.remoting.http12.HttpMethods;
import org.apache.dubbo.remoting.http12.rest.Mapping;
import org.apache.dubbo.remoting.http12.rest.Param;
import org.apache.dubbo.remoting.http12.rest.ParamType;
import org.hibernate.validator.constraints.Length;
import top.huzz.jaksho.api.dto.AbstractPageQuery;
import top.huzz.jaksho.common.entity.PageResult;
import top.huzz.jaksho.domain.entity.Service;
import top.huzz.resilix.validation.annotation.BizCheck;

/**
 * 服务管理服务接口
 *
 * @author mybatis-plus-generator
 * @since 1.0.2
 */
@Mapping("/sr/service")
public interface ServiceService {

    @Mapping("")
    Integer create(CreateServiceRequest request);

    @Getter
    @Setter
    class CreateServiceRequest {
        @BizCheck("#__DB_UNIQUE('top.huzz.jaksho.domain.entity.Service', #this)")
        private Integer serviceCode;

        @Length(min = 1, max = 100)
        @BizCheck("#__DB_UNIQUE('top.huzz.jaksho.domain.entity.Service', #this)")
        private String name;

        @Length(max = 255)
        private String description;

        @Length(max = 255)
        private String remark;
    }

    @Mapping(path = "/page", method = HttpMethods.POST)
    PageResult<Service> pageQuery(ServiceService.PageQueryRequest request);

    @Getter
    @Setter
    class PageQueryRequest extends AbstractPageQuery<Service> {
        private String name;
        private Integer serviceCode;
    }

    @Mapping(path = "/{id}", method = HttpMethods.DELETE)
    int delete(@Param(value = "id", type = ParamType.PathVariable) Integer id);

    @Mapping(path = "/{id}", method = HttpMethods.GET)
    Service detail(@Param(value = "id", type = ParamType.PathVariable) Integer id);

    @Mapping(path = "/{id}", method = HttpMethods.PUT)
    int update(@Param(value = "id", type = ParamType.PathVariable) Integer id, @Param(type = ParamType.Body) UpdateServiceRequest request);

    @Getter
    @Setter
    class UpdateServiceRequest {
        @Length(min = 1, max = 100)
        private String name;

        @Length(max = 255)
        private String description;

        @Length(max = 255)
        private String remark;
    }
}


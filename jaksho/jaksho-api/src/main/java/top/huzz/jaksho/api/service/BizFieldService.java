package top.huzz.jaksho.api.service;

import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.remoting.http12.HttpMethods;
import org.apache.dubbo.remoting.http12.rest.Mapping;
import org.hibernate.validator.constraints.Length;
import top.huzz.jaksho.api.dto.AbstractPageQuery;
import top.huzz.jaksho.common.entity.CombineResult;
import top.huzz.resilix.validation.annotation.BizCheck;

import java.util.List;

/**
 * 业务字段服务接口
 *
 * @author chenji
 * @since 1.0.2
 */
@Mapping("/sr/biz-field")
public interface BizFieldService {

    @Mapping("")
    Integer create(CreateBizFieldRequest request);

    @Getter
    @Setter
    class CreateBizFieldRequest {
        @Length(min = 1, max = 100)
        @BizCheck("#__DB_UNIQUE('top.huzz.jaksho.domain.entity.BizField', #this)")
        private String name;

        @Length(max = 255)
        private String description;

        @BizCheck("#__DB_EXIST_WITH_ID('top.huzz.jaksho.domain.entity.BizFieldType', #this)")
        private Integer bizFieldTypeId;
    }

    @Mapping(path = "/page", method = HttpMethods.POST)
    List<CombineResult> pageQuery(BizFieldService.PageQueryRequest request);

    class PageQueryRequest extends AbstractPageQuery<CombineResult> {

    }
}

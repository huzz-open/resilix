package top.huzz.jaksho.api.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.remoting.http12.HttpMethods;
import org.apache.dubbo.remoting.http12.rest.Mapping;
import org.apache.dubbo.remoting.http12.rest.Param;
import org.apache.dubbo.remoting.http12.rest.ParamType;
import org.hibernate.validator.constraints.Length;
import top.huzz.jaksho.api.dto.AbstractPageQuery;
import top.huzz.jaksho.api.dto.ValueDictItemDTO;
import top.huzz.jaksho.common.constant.ValueDictType;
import top.huzz.jaksho.common.entity.PageResult;
import top.huzz.jaksho.domain.entity.ValueDict;
import top.huzz.jaksho.domain.entity.ValueDictItem;
import top.huzz.resilix.validation.annotation.BizCheck;

import java.util.List;

/**
 * 值字典服务接口
 *
 * @author huzz
 * @since 1.0.2
 */
@Mapping("/sr/value-dict")
public interface ValueDictService {

    /**
     * 创建值字典（包含字典项）
     */
    @Mapping("")
    Integer create(@Valid CreateValueDictRequest request);

    @Getter
    @Setter
    class CreateValueDictRequest {
        /**
         * 值字典名称（下划线命名，如：login_type）
         */
        @Length(min = 1, max = 100)
        @BizCheck("#__DB_UNIQUE('top.huzz.jaksho.domain.entity.ValueDict', #this)")
        private String name;

        /**
         * 值字典描述
         */
        @Length(max = 255)
        private String description;

        /**
         * 值字典类型
         */
        @NotNull
        private ValueDictType type;

        /**
         * 备注
         */
        @Length(max = 255)
        private String remark;

        /**
         * 字典项列表
         */
        @NotNull
        private List<@Valid ValueDictItemDTO> items;
    }

    /**
     * 分页查询值字典列表
     */
    @Mapping(path = "/page", method = HttpMethods.POST)
    PageResult<ValueDict> pageQuery(PageQueryRequest request);

    @Getter
    @Setter
    class PageQueryRequest extends AbstractPageQuery<ValueDict> {
        /**
         * 按类型筛选
         */
        private ValueDictType type;
    }

    /**
     * 查询值字典详情（含字典项）
     */
    @Mapping(value = "/{id}", method = HttpMethods.GET)
    DetailResponse detail(@Param(value = "id", type = ParamType.PathVariable) Integer id);

    @Getter
    @Setter
    class DetailResponse extends ValueDict {
        /**
         * 字典项列表
         */
        private List<ValueDictItem> items;
    }

    /**
     * 更新值字典（含字典项）
     */
    @Mapping(path = "/{id}", method = HttpMethods.PUT)
    int update(@Param(value = "id", type = ParamType.PathVariable) Integer id, 
               @Param(type = ParamType.Body) @Valid UpdateValueDictRequest request);

    @Getter
    @Setter
    class UpdateValueDictRequest {
        /**
         * 值字典描述
         */
        @Length(max = 255)
        private String description;

        /**
         * 备注
         */
        @Length(max = 255)
        private String remark;

        /**
         * 字典项列表
         */
        @NotNull
        private List<@Valid ValueDictItemDTO> items;
    }

    /**
     * 删除值字典（级联删除字典项）
     */
    @Mapping(path = "/{id}", method = HttpMethods.DELETE)
    int delete(@Param(value = "id", type = ParamType.PathVariable) Integer id);
}



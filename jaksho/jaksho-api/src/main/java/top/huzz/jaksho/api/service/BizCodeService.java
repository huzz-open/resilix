package top.huzz.jaksho.api.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.remoting.http12.HttpMethods;
import org.apache.dubbo.remoting.http12.rest.Mapping;
import org.apache.dubbo.remoting.http12.rest.Param;
import org.apache.dubbo.remoting.http12.rest.ParamType;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import top.huzz.jaksho.api.dto.AbstractPageQuery;
import top.huzz.jaksho.common.entity.CombineResult;
import top.huzz.jaksho.common.entity.PageResult;

import java.util.List;

/**
 * 业务码管理服务接口
 *
 * @author mybatis-plus-generator
 * @since 1.0.2
 */
@Mapping("/sr/biz-code")
public interface BizCodeService {

    /**
     * 创建业务码
     */
    @Mapping("")
    Integer create(@Param(type = ParamType.Body) CreateBizCodeRequest request);

    @Getter
    @Setter
    class CreateBizCodeRequest {
        /**
         * 服务ID
         */
        @NotNull(message = "服务ID不能为空")
        private Integer serviceId;

        /**
         * 序号（根据配置决定是否必填）
         * 如果 sequenceMode=auto，则自动生成
         * 如果 sequenceMode=manual，则必须手动输入
         */
        private Integer sequenceNumber;

        /**
         * HTTP状态码
         */
        @NotNull(message = "HTTP状态码不能为空")
        @Range(min = 100, max = 599, message = "HTTP状态码范围：100-599")
        private Integer httpStatus;

        /**
         * 简短描述
         */
        @NotBlank(message = "简短描述不能为空")
        @Length(min = 1, max = 100, message = "简短描述长度：1-100")
        private String shortDesc;

        /**
         * 详细描述
         */
        @Length(max = 500, message = "详细描述最多500个字符")
        private String detailDesc;

        /**
         * 国际化key
         */
        @Length(max = 100, message = "国际化key最多100个字符")
        private String i18nKey;

        /**
         * 备注
         */
        @Length(max = 255, message = "备注最多255个字符")
        private String remark;
    }

    /**
     * 分页查询业务码
     */
    @Mapping(path = "/page", method = HttpMethods.POST)
    PageResult<CombineResult> pageQuery(@Param(type = ParamType.Body) PageQueryRequest request);

    @Getter
    @Setter
    class PageQueryRequest extends AbstractPageQuery<CombineResult> {
        /**
         * 服务ID
         */
        private Integer serviceId;

        /**
         * 业务码
         */
        private Integer code;

        /**
         * HTTP状态码
         */
        private Integer httpStatus;

        /**
         * 简短描述（模糊搜索）
         */
        private String shortDesc;
    }

    /**
     * 删除业务码
     */
    @Mapping(path = "/{id}", method = HttpMethods.DELETE)
    int delete(@Param(value = "id", type = ParamType.PathVariable) Integer id);

    /**
     * 获取业务码详情
     */
    @Mapping(path = "/{id}", method = HttpMethods.GET)
    CombineResult detail(@Param(value = "id", type = ParamType.PathVariable) Integer id);

    /**
     * 更新业务码（仅支持修改描述和备注）
     */
    @Mapping(path = "/{id}", method = HttpMethods.PUT)
    int update(
            @Param(value = "id", type = ParamType.PathVariable) Integer id,
            @Param(type = ParamType.Body) UpdateBizCodeRequest request
    );

    @Getter
    @Setter
    class UpdateBizCodeRequest {
        /**
         * 简短描述
         */
        @Length(min = 1, max = 100, message = "简短描述长度：1-100")
        private String shortDesc;

        /**
         * 详细描述
         */
        @Length(max = 500, message = "详细描述最多500个字符")
        private String detailDesc;

        /**
         * 备注
         */
        @Length(max = 255, message = "备注最多255个字符")
        private String remark;
    }

    /**
     * 获取指定服务的下一个可用序号
     */
    @Mapping(path = "/next-sequence/{serviceId}", method = HttpMethods.GET)
    Integer getNextSequence(@Param(value = "serviceId", type = ParamType.PathVariable) Integer serviceId);

    /**
     * 获取业务码配置
     */
    @Mapping(path = "/config", method = HttpMethods.GET)
    ConfigResponse getConfig();

    @Getter
    @Setter
    class ConfigResponse {
        /**
         * 服务码位数
         */
        private int serviceCodeLength;

        /**
         * 序号位数
         */
        private int sequenceLength;

        /**
         * 序号生成模式：auto-自动生成，manual-手动输入
         */
        private String sequenceMode;

        /**
         * 是否启用国际化
         */
        private boolean i18nEnabled;

        /**
         * 默认语言
         */
        private String defaultLocale;
    }

    /**
     * 根据接口ID获取关联的业务码列表
     */
    @Mapping(path = "/by-api/{apiId}", method = HttpMethods.GET)
    List<CombineResult> getByApiId(@Param(value = "apiId", type = ParamType.PathVariable) Integer apiId);

    /**
     * 为接口绑定业务码
     */
    @Mapping(path = "/bind-to-api", method = HttpMethods.POST)
    int bindToApi(@Param(type = ParamType.Body) BindBizCodeRequest request);

    @Getter
    @Setter
    class BindBizCodeRequest {
        /**
         * 接口ID
         */
        @NotNull(message = "接口ID不能为空")
        private Integer apiId;

        /**
         * 业务码ID列表
         */
        @NotNull(message = "业务码ID列表不能为空")
        private List<Integer> bizCodeIds;
    }

    /**
     * 解除接口与业务码的绑定
     */
    @Mapping(path = "/unbind-from-api/{apiId}/{bizCodeId}", method = HttpMethods.DELETE)
    int unbindFromApi(
            @Param(value = "apiId", type = ParamType.PathVariable) Integer apiId,
            @Param(value = "bizCodeId", type = ParamType.PathVariable) Integer bizCodeId
    );
}


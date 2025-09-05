package top.huzz.jaksho.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import top.huzz.jaksho.common.constant.FieldType;
import top.huzz.jaksho.common.entity.BasicProperties;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 接口关联字段
 * </p>
 *
 * @author mybatis-plus-generator
 * @since 1.0.2
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName("sr_api_definition_field")
public class ApiDefinitionField extends BasicProperties implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据库主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 接口ID
     */
    @TableField("api_id")
    private Integer apiId;

    /**
     * 字段唯一标识符
     */
    @TableField("ulid")
    private String ulid;

    /**
     * 父字段唯一标识符，只有field_type为JSON的时候，父字段才起作用，用于表示字段的层级关系，根字段该值为空字符串
     */
    @TableField("parent_ulid")
    private String parentUlid;

    /**
     * 在同一field_type内的排序序号，值越小越靠前，从0开始
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 字段类型
     */
    @TableField("field_type")
    private FieldType fieldType;

    /**
     * 标准字段ID，引用sr_biz_field_domain.id
     */
    @TableField("biz_field_domain_id")
    private Integer bizFieldDomainId;

    /**
     * 是否必填：0-否，1-是
     */
    @TableField("is_required")
    private Boolean isRequired;

    /**
     * 字段说明
     */
    @TableField("description")
    private String description;

    /**
     * 工作空间ID
     */
    @TableField(value = "workspace_id", fill = FieldFill.INSERT)
    private Integer workspaceId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    public static final String ID = "id";

    public static final String API_ID = "api_id";

    public static final String ULID = "ulid";

    public static final String PARENT_ULID = "parent_ulid";

    public static final String SORT_ORDER = "sort_order";

    public static final String FIELD_TYPE = "field_type";

    public static final String BIZ_FIELD_DOMAIN_ID = "biz_field_domain_id";

    public static final String IS_REQUIRED = "is_required";

    public static final String DESCRIPTION = "description";

    public static final String WORKSPACE_ID = "workspace_id";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";
}

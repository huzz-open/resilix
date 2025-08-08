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
@TableName("sr_api_field")
public class ApiField extends BasicProperties implements Serializable {

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
     * 字段类型
     */
    @TableField("field_type")
    private FieldType fieldType;

    /**
     * 标准字段ID，引用sr_biz_field.id
     */
    @TableField("biz_field_id")
    private Integer bizFieldId;

    /**
     * 字段名称
     */
    @TableField("field_name")
    private String fieldName;

    /**
     * 是否为文件字段：0-否，1-是（仅FORM_DATA时有意义）
     */
    @TableField("is_file_field")
    private Byte isFileField;

    /**
     * 是否必填：0-否，1-是
     */
    @TableField("is_required")
    private Byte isRequired;

    /**
     * 字段说明
     */
    @TableField("description")
    private String description;

    /**
     * 在同一field_type内的排序序号
     */
    @TableField("sort_order")
    private Integer sortOrder;

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

    public static final String FIELD_TYPE = "field_type";

    public static final String BIZ_FIELD_ID = "biz_field_id";

    public static final String FIELD_NAME = "field_name";

    public static final String IS_FILE_FIELD = "is_file_field";

    public static final String IS_REQUIRED = "is_required";

    public static final String DESCRIPTION = "description";

    public static final String SORT_ORDER = "sort_order";

    public static final String WORKSPACE_ID = "workspace_id";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";
}

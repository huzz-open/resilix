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
import top.huzz.jaksho.common.entity.BasicProperties;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 业务字段领域
 * </p>
 *
 * @author mybatis-plus-generator
 * @since 1.0.2
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName("sr_biz_field_domain")
public class BizFieldDomain extends BasicProperties implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据库主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 业务字段id（sr_biz_field数据库主键id）
     */
    @TableField("biz_field_id")
    private Integer bizFieldId;

    /**
     * 业务领域id（sr_biz_domain数据库主键id）
     */
    @TableField("biz_domain_id")
    private Integer bizDomainId;

    /**
     * 业务字段类型id（sr_biz_field_type数据库主键id）。业务领域字段可以拥有新的业务字段类型
     */
    @TableField("biz_field_type_id")
    private Integer bizFieldTypeId;

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

    /**
     * 工作空间ID
     */
    @TableField(value = "workspace_id", fill = FieldFill.INSERT)
    private Integer workspaceId;

    public static final String ID = "id";

    public static final String BIZ_FIELD_ID = "biz_field_id";

    public static final String BIZ_DOMAIN_ID = "biz_domain_id";

    public static final String BIZ_FIELD_TYPE_ID = "biz_field_type_id";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String WORKSPACE_ID = "workspace_id";
}

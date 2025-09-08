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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 * 业务字段
 * </p>
 *
 * @author mybatis-plus-generator
 * @since 1.0.2
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName("sr_biz_field")
public class BizField extends BasicProperties implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据库主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 业务字段名称
     */
    @TableField("name")
    private String name;

    /**
     * 业务字段描述
     */
    @TableField("description")
    private String description;

    /**
     * 业务字段的业务字段类型id（sr_biz_field_type数据库主键id）
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

    @Override
    @SuppressWarnings("all")
    public Map<String, Object> properties() {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("id", id);
        props.put("name", name);
        props.put("description", description);
        props.put("bizFieldTypeId", bizFieldTypeId);
        props.put("createTime", createTime);
        props.put("updateTime", updateTime);
        props.put("workspaceId", workspaceId);
        return props;
    }
}

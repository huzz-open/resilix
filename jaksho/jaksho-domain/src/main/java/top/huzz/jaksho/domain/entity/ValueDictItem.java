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
 * 值字典项
 * </p>
 *
 * @author mybatis-plus-generator
 * @since 1.0.2
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName("sr_value_dict_item")
public class ValueDictItem extends BasicProperties implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据库主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 所属值字典ID
     */
    @TableField("value_dict_id")
    private Integer valueDictId;

    /**
     * 枚举项名称（大写下划线，如：PC_WEB），用于代码生成
     */
    @TableField("name")
    private String name;

    /**
     * 原始值（如："PC", "1", "1.0"）
     */
    @TableField("raw_value")
    private String rawValue;

    /**
     * 值字典项描述
     */
    @TableField("description")
    private String description;

    /**
     * 排序序号（值越小越靠前）
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

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

    @Override
    @SuppressWarnings("all")
    public Map<String, Object> properties() {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("id", id);
        props.put("valueDictId", valueDictId);
        props.put("name", name);
        props.put("rawValue", rawValue);
        props.put("description", description);
        props.put("sortOrder", sortOrder);
        props.put("remark", remark);
        props.put("workspaceId", workspaceId);
        props.put("createTime", createTime);
        props.put("updateTime", updateTime);
        return props;
    }
}

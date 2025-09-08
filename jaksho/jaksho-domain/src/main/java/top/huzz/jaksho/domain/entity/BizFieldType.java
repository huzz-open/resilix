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
import top.huzz.jaksho.common.constant.BasicFieldType;
import top.huzz.jaksho.common.constant.CollectionType;
import top.huzz.jaksho.common.entity.BasicProperties;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 * 业务字段类型
 * </p>
 *
 * @author mybatis-plus-generator
 * @since 1.0.2
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName("sr_biz_field_type")
public class BizFieldType extends BasicProperties implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据库主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 业务字段类型名称
     */
    @TableField("name")
    private String name;

    /**
     * 业务字段类型描述
     */
    @TableField("description")
    private String description;

    /**
     * 最小值或最小长度。在基础字段类型取不同的值的时候有不同的含义。
     * <p/>当{@link #collectionType}为{@link CollectionType#NONE}时，表示该字段不为集合类型，取值含义如下：
     * <li/>数值类型：设定其取值范围的最小值；
     * <li/>string：设定其最小长度
     * <li/>file：设定文件最小大小
     * <p/>反之，如果该字段为集合类型，则该值表示集合中元素的最小个数。
     */
    @TableField("minimum")
    private Integer minimum;

    /**
     * 最大值或最大长度。在基础字段类型取不同的值的时候有不同的含义。
     * <p/>当{@link #collectionType}为{@link CollectionType#NONE}时，表示该字段不为集合类型，取值含义如下：
     * <li/>数值类型：设定其取值范围的最大值；
     * <li/>string：设定其最大长度
     * <li/>file：设定文件最大大小
     * <p/>反之，如果该字段为集合类型，则该值表示集合中元素的最大个数。
     */
    @TableField("maximum")
    private Integer maximum;

    /**
     * 集合类型
     */
    @TableField("collection_type")
    private CollectionType collectionType;

    /**
     * 基础字段类型名称
     */
    @TableField("basic_field_type")
    private BasicFieldType basicFieldType;

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
     * 工作空间数据库主键id
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
        props.put("minimum", minimum);
        props.put("maximum", maximum);
        props.put("collectionType", collectionType);
        props.put("basicFieldType", basicFieldType);
        props.put("createTime", createTime);
        props.put("updateTime", updateTime);
        props.put("workspaceId", workspaceId);
        return props;
    }
}

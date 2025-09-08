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
 * 对象业务字段类型引用表
 * </p>
 *
 * @author mybatis-plus-generator
 * @since 1.0.2
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName("sr_object_biz_field_type_ref")
public class ObjectBizFieldTypeRef extends BasicProperties implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据库主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 对象唯一标识符
     */
    @TableField("ulid")
    private String ulid;

    /**
     * 父对象唯一标识符，根对象该值为空字符串
     */
    @TableField("parent_ulid")
    private String parentUlid;

    /**
     * 在同一层级内的排序序号，值越小越靠前，从0开始
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 该ref所属的业务字段类型数据库主键id（sr_biz_field_type数据库主键id）
     */
    @TableField("biz_field_type_id")
    private Integer bizFieldTypeId;

    /**
     * 标准字段ID，引用sr_biz_field_domain.id
     */
    @TableField("biz_field_domain_id")
    private Integer bizFieldDomainId;

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
        props.put("ulid", ulid);
        props.put("parentUlid", parentUlid);
        props.put("sortOrder", sortOrder);
        props.put("bizFieldTypeId", bizFieldTypeId);
        props.put("bizFieldDomainId", bizFieldDomainId);
        props.put("createTime", createTime);
        props.put("updateTime", updateTime);
        props.put("workspaceId", workspaceId);
        return props;
    }
}

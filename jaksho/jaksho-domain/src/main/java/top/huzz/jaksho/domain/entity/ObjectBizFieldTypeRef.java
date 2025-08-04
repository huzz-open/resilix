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
import java.util.Date;

/**
 * <p>
 * 
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
     *  数据库主键id 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     *  该ref所属的业务字段类型数据库主键id（sr_biz_field_type数据库主键id）
     */
    @TableField("biz_field_type_id")
    private Integer bizFieldTypeId;

    /**
     *  引用的基础字段id（sr_biz_field_type数据库主键id）
     */
    @TableField("ref_id")
    private Integer refId;

    /**
     *  父级id（sr_object_biz_field_type_ref数据库主键），0表示没有父级，也就是说该ref的字段是顶级字段，否则表示该ref的字段是子级字段 
     */
    @TableField("parent_id")
    private Integer parentId;

    /**
     *  排序，值越小越靠前 
     */
    @TableField("order")
    private Integer order;

    /**
     *  创建时间 
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     *  更新时间 
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     *  工作空间数据库主键id 
     */
    @TableField(value = "workspace_id", fill = FieldFill.INSERT)
    private Integer workspaceId;

    public static final String ID = "id";

    public static final String BIZ_FIELD_TYPE_ID = "biz_field_type_id";

    public static final String REF_ID = "ref_id";

    public static final String PARENT_ID = "parent_id";

    public static final String ORDER = "order";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String WORKSPACE_ID = "workspace_id";
}

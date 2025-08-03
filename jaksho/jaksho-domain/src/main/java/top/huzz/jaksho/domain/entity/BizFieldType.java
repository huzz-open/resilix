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
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author huzz
 * @since 2025-08-03 05:07:35
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName(value = "sr_biz_field_type", autoResultMap = true)
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
    @TableField(value = "basic_field_type", typeHandler = top.huzz.jaksho.common.json.CustomFastjson2TypeHandler.class)
    private BasicFieldType basicFieldType;

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

    public static final String NAME = "name";

    public static final String DESCRIPTION = "description";

    public static final String MINIMUM = "minimum";

    public static final String MAXIMUM = "maximum";

    public static final String COLLECTION_TYPE = "collection_type";

    public static final String BASIC_FIELD_TYPE = "basic_field_type";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String WORKSPACE_ID = "workspace_id";
}

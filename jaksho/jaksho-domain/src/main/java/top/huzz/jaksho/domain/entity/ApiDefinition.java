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
import top.huzz.jaksho.common.constant.BodyType;
import top.huzz.jaksho.common.constant.HttpMethod;
import top.huzz.jaksho.common.constant.RawType;
import top.huzz.jaksho.common.entity.BasicProperties;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 接口定义表
 * </p>
 *
 * @author mybatis-plus-generator
 * @since 1.0.2
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName("sr_api_definition")
public class ApiDefinition extends BasicProperties implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据库主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 接口名称
     */
    @TableField("name")
    private String name;

    /**
     * HTTP方法
     */
    @TableField("method")
    private HttpMethod method;

    /**
     * 接口路径，如: /users/{userId}
     */
    @TableField("path")
    private String path;

    /**
     * 接口描述
     */
    @TableField("description")
    private String description;

    /**
     * 请求体类型
     */
    @TableField("body_type")
    private BodyType bodyType;

    /**
     * raw类型的子类型，当body_type=RAW起作用
     */
    @TableField("raw_type")
    private RawType rawType;

    /**
     * 当body_type=RAW且raw_type=TEXT时，指定对应的标准字段ID
     */
    @TableField("raw_text_field_id")
    private Integer rawTextFieldId;

    /**
     * 工作空间ID
     */
    @TableField(value = "workspace_id", fill = FieldFill.INSERT)
    private Integer workspaceId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    public static final String ID = "id";

    public static final String NAME = "name";

    public static final String METHOD = "method";

    public static final String PATH = "path";

    public static final String DESCRIPTION = "description";

    public static final String BODY_TYPE = "body_type";

    public static final String RAW_TYPE = "raw_type";

    public static final String RAW_TEXT_FIELD_ID = "raw_text_field_id";

    public static final String WORKSPACE_ID = "workspace_id";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String REMARK = "remark";
}

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
 * 业务码表
 * </p>
 *
 * @author mybatis-plus-generator
 * @since 1.0.2
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName("sr_biz_code")
public class BizCode extends BasicProperties implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 数据库主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 服务ID，引用sr_service.id
     */
    @TableField("service_id")
    private Integer serviceId;

    /**
     * 完整业务码（服务码+序号）
     */
    @TableField("code")
    private Integer code;

    /**
     * 自增序号
     */
    @TableField("sequence_number")
    private Integer sequenceNumber;

    /**
     * HTTP状态码（如：200,400,500）
     */
    @TableField("http_status")
    private Integer httpStatus;

    /**
     * 简短描述
     */
    @TableField("short_desc")
    private String shortDesc;

    /**
     * 详细描述
     */
    @TableField("detail_desc")
    private String detailDesc;

    /**
     * 国际化key（如：biz.code.user.not.found）
     */
    @TableField("i18n_key")
    private String i18nKey;

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

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    @Override
    @SuppressWarnings("all")
    public Map<String, Object> properties() {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("id", id);
        props.put("serviceId", serviceId);
        props.put("code", code);
        props.put("sequenceNumber", sequenceNumber);
        props.put("httpStatus", httpStatus);
        props.put("shortDesc", shortDesc);
        props.put("detailDesc", detailDesc);
        props.put("i18nKey", i18nKey);
        props.put("workspaceId", workspaceId);
        props.put("createTime", createTime);
        props.put("updateTime", updateTime);
        props.put("remark", remark);
        return props;
    }
}

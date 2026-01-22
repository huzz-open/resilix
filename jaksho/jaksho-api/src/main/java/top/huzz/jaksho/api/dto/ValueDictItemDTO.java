package top.huzz.jaksho.api.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

/**
 * 值字典项 DTO
 *
 * @author huzz
 * @since 1.0.2
 */
@Getter
@Setter
public class ValueDictItemDTO {
    /**
     * 数据库主键ID（更新时需要）
     */
    private Integer id;

    /**
     * 枚举项名称（大写下划线，如：PC_WEB），用于代码生成
     */
    @Length(min = 1, max = 100)
    private String name;

    /**
     * 原始值（如："PC", "1", "1.0"）
     */
    @Length(min = 1, max = 100)
    private String rawValue;

    /**
     * 值字典项描述
     */
    @Length(max = 255)
    private String description;

    /**
     * 排序序号（值越小越靠前）
     */
    private Integer sortOrder;

    /**
     * 备注
     */
    @Length(max = 255)
    private String remark;
}



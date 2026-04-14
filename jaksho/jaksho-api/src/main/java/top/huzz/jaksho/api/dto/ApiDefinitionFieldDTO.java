package top.huzz.jaksho.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import top.huzz.jaksho.common.constant.FieldType;
import top.huzz.resilix.validation.annotation.BizCheck;

/**
 * @author huzz
 * @since 1.0.2
 */
@Getter
@Setter
public class ApiDefinitionFieldDTO implements TreeDTO {
	@NotNull
	private FieldType fieldType;

	@BizCheck("#__DB_EXIST_WITH_ID('top.huzz.jaksho.domain.entity.BizFieldDomain', #this)")
	private Integer bizFieldDomainId;

	private Boolean isRequired;

	@Length(max = 255)
	private String description;

	/**
	 * 插槽映射配置（JSON格式），格式：{"slotFieldName":targetFieldDomainId}
	 */
	private String slotMappings;

	@NotNull
	@Length(min = 1, max = 26)
	private String ulid;

	@Length(min = 1, max = 26)
	private String parentUlid;

	@NotNull
	private Integer sortOrder;
}

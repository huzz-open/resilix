package top.huzz.jaksho.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import top.huzz.resilix.validation.annotation.BizCheck;

/**
 * @author huzz
 * @since 1.0.2
 */
@Getter
@Setter
public class ObjectBizFieldTypeRefDTO {
	@NotNull
	@BizCheck("#__DB_EXIST_WITH_ID('top.huzz.jaksho.domain.entity.BizField', #this)")
	private Integer refId;
	@NotNull
	private Integer parentId;
	@NotNull
	private Integer sortOrder;
}

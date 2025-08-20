package top.huzz.jaksho.api.service;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.dubbo.remoting.http12.rest.Mapping;
import org.hibernate.validator.constraints.Length;
import top.huzz.resilix.validation.annotation.BizCheck;

/**
 * 业务字段类型服务接口
 *
 * @author chenji
 * @since 1.0.2
 */
@Mapping("/sr/biz-field")
public interface BizFieldService {

	@Mapping("")
	Integer create(CreateBizFieldRequest request);

	@Getter
	@Setter
	class CreateBizFieldRequest {
		@NotNull
		@Length(min = 1, max = 100)
		@BizCheck("#__DB_UNIQUE('top.huzz.jaksho.domain.entity.BizField', #this)")
		private String name;

		@Length(max = 255)
		private String description;

		@BizCheck("#__DB_EXIST_WITH_ID('top.huzz.jaksho.domain.entity.BizFieldType', #this)")
		private Integer bizFieldTypeId;
	}
}

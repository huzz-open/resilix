package top.huzz.jaksho.bizfun;

import top.huzz.jaksho.api.config.AppConfigUtil;
import top.huzz.jaksho.api.dto.ApiDefinitionFieldDTO;
import top.huzz.resilix.validation.Validations;
import top.huzz.resilix.validation.annotation.BizCheckFunction;

import java.util.List;

/**
 * @author huzz
 * @since 1.0.2
 */
@SuppressWarnings("unused")
public class ApiDefinitionFunctions {

	@BizCheckFunction("checkApiDefinitionField")
	public static boolean check(List<ApiDefinitionFieldDTO> apiDefinitionFieldDTOList) {
		// 首先进行基本的Bean验证
		Validations.valid(apiDefinitionFieldDTOList);

		int maxDepth = AppConfigUtil.getAppConfig().getApiDefinition().getRefMaxDepth();
		// 同一种fieldType下，bizFieldDomainId必须唯一，所以这里是用 fieldType.name()、bizFieldDomainId 作为唯一性校验字段
		return TreeDTOUtil.check(apiDefinitionFieldDTOList, maxDepth, dto -> dto.getFieldType() + "、" + dto.getBizFieldDomainId(), "fieldType、bizFieldDomainId");
	}

}

package top.huzz.jaksho.bizfun;

import top.huzz.jaksho.api.config.AppConfigUtil;
import top.huzz.jaksho.api.dto.ObjectBizFieldTypeRefDTO;
import top.huzz.resilix.validation.Validations;
import top.huzz.resilix.validation.annotation.BizCheckFunction;

import java.util.List;

/**
 * @author huzz
 * @since 1.0.2
 */
@SuppressWarnings("unused")
public class BizFieldTypeFunctions {

	@BizCheckFunction("checkObjectBizFieldTypeRef")
	public static boolean check(List<ObjectBizFieldTypeRefDTO> objectBizFieldTypeRefDTOList) {
		// 首先进行基本的Bean验证
		Validations.valid(objectBizFieldTypeRefDTOList);

		int maxDepth = AppConfigUtil.getAppConfig().getBizFieldType().getRefMaxDepth();
		return TreeDTOUtil.check(objectBizFieldTypeRefDTOList, maxDepth, ObjectBizFieldTypeRefDTO::getBizFieldDomainId, "bizFieldDomainId");
	}

}

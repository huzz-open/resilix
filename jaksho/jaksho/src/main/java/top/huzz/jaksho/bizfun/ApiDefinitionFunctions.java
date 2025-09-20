package top.huzz.jaksho.bizfun;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Configuration;
import top.huzz.jaksho.api.config.AppConfigUtil;
import top.huzz.jaksho.api.dto.ApiDefinitionFieldDTO;
import top.huzz.jaksho.common.constant.BasicFieldType;
import top.huzz.jaksho.common.constant.CollectionType;
import top.huzz.jaksho.common.constant.FieldType;
import top.huzz.jaksho.domain.entity.BizFieldDomain;
import top.huzz.jaksho.domain.entity.BizFieldType;
import top.huzz.jaksho.domain.mapper.BizFieldDomainMapper;
import top.huzz.jaksho.domain.mapper.BizFieldTypeMapper;
import top.huzz.resilix.validation.Validations;
import top.huzz.resilix.validation.annotation.BizCheckFunction;

import java.util.List;

/**
 * @author huzz
 * @since 1.0.2
 */
@SuppressWarnings("unused")
@Configuration
public class ApiDefinitionFunctions {

    private static BizFieldDomainMapper bizFieldDomainMapper;
    private static BizFieldTypeMapper bizFieldTypeMapper;

    public ApiDefinitionFunctions(BizFieldDomainMapper bizFieldDomainMapper, BizFieldTypeMapper bizFieldTypeMapper) {
        ApiDefinitionFunctions.bizFieldDomainMapper = bizFieldDomainMapper;
        ApiDefinitionFunctions.bizFieldTypeMapper = bizFieldTypeMapper;
    }

    @BizCheckFunction("checkApiDefinitionField")
    public static boolean check(List<ApiDefinitionFieldDTO> apiDefinitionFieldDTOList) {
        if (CollectionUtils.isEmpty(apiDefinitionFieldDTOList)) {
            return true;
        }
        // 首先进行基本的Bean验证
        Validations.valid(apiDefinitionFieldDTOList);

        // 检查路径参数是否符合要求：路径参数的基础类型不能是OBJECT、FILE，且集合类型只能是NONE
        for (ApiDefinitionFieldDTO dto : apiDefinitionFieldDTOList) {
            if (dto.getFieldType() == FieldType.PATH) {
                // TODO 后续优化成接口、缓存的方式获取数据
                Integer bizFieldDomainId = dto.getBizFieldDomainId();
                BizFieldDomain bizFieldDomain = bizFieldDomainMapper.selectById(bizFieldDomainId);
                if (bizFieldDomain == null) {
                    throw new IllegalArgumentException("业务字段领域不存在");
                }
                Integer bizFieldTypeId = bizFieldDomain.getBizFieldTypeId();
                BizFieldType bizFieldType = bizFieldTypeMapper.selectById(bizFieldTypeId);
                if (bizFieldType == null) {
                    throw new IllegalArgumentException("业务字段类型不存在");
                }
                if (bizFieldType.getBasicFieldType() == BasicFieldType.OBJECT || bizFieldType.getBasicFieldType() == BasicFieldType.FILE) {
                    throw new IllegalArgumentException("路径参数的基础类型不能是OBJECT、FILE");
                }
                if (bizFieldType.getCollectionType() != CollectionType.NONE) {
                    throw new IllegalArgumentException("路径参数的集合类型只能是NONE");

                }
            }
        }

        List<ApiDefinitionFieldDTO> treeList = apiDefinitionFieldDTOList.stream().filter(dto -> dto.getFieldType() == FieldType.RAW_JSON).toList();
        if (CollectionUtils.isEmpty(treeList)) {
            return true;
        }
        int maxDepth = AppConfigUtil.getAppConfig().getApiDefinition().getRefMaxDepth();
        // 同一种fieldType下，bizFieldDomainId必须唯一，所以这里是用 fieldType.name()、bizFieldDomainId 作为唯一性校验字段
        return TreeDTOUtil.check(treeList, maxDepth, dto -> dto.getFieldType() + "、" + dto.getBizFieldDomainId(), "fieldType、bizFieldDomainId");
    }

}

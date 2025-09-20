package top.huzz.jaksho.bizfun;

import org.springframework.context.annotation.Configuration;
import top.huzz.jaksho.common.constant.BasicFieldType;
import top.huzz.jaksho.domain.entity.BizField;
import top.huzz.jaksho.domain.entity.BizFieldType;
import top.huzz.jaksho.domain.mapper.BizFieldMapper;
import top.huzz.jaksho.domain.mapper.BizFieldTypeMapper;
import top.huzz.resilix.validation.annotation.BizCheckFunction;

/**
 * @author huzz
 * @since 1.0.2
 */
@Configuration
@SuppressWarnings("unused")
public class BizFieldDomainFunctions {

    private static BizFieldMapper bizFieldMapper;
    private static BizFieldTypeMapper bizFieldTypeMapper;

    public BizFieldDomainFunctions(BizFieldMapper bizFieldMapper, BizFieldTypeMapper bizFieldTypeMapper) {
        BizFieldDomainFunctions.bizFieldMapper = bizFieldMapper;
        BizFieldDomainFunctions.bizFieldTypeMapper = bizFieldTypeMapper;
    }

    /**
     * 业务字段类型与业务字段是否匹配
     *
     * @param bizFieldId     业务字段ID
     * @param bizFieldTypeId 业务字段类型ID
     */
    @BizCheckFunction
    public static void isBizFieldDomainMatched(Integer bizFieldId, Integer bizFieldTypeId) {
        BizField bizField = bizFieldMapper.selectById(bizFieldId);
        if (bizField == null) {
            throw new IllegalArgumentException("业务字段不存在");
        }
        Integer dbBizFieldTypeId = bizField.getBizFieldTypeId();
        if (!dbBizFieldTypeId.equals(bizFieldTypeId)) {
            throw new IllegalArgumentException("字段类型与业务字段不匹配");
        }
    }

    /**
     * 业务字段类型不能为对象类型
     *
     * @param bizFieldTypeId 业务字段类型ID
     */
    @BizCheckFunction
    public static void mustNotBasicObjectType(Integer bizFieldTypeId) {
        BizFieldType bizFieldType = bizFieldTypeMapper.selectById(bizFieldTypeId);
        if (bizFieldType == null) {
            throw new IllegalArgumentException("业务字段类型不存在");
        }
        if (bizFieldType.getBasicFieldType() == BasicFieldType.OBJECT) {
            throw new IllegalArgumentException("业务字段类型不能为对象类型");
        }
    }

}

package top.huzz.jaksho.common.util;

import top.huzz.jaksho.common.constant.BasicFieldType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 插槽映射验证工具类
 * 用于验证插槽映射的完整性和类型匹配
 *
 * @author system
 * @since 1.0.3
 */
public class SlotMappingValidator {

    /**
     * 验证结果
     */
    public static class ValidationResult {
        private boolean valid;
        private List<String> errors;

        public ValidationResult() {
            this.valid = true;
            this.errors = new ArrayList<>();
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public void addError(String error) {
            this.errors.add(error);
            this.valid = false;
        }

        public String getErrorMessage() {
            return String.join("; ", errors);
        }
    }

    /**
     * 插槽定义信息
     */
    public static class SlotDefinition {
        private String slotName;
        private BasicFieldType requiredType;

        public SlotDefinition(String slotName, BasicFieldType requiredType) {
            this.slotName = slotName;
            this.requiredType = requiredType;
        }

        public String getSlotName() {
            return slotName;
        }

        public BasicFieldType getRequiredType() {
            return requiredType;
        }
    }

    /**
     * 验证插槽映射
     *
     * @param slotDefinitions 插槽定义列表（插槽名称 -> 要求的基础类型）
     * @param slotMappings    插槽映射（插槽名称 -> 目标字段的基础类型）
     * @return 验证结果
     */
    public static ValidationResult validate(
            List<SlotDefinition> slotDefinitions,
            Map<String, BasicFieldType> slotMappings
    ) {
        ValidationResult result = new ValidationResult();

        if (slotDefinitions == null || slotDefinitions.isEmpty()) {
            return result; // 没有插槽，验证通过
        }

        if (slotMappings == null) {
            slotMappings = new HashMap<>();
        }

        // 检查所有插槽是否都已映射
        for (SlotDefinition slotDef : slotDefinitions) {
            String slotName = slotDef.getSlotName();
            BasicFieldType requiredType = slotDef.getRequiredType();

            // 检查插槽是否已映射
            if (!slotMappings.containsKey(slotName)) {
                result.addError(String.format("插槽 '%s' 未映射", slotName));
                continue;
            }

            // 检查类型是否匹配
            BasicFieldType mappedType = slotMappings.get(slotName);
            if (mappedType == null) {
                result.addError(String.format("插槽 '%s' 映射的字段类型为空", slotName));
                continue;
            }

            // 如果插槽要求特定类型，则检查类型是否匹配
            if (requiredType != null && !requiredType.equals(mappedType)) {
                result.addError(String.format(
                        "插槽 '%s' 类型不匹配：要求 %s，实际 %s",
                        slotName, requiredType, mappedType
                ));
            }
        }

        return result;
    }

    /**
     * 简化版验证：只检查所有插槽是否都已映射
     *
     * @param requiredSlots 必需的插槽名称列表
     * @param slotMappings  插槽映射（插槽名称 -> 字段ID）
     * @return 验证结果
     */
    public static ValidationResult validateMappingCompleteness(
            List<String> requiredSlots,
            Map<String, Integer> slotMappings
    ) {
        ValidationResult result = new ValidationResult();

        if (requiredSlots == null || requiredSlots.isEmpty()) {
            return result;
        }

        if (slotMappings == null) {
            slotMappings = new HashMap<>();
        }

        for (String slotName : requiredSlots) {
            if (!slotMappings.containsKey(slotName) || slotMappings.get(slotName) == null) {
                result.addError(String.format("插槽 '%s' 未映射", slotName));
            }
        }

        return result;
    }
}

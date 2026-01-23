package top.huzz.jaksho.common.util;

import lombok.Data;
import top.huzz.jaksho.common.constant.BasicFieldType;

import java.util.ArrayList;
import java.util.List;

/**
 * 插槽识别工具类
 * 基于 BasicFieldType.SLOT 识别字段中的插槽
 *
 * @author system
 * @since 1.0.3
 */
public class SlotDetector {

    /**
     * 插槽信息
     */
    @Data
    public static class SlotInfo {
        /**
         * 插槽字段的ULID（sr_object_biz_field_type_ref.ulid）
         */
        private String ulid;

        /**
         * 插槽名称（字段名称）
         */
        private String slotName;

        /**
         * 插槽描述
         */
        private String description;

        /**
         * 插槽要求的基础类型（可选，用于类型校验）
         */
        private BasicFieldType requiredType;

        public SlotInfo() {
        }

        public SlotInfo(String ulid, String slotName, String description) {
            this.ulid = ulid;
            this.slotName = slotName;
            this.description = description;
        }

        public SlotInfo(String ulid, String slotName, String description, BasicFieldType requiredType) {
            this.ulid = ulid;
            this.slotName = slotName;
            this.description = description;
            this.requiredType = requiredType;
        }
    }

    /**
     * 判断是否为叶子节点
     *
     * @param ulid    当前节点的ULID
     * @param allRefs 所有引用列表
     * @return 是否为叶子节点
     */
    public static boolean isLeafNode(String ulid, List<?> allRefs) {
        if (allRefs == null || allRefs.isEmpty()) {
            return true;
        }

        // 检查是否有子节点（parent_ulid == ulid）
        for (Object ref : allRefs) {
            try {
                // 通过反射获取 parentUlid 字段
                String parentUlid = (String) ref.getClass().getMethod("getParentUlid").invoke(ref);
                if (ulid.equals(parentUlid)) {
                    return false;  // 有子节点，不是叶子
                }
            } catch (Exception e) {
                // 忽略异常
            }
        }

        return true;  // 没有子节点，是叶子
    }
}

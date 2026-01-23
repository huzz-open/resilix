-- ========================================
-- 接口响应体功能增量迁移脚本
-- 版本: V1.0.3
-- 描述: 添加字段属性和SLOT基础字段类型，支持接口响应体定义
-- 作者: system
-- 日期: 2026-01-23
-- ========================================

-- 1. 修改业务字段表，添加字段属性标识
ALTER TABLE sr_biz_field
ADD COLUMN field_attributes INT DEFAULT 3 COMMENT '字段属性（位标识）：1=输入 2=输出 3=输入输出均可';

ALTER TABLE sr_biz_field
ADD INDEX idx_field_attributes (field_attributes);

-- 2. 修改字段领域关联表，添加字段属性标识
ALTER TABLE sr_biz_field_domain
ADD COLUMN field_attributes INT DEFAULT 3 COMMENT '字段属性（位标识）：1=输入 2=输出 3=输入输出均可';

ALTER TABLE sr_biz_field_domain
ADD INDEX idx_field_attributes (field_attributes);

-- 3. 修改接口字段表，添加插槽映射字段
ALTER TABLE sr_api_definition_field
ADD COLUMN slot_mappings TEXT COMMENT '插槽映射配置（JSON格式），格式：{"slotFieldName":targetFieldDomainId}';

-- 4. 创建插槽领域（用于SLOT类型字段，解决字段名冲突）
INSERT INTO sr_biz_domain (id, name, description, workspace_id)
VALUES (-2, 'SLOT', '插槽领域：用于定义可替换的字段占位符', 1)
ON DUPLICATE KEY UPDATE description = '插槽领域：用于定义可替换的字段占位符';

-- 注意：
-- - BasicFieldType.SLOT 的添加在代码层面完成，无需数据库迁移
-- - FieldType.RESPONSE_OK 的添加在代码层面完成，无需数据库迁移
-- - 插槽领域（id=-2）用于解决字段名冲突：同一字段名可在不同领域有不同类型

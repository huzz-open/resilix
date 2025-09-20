<template>
  <div class="j-bfd-card">
    <!-- 第一行：名称：bizDomain.name bizFieldType.name[bizFieldType.basicFieldType] -->
    <div class="row1">
      <t-tooltip
        placement="top"
        :content="get(entity, 'bizDomain.description') || t('pages.apiDefinition.drawer.none')"
      >
        <span class="domain-pill">{{ get(entity, 'bizDomain.name') || t('pages.apiDefinition.drawer.none') }}</span>
      </t-tooltip>
      <span class="name">
        {{ get(entity, 'bizFieldType.name') || get(entity, 'bizField.name') || get(entity, 'name') }}
        <span class="type">[{{ get(entity, 'bizFieldType.basicFieldType') || '-' }}]</span>
      </span>
    </div>

    <!-- 第二行：取值区间 -->
    <div class="row2">
      <span class="label">{{ t('pages.apiDefinition.drawer.rangeTitle') }}：</span>
      <span class="val"
        >[{{ get(entity, 'bizFieldType.minimum') ?? '-' }}, {{ get(entity, 'bizFieldType.maximum') ?? '-' }}]</span
      >
    </div>

    <!-- 第三行：字段类型描述 -->
    <div
      v-if="
        get(entity, 'bizFieldType.description') || get(entity, 'bizField.description') || get(entity, 'description')
      "
      class="row3"
    >
      <span class="label">{{ t('pages.apiDefinition.drawer.fieldTypeDescriptionTitle') }}：</span>
      <span class="val">{{
        get(entity, 'bizFieldType.description') || get(entity, 'bizField.description') || get(entity, 'description')
      }}</span>
    </div>
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue';

import { t } from '@/locales';

const props = defineProps<{ entity: Record<string, any> | null | undefined; mode?: 'inline' | 'panel' }>();

const entity = computed(() => props.entity || {});
computed(
  () =>
    entity.value?.bizFieldType &&
    (entity.value.bizFieldType.minimum != null || entity.value.bizFieldType.maximum != null),
);
function get(obj: any, path: string): any {
  if (!obj) return undefined;
  return path.split('.').reduce((acc, k) => (acc == null ? acc : acc[k]), obj);
}
</script>
<style scoped>
.j-bfd-card {
  max-width: 520px;
}

.row1 {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}

.domain-pill {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 8px;
  box-shadow: inset 0 1px 3px rgb(0 0 0 / 25%);
  background: linear-gradient(180deg, rgb(255 255 255 / 6%), rgb(255 255 255 / 2%));
  border: 1px solid var(--td-component-border);
  color: var(--td-text-color-primary);
}

.name {
  font-weight: 600;
}

.name .type {
  margin-left: 6px;
  color: var(--td-text-color-placeholder);
  font-weight: 400;
}

.row2,
.row3 {
  margin: 3px 0;
}

.label {
  color: var(--td-text-color-placeholder);
}

.val {
  color: var(--td-text-color-primary);
}

.tags :deep(.t-tag) {
  line-height: 18px;
  height: 20px;
}

.desc {
  margin-top: 4px;
  color: var(--td-text-color-secondary);
  font-size: 12px;
}

.panel-grid {
  display: grid;
  grid-template-columns: 120px 1fr;
  grid-gap: 4px 12px;
}

.panel-grid .label {
  color: var(--td-text-color-placeholder);
}

.panel-grid .val {
  color: var(--td-text-color-primary);
  word-break: break-all;
}
</style>

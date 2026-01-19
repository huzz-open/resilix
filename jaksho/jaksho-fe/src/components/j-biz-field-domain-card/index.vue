<template>
  <div :class="cardClasses">
    <!-- 主行：领域 + 名称 + 类型 -->
    <div class="card-main">
      <t-tooltip v-if="showDomain && domainName" :content="domainDescription" placement="top">
        <span class="domain-badge">{{ domainName }}</span>
      </t-tooltip>

      <span class="field-name">{{ fieldName }}</span>

      <span v-if="basicFieldType" class="field-type">[{{ basicFieldType }}]</span>
    </div>

    <!-- 次行：取值区间 -->
    <div v-if="showRange && hasRange" class="card-row">
      <span class="row-label">{{ t('pages.apiDefinition.drawer.rangeTitle') }}：</span>
      <span class="row-value">[{{ minimum ?? '-' }}, {{ maximum ?? '-' }}]</span>
    </div>

    <!-- 描述行 -->
    <div v-if="showDescription && description" class="card-row">
      <span class="row-label">{{ t('pages.apiDefinition.drawer.fieldTypeDescriptionTitle') }}：</span>
      <span class="row-value">{{ description }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { get } from 'lodash'
import { t } from '@/locales'
import type { BizFieldDomainEntity, JBizFieldDomainCardProps } from './types'
import type { Density } from '@/types/common'

const props = withDefaults(
  defineProps<{
    entity: BizFieldDomainEntity | null | undefined
    density?: Density
    showDomain?: boolean
    showRange?: boolean
    showDescription?: boolean
  }>(),
  {
    density: 'comfortable',
    showDomain: true,
    showRange: true,
    showDescription: true,
  },
)

// ========== 计算属性 ==========
const cardClasses = computed(() => ['j-biz-field-domain-card', `density-${props.density}`])

const domainName = computed(() => get(props.entity, 'bizDomain.name'))

const domainDescription = computed(() => get(props.entity, 'bizDomain.description') || t('pages.apiDefinition.drawer.none'))

const fieldName = computed(
  () => get(props.entity, 'bizFieldType.name') || get(props.entity, 'bizField.name') || get(props.entity, 'name'),
)

const basicFieldType = computed(() => get(props.entity, 'bizFieldType.basicFieldType') || get(props.entity, 'basicFieldType'))

const minimum = computed(() => get(props.entity, 'bizFieldType.minimum'))

const maximum = computed(() => get(props.entity, 'bizFieldType.maximum'))

const hasRange = computed(() => minimum.value != null || maximum.value != null)

const description = computed(
  () =>
    get(props.entity, 'bizFieldType.description') ||
    get(props.entity, 'bizField.description') ||
    get(props.entity, 'description'),
)
</script>

<style lang="less" scoped>
// ========== 基础样式 ==========
.j-biz-field-domain-card {
  max-width: 520px;

  .card-main {
    display: flex;
    align-items: center;
    gap: var(--card-gap);
  }

  .domain-badge {
    display: inline-flex;
    align-items: center;
    padding: var(--badge-padding);
    border-radius: var(--td-radius-default);
    background: var(--td-bg-color-secondarycontainer);
    border: 1px solid var(--td-component-border);
    color: var(--td-text-color-primary);
    font-size: var(--badge-font-size);
    white-space: nowrap;
  }

  .field-name {
    font-weight: 600;
    color: var(--td-text-color-primary);
    font-size: var(--field-name-font-size);
  }

  .field-type {
    color: var(--td-text-color-placeholder);
    font-size: var(--field-type-font-size);
  }

  .card-row {
    margin-top: var(--row-spacing);
    font-size: var(--row-font-size);

    .row-label {
      color: var(--td-text-color-placeholder);
    }

    .row-value {
      color: var(--td-text-color-primary);
    }
  }
}

// ========== 密度变体（CSS Variables）==========
.density-compact {
  --card-gap: 6px;
  --badge-padding: 1px 6px;
  --badge-font-size: 12px;
  --field-name-font-size: 13px;
  --field-type-font-size: 12px;
  --row-spacing: 2px;
  --row-font-size: 12px;
}

.density-comfortable {
  --card-gap: 10px;
  --badge-padding: 2px 8px;
  --badge-font-size: 13px;
  --field-name-font-size: 14px;
  --field-type-font-size: 13px;
  --row-spacing: 4px;
  --row-font-size: 13px;
}

.density-spacious {
  --card-gap: 14px;
  --badge-padding: 4px 12px;
  --badge-font-size: 14px;
  --field-name-font-size: 15px;
  --field-type-font-size: 14px;
  --row-spacing: 8px;
  --row-font-size: 14px;
}
</style>

<template>
  <t-dialog
    v-model:visible="visible"
    :header="t('pages.apiDefinition.drawer.selectField')"
    width="70%"
    @cancel="handleCancel"
    @confirm="handleConfirm"
  >
    <t-space direction="vertical" style="width: 100%">
      <!-- 搜索框 -->
      <t-input
        v-model="keyword"
        :placeholder="t('pages.apiDefinition.drawer.searchPlaceholder')"
        clearable
        @change="handleKeywordChange"
      >
        <template #suffix-icon>
          <search-icon size="16px" />
        </template>
      </t-input>

      <!-- 表格 -->
      <t-table
        v-model:selected-row-keys="selectedRowKeys"
        select-on-row-click
        :data="listData"
        :columns="selectorColumns"
        row-key="bizFieldDomain.id"
        :hover="true"
        :pagination="pagination"
        :loading="loading"
        @page-change="handlePageChange"
        @select-change="handleSelectChange"
      />
    </t-space>
  </t-dialog>
</template>

<script setup lang="ts">
import { computed, h } from 'vue'
import { SearchIcon } from 'tdesign-icons-vue-next'
import { Tooltip } from 'tdesign-vue-next'
import type { PrimaryTableCol } from 'tdesign-vue-next'
import { t } from '@/locales'
import { useFieldSelector } from './composables/useFieldSelector'

const props = defineProps<{
  filters?: Record<string, any>
  existingIds?: Set<string | number>
}>()

const emit = defineEmits<{
  select: [rows: any[]]
}>()

// ========== 使用 Composable ==========
const {
  visible,
  keyword,
  loading,
  listData,
  selectedRowKeys,
  pagination,
  open,
  close,
  handleKeywordChange,
  handlePageChange,
  handleSelectChange,
  confirm,
  getRowId,
} = useFieldSelector({
  filters: props.filters,
  onSelect: (rows) => emit('select', rows),
})

// ========== 表格列定义 ==========
const selectorColumns = computed<PrimaryTableCol[]>(() => [
  {
    colKey: 'row-select',
    type: 'multiple',
    width: 52,
    checkProps: ({ row }: any) => ({
      disabled: props.existingIds?.has(getRowId(row)) ?? false,
    }),
  },
  {
    title: t('pages.apiDefinition.drawer.selector.name'),
    colKey: 'bizField.name',
    ellipsis: true,
  },
  {
    title: t('pages.apiDefinition.drawer.selector.domain'),
    colKey: 'bizDomain.name',
    width: 160,
    cell: (_h: any, { row }: any) => {
      const domainName = row?.bizDomain?.name as string | undefined
      if (!domainName) return null
      const domainDesc = (row?.bizDomain?.description as string | undefined) || t('pages.apiDefinition.drawer.none')
      return h(
        Tooltip as any,
        { content: domainDesc, placement: 'top' },
        { default: () => h('span', { class: 'domain-pill' }, domainName) },
      )
    },
  },
  {
    title: t('pages.apiDefinition.drawer.selector.basicFieldType'),
    colKey: 'bizFieldType.basicFieldType',
    ellipsis: true,
  },
  {
    title: t('pages.apiDefinition.drawer.selector.collectionType'),
    colKey: 'bizFieldType.collectionType',
    ellipsis: true,
  },
  {
    title: t('pages.apiDefinition.drawer.selector.minimum'),
    colKey: 'bizFieldType.minimum',
    width: 120,
  },
  {
    title: t('pages.apiDefinition.drawer.selector.maximum'),
    colKey: 'bizFieldType.maximum',
    width: 120,
  },
  {
    title: t('pages.apiDefinition.drawer.selector.description'),
    colKey: 'bizField.description',
    ellipsis: true,
  },
])

// ========== 事件处理 ==========
const handleCancel = () => {
  close()
}

const handleConfirm = () => {
  confirm()
}

// ========== 暴露方法 ==========
defineExpose({
  open,
  close,
})
</script>

<style lang="less" scoped>
.domain-pill {
  display: inline-block;
  padding: 1px 6px;
  border-radius: 8px;
  box-shadow: inset 0 1px 3px rgb(0 0 0 / 25%);
  background: linear-gradient(180deg, rgb(255 255 255 / 6%), rgb(255 255 255 / 2%));
  border: 1px solid var(--td-component-border);
  color: var(--td-text-color-primary);
  margin-right: 0;
}
</style>


<template>
  <div class="j-kvp-table">
    <!-- 基础表格 -->
    <base-kvp-table
      :rows="internalRows"
      :row-key="rowKey"
      :value-title-key="valueTitleKey"
      :hide-delete-for-last-empty="hideDeleteForLastEmpty"
      :open-by-click="openByClick"
      :disabled="disabled"
      @row-click="handleRowClick"
      @cell-change="handleCellChange"
      @remove="handleRemove"
      @clear="handleClear"
    />

    <!-- 字段选择器 -->
    <kvp-field-selector ref="selectorRef" :filters="selectorFilters" :existing-ids="existingIds" @select="handleSelect" />
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ulid } from 'ulid'
import type { KvpRow } from './types'
import { useKvpTable } from './composables/useKvpTable'
import BaseKvpTable from './BaseKvpTable.vue'
import KvpFieldSelector from './KvpFieldSelector.vue'

const props = withDefaults(
  defineProps<{
    rows: KvpRow[]
    rowKey?: string
    hideDeleteForLastEmpty?: boolean
    valueTitleKey?: string
    selectorFilters?: Record<string, any>
    openByClick?: boolean
    disabled?: boolean
  }>(),
  {
    rowKey: 'ulid',
    hideDeleteForLastEmpty: false,
    openByClick: true,
    disabled: false,
  },
)

const emit = defineEmits<{
  'update:rows': [rows: KvpRow[]]
}>()

// ========== 使用 Composable ==========
const { rows: internalRows, removeRow, clearRow, updateRow, insertRows } = useKvpTable(
  computed(() => props.rows),
  {
    rowKey: props.rowKey,
    hideDeleteForLastEmpty: props.hideDeleteForLastEmpty,
    onUpdate: (rows) => emit('update:rows', rows),
  },
)

// ========== 选择器 ==========
const selectorRef = ref<InstanceType<typeof KvpFieldSelector>>()
const currentRowIndex = ref(-1)

const existingIds = computed(() => {
  const ids = new Set<string | number>()
  internalRows.value.forEach((row) => {
    if (row.bizFieldDomainId != null) {
      ids.add(row.bizFieldDomainId)
    }
  })
  return ids
})

// ========== 事件处理 ==========
const handleRowClick = (index: number) => {
  if (props.disabled) return
  currentRowIndex.value = index
  selectorRef.value?.open()
}

const handleCellChange = (index: number, field: keyof KvpRow, value: any) => {
  updateRow(index, { [field]: value })
}

const handleRemove = (index: number) => {
  removeRow(index)
}

const handleClear = (index: number) => {
  clearRow(index)
}

const handleSelect = (rows: any[]) => {
  // 构建新行数据
  const newRows: KvpRow[] = rows.map((row) => ({
    ulid: ulid(),
    key: row?.bizField?.name ?? row?.name ?? '',
    value: '',
    isRequired: false,
    bizFieldDomainId: row?.bizFieldDomain?.id ?? row?.id,
    __meta: {
      bizField: row?.bizField,
      bizFieldType: row?.bizFieldType,
      bizDomain: row?.bizDomain,
      name: row?.bizField?.name,
      description: row?.bizField?.description,
      basicFieldType: row?.bizFieldType?.basicFieldType,
      minimum: row?.bizFieldType?.minimum,
      maximum: row?.bizFieldType?.maximum,
    },
  }))

  // 插入到当前行
  const index = currentRowIndex.value >= 0 ? currentRowIndex.value : internalRows.value.length - 1
  insertRows(index, newRows)
}
</script>

<style lang="less" scoped>
.j-kvp-table {
  // 组件容器样式
}
</style>


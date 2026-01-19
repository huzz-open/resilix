<template>
  <t-table :data="rows" :columns="columns" :row-key="rowKey" bordered size="small" />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { PrimaryTableCol } from 'tdesign-vue-next'
import { Input, Switch as TSwitch, Link, Tooltip } from 'tdesign-vue-next'
import { h } from 'vue'
import { t } from '@/locales'
import type { KvpRow } from './types'
import JBizFieldDomainCard from '@/components/j-biz-field-domain-card/index.vue'

const props = withDefaults(
  defineProps<{
    rows: KvpRow[]
    rowKey?: string
    valueTitleKey?: string
    hideDeleteForLastEmpty?: boolean
    openByClick?: boolean
    disabled?: boolean
    onRowClick?: (index: number) => void
    onCellChange?: (index: number, field: keyof KvpRow, value: any) => void
    onRemove?: (index: number) => void
    onClear?: (index: number) => void
  }>(),
  {
    rowKey: 'ulid',
    hideDeleteForLastEmpty: false,
    openByClick: true,
    disabled: false,
  },
)

// ========== 表格列定义 ==========
const columns = computed<PrimaryTableCol[]>(() => [
  {
    title: t('pages.apiDefinition.drawer.key'),
    colKey: 'key',
    width: 260,
    cell: (_h: any, { row, rowIndex }: any) => {
      const input = h(Input as any, {
        size: 'small',
        readonly: true,
        disabled: props.disabled,
        placeholder: t('pages.apiDefinition.drawer.selectFieldPlaceholder'),
        value: row?.key ?? '',
        onClick: props.openByClick && !props.disabled ? () => props.onRowClick?.(rowIndex) : undefined,
      })

      // 如果有元信息，包裹 Tooltip
      if (row?.__meta) {
        const content = h(JBizFieldDomainCard as any, {
          entity: row.__meta,
          density: 'compact',
        })
        return h(
          Tooltip as any,
          { placement: 'top-left' },
          {
            default: () => input,
            content: () => content,
          },
        )
      }

      return input
    },
  },
  {
    title: t(props.valueTitleKey || 'pages.apiDefinition.drawer.value'),
    colKey: 'value',
    width: 320,
    cell: (_h: any, { row, rowIndex }: any) =>
      h(Input as any, {
        size: 'small',
        disabled: props.disabled,
        placeholder: t('pages.apiDefinition.drawer.valuePlaceholder'),
        value: row?.value,
        onChange: (v: string) => props.onCellChange?.(rowIndex, 'value', v),
      }),
  },
  {
    title: t('pages.apiDefinition.drawer.required'),
    colKey: 'isRequired',
    width: 120,
    cell: (_h: any, { row, rowIndex }: any) =>
      h(TSwitch as any, {
        size: 'small',
        disabled: props.disabled,
        value: Boolean(row?.isRequired),
        onChange: (v: boolean) => props.onCellChange?.(rowIndex, 'isRequired', v),
      }),
  },
  {
    title: t('pages.apiDefinition.list.columns.operation'),
    colKey: 'op',
    width: 120,
    cell: (_h: any, { rowIndex }: any) => {
      if (props.disabled) return null

      const isSingle = props.rows.length <= 1
      const isLastEmpty =
        props.hideDeleteForLastEmpty &&
        rowIndex === props.rows.length - 1 &&
        !props.rows[rowIndex].key &&
        !props.rows[rowIndex].value

      if (isLastEmpty) return null

      if (isSingle) {
        return h(
          Link as any,
          { theme: 'primary', onClick: () => props.onClear?.(rowIndex) },
          { default: () => t('pages.apiDefinition.drawer.clear') },
        )
      }

      return h(
        Link as any,
        { theme: 'danger', onClick: () => props.onRemove?.(rowIndex) },
        { default: () => t('pages.apiDefinition.drawer.remove') },
      )
    },
  },
])
</script>


/**
 * useKvpTable - 键值对表格逻辑
 */

import { ref, computed, watch, type Ref } from 'vue'
import { ulid } from 'ulid'
import type { KvpRow } from '../types'

export interface UseKvpTableOptions {
  /** 行主键 */
  rowKey?: string
  /** 是否隐藏最后一行空行的删除按钮 */
  hideDeleteForLastEmpty?: boolean
  /** 更新回调 */
  onUpdate?: (rows: KvpRow[]) => void
}

export function useKvpTable(rows: Ref<KvpRow[]>, options: UseKvpTableOptions = {}) {
  const { rowKey = 'ulid', hideDeleteForLastEmpty = false, onUpdate } = options

  // ========== 响应式状态 ==========
  const internalRows = ref<KvpRow[]>([...rows.value])

  // ========== 计算属性 ==========
  const isSingleRow = computed(() => internalRows.value.length <= 1)

  const lastRowIndex = computed(() => internalRows.value.length - 1)

  // ========== 行操作 ==========
  /**
   * 确保末尾有且仅有一个空行
   */
  const ensureTrailingEmptyRow = () => {
    const arr = internalRows.value
    const last = arr[arr.length - 1]
    const isLastEmpty =
      !last || (String(last.key || '').trim() === '' && String(last.value || '').trim() === '' && !last.isRequired)

    if (!isLastEmpty) {
      internalRows.value = [...arr, { ulid: ulid(), key: '', value: '', isRequired: false }]
      emitUpdate()
    }
  }

  /**
   * 判断是否为最后一个空行
   */
  const isLastEmptyRow = (index: number): boolean => {
    if (index !== lastRowIndex.value) return false
    const row = internalRows.value[index]
    return String(row.key || '').trim() === '' && String(row.value || '').trim() === '' && !row.isRequired
  }

  /**
   * 添加行
   */
  const addRow = (row: Partial<KvpRow> = {}) => {
    const newRow: KvpRow = {
      ulid: ulid(),
      key: '',
      value: '',
      isRequired: false,
      ...row,
    }
    internalRows.value = [...internalRows.value, newRow]
    ensureTrailingEmptyRow()
  }

  /**
   * 删除行
   */
  const removeRow = (index: number) => {
    internalRows.value = [...internalRows.value.slice(0, index), ...internalRows.value.slice(index + 1)]
    ensureTrailingEmptyRow()
  }

  /**
   * 清空行
   */
  const clearRow = (index: number) => {
    const row = internalRows.value[index]
    if (!row) return

    row.key = ''
    row.value = ''
    row.isRequired = false
    delete row.bizFieldDomainId
    delete row.__meta

    internalRows.value = [...internalRows.value]
    ensureTrailingEmptyRow()
  }

  /**
   * 更新行
   */
  const updateRow = (index: number, updates: Partial<KvpRow>) => {
    const row = internalRows.value[index]
    if (!row) return

    Object.assign(row, updates)
    internalRows.value = [...internalRows.value]
    ensureTrailingEmptyRow()
  }

  /**
   * 批量插入行（用于选择器确认）
   */
  const insertRows = (index: number, rows: KvpRow[]) => {
    if (index < 0 || rows.length === 0) return

    const arr = internalRows.value.slice()

    if (index < arr.length) {
      // 替换当前行并插入剩余行
      const first = rows[0]
      arr[index] = { ...arr[index], ...first, ulid: arr[index].ulid || first.ulid }
      if (rows.length > 1) {
        arr.splice(index + 1, 0, ...rows.slice(1))
      }
    } else {
      // 追加到末尾
      arr.push(...rows)
    }

    internalRows.value = arr
    ensureTrailingEmptyRow()
  }

  // ========== 同步 ==========
  const emitUpdate = () => {
    onUpdate?.(internalRows.value)
  }

  // 监听外部变化
  watch(
    () => rows.value,
    (newRows) => {
      internalRows.value = [...newRows]
      ensureTrailingEmptyRow()
    },
    { deep: true },
  )

  // 初始化
  ensureTrailingEmptyRow()

  return {
    // 状态
    rows: internalRows,
    isSingleRow,
    // 方法
    isLastEmptyRow,
    addRow,
    removeRow,
    clearRow,
    updateRow,
    insertRows,
    ensureTrailingEmptyRow,
  }
}


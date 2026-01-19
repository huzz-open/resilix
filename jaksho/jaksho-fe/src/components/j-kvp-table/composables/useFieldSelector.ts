/**
 * useFieldSelector - 字段选择器逻辑
 */

import { ref, reactive } from 'vue'
import type { PageInfo } from 'tdesign-vue-next'
import { request } from '@/utils/request'

export interface UseFieldSelectorOptions {
  /** 过滤条件 */
  filters?: Record<string, any>
  /** 选择回调 */
  onSelect?: (rows: any[]) => void
}

export function useFieldSelector(options: UseFieldSelectorOptions = {}) {
  const { filters = {}, onSelect } = options

  // ========== 响应式状态 ==========
  const visible = ref(false)
  const keyword = ref('')
  const loading = ref(false)
  const listData = ref<any[]>([])
  const selectedRowKeys = ref<Array<string | number>>([])

  const pagination = reactive({
    pageSize: 10,
    total: 0,
    current: 1,
  })

  // ========== 方法 ==========
  /**
   * 打开选择器
   */
  const open = () => {
    visible.value = true
    pagination.current = 1
    selectedRowKeys.value = []
    fetchData()
  }

  /**
   * 关闭选择器
   */
  const close = () => {
    visible.value = false
    keyword.value = ''
  }

  /**
   * 获取数据
   */
  const fetchData = async () => {
    loading.value = true
    try {
      const response = await request.post<{ rows: any[]; total: number }>({
        url: '/sr/biz-field-domain/page',
        data: {
          current: pagination.current,
          pageSize: pagination.pageSize,
          keyword: keyword.value,
          ...filters,
        },
      })
      listData.value = response.rows || []
      pagination.total = response.total || 0
    } catch (error) {
      console.error('[useFieldSelector] 数据加载失败:', error)
      listData.value = []
      pagination.total = 0
    } finally {
      loading.value = false
    }
  }

  /**
   * 搜索关键字变化
   */
  const handleKeywordChange = () => {
    pagination.current = 1
    fetchData()
  }

  /**
   * 分页变化
   */
  const handlePageChange = (pageInfo: PageInfo) => {
    pagination.current = pageInfo.current
    pagination.pageSize = pageInfo.pageSize
    fetchData()
  }

  /**
   * 选择变化
   */
  const handleSelectChange = (keys: (string | number)[]) => {
    selectedRowKeys.value = keys
  }

  /**
   * 确认选择
   */
  const confirm = () => {
    const selectedRows = listData.value.filter((row) => selectedRowKeys.value.includes(getRowId(row)))
    onSelect?.(selectedRows)
    close()
  }

  /**
   * 获取行 ID
   */
  const getRowId = (row: any): string | number => {
    return row?.bizFieldDomain?.id ?? row?.bizField?.id ?? row?.id
  }

  return {
    // 状态
    visible,
    keyword,
    loading,
    listData,
    selectedRowKeys,
    pagination,
    // 方法
    open,
    close,
    fetchData,
    handleKeywordChange,
    handlePageChange,
    handleSelectChange,
    confirm,
    getRowId,
  }
}


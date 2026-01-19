/**
 * useTableList - 表格列表通用逻辑
 * 处理分页、加载、搜索等常见列表操作
 */

import { ref, reactive, computed } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import type { PageParams, PageResult, PaginationConfig } from '@/types/common'
import type { UseTableListOptions, UseTableListReturn } from '@/types/composables'

/**
 * 表格列表 Composable
 * @param options 配置选项
 * @returns 表格列表相关状态和方法
 */
export function useTableList<T = any>(options: UseTableListOptions<T>): UseTableListReturn<T> {
  const {
    fetchApi,
    initialPagination = {},
    immediate = true,
    extraParams = {},
    onSuccess,
    onError,
  } = options

  // ========== 响应式状态 ==========
  const listData = ref<T[]>([]) as any
  const loading = ref(false)
  const error = ref<Error | null>(null)

  // 分页配置
  const pagination = reactive<PaginationConfig>({
    current: 1,
    pageSize: 10,
    total: 0,
    pageSizeOptions: [10, 20, 50, 100],
    showJumper: true,
    showTotal: true,
    ...initialPagination,
  })

  // 搜索参数
  const searchParams = ref<Record<string, any>>({})

  // ========== 计算属性 ==========
  const hasData = computed(() => listData.value.length > 0)
  const isEmpty = computed(() => !loading.value && listData.value.length === 0)

  // ========== 方法 ==========
  /**
   * 获取列表数据
   */
  const fetchData = async (params: Partial<PageParams> = {}) => {
    loading.value = true
    error.value = null

    try {
      const requestParams: PageParams = {
        current: pagination.current,
        pageSize: pagination.pageSize,
        ...extraParams,
        ...searchParams.value,
        ...params,
      }

      const result = await fetchApi(requestParams)

      listData.value = result.rows || []
      pagination.total = result.total || 0

      // 如果当前页没有数据且不是第一页，自动跳转到上一页
      if (listData.value.length === 0 && pagination.current > 1) {
        pagination.current -= 1
        await fetchData()
        return
      }

      onSuccess?.(result)
    } catch (err) {
      error.value = err as Error
      listData.value = []
      pagination.total = 0

      console.error('[useTableList] 数据加载失败:', err)
      onError?.(err as Error)

      await MessagePlugin.error('数据加载失败，请重试')
    } finally {
      loading.value = false
    }
  }

  /**
   * 刷新列表（保持当前页）
   */
  const refresh = async () => {
    await fetchData()
  }

  /**
   * 重置并刷新列表（回到第一页）
   */
  const reset = async () => {
    pagination.current = 1
    searchParams.value = {}
    await fetchData()
  }

  /**
   * 分页变化处理
   */
  const handlePageChange = (pageInfo: { current: number; pageSize: number }) => {
    pagination.current = pageInfo.current
    pagination.pageSize = pageInfo.pageSize
    fetchData()
  }

  /**
   * 搜索处理
   */
  const handleSearch = (params: Record<string, any>) => {
    searchParams.value = { ...params }
    pagination.current = 1 // 搜索时重置到第一页
    fetchData()
  }

  /**
   * 设置加载状态
   */
  const setLoading = (state: boolean) => {
    loading.value = state
  }

  // ========== 初始化 ==========
  if (immediate) {
    fetchData()
  }

  return {
    listData,
    pagination,
    loading,
    error,
    fetchData,
    refresh,
    reset,
    handlePageChange,
    handleSearch,
    setLoading,
  }
}


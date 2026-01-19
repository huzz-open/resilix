/**
 * Composables 相关类型定义
 * 包含各个 composable 函数的输入输出类型
 */

import type { Ref, ComputedRef } from 'vue'
import type { PageParams, PageResult, PaginationConfig, DialogConfig, DeleteConfirmConfig } from './common'

/**
 * useTableList 返回类型
 */
export interface UseTableListReturn<T = any> {
  /** 列表数据 */
  listData: Ref<T[]>
  /** 分页配置 */
  pagination: PaginationConfig
  /** 加载状态 */
  loading: Ref<boolean>
  /** 错误信息 */
  error: Ref<Error | null>
  /** 获取列表数据 */
  fetchData: (params?: Partial<PageParams>) => Promise<void>
  /** 刷新列表（保持当前页） */
  refresh: () => Promise<void>
  /** 重置并刷新列表（回到第一页） */
  reset: () => Promise<void>
  /** 分页变化处理 */
  handlePageChange: (pageInfo: { current: number; pageSize: number }) => void
  /** 搜索处理 */
  handleSearch: (searchParams: Record<string, any>) => void
  /** 设置加载状态 */
  setLoading: (state: boolean) => void
}

/**
 * useTableList 配置
 */
export interface UseTableListOptions<T = any> {
  /** 获取数据的 API 函数 */
  fetchApi: (params: PageParams) => Promise<PageResult<T>>
  /** 初始分页配置 */
  initialPagination?: Partial<PaginationConfig>
  /** 是否立即加载 */
  immediate?: boolean
  /** 额外的查询参数 */
  extraParams?: Record<string, any>
  /** 成功回调 */
  onSuccess?: (data: PageResult<T>) => void
  /** 失败回调 */
  onError?: (error: Error) => void
}

/**
 * useDialogForm 返回类型
 */
export interface UseDialogFormReturn<T = any> {
  /** 对话框配置 */
  dialogConfig: DialogConfig & { visible: Ref<boolean> }
  /** 表单数据 */
  formData: Ref<T>
  /** 表单 ref */
  formRef: Ref<any>
  /** 提交加载状态 */
  submitting: Ref<boolean>
  /** 打开对话框（新建模式） */
  openCreate: () => void
  /** 打开对话框（编辑模式） */
  openEdit: (data: T) => void
  /** 打开对话框（详情模式） */
  openDetail: (data: T) => void
  /** 关闭对话框 */
  close: () => void
  /** 提交表单 */
  submit: () => Promise<void>
  /** 重置表单 */
  reset: () => void
  /** 验证表单 */
  validate: () => Promise<boolean>
  /** 当前模式 */
  mode: Ref<'create' | 'edit' | 'detail'>
}

/**
 * useDialogForm 配置
 */
export interface UseDialogFormOptions<T = any> {
  /** 初始表单数据 */
  initialData?: T | (() => T)
  /** 对话框配置 */
  dialogConfig?: Partial<DialogConfig>
  /** 提交处理函数 */
  onSubmit?: (data: T, mode: 'create' | 'edit') => Promise<void>
  /** 关闭回调 */
  onClose?: () => void
  /** 打开回调 */
  onOpen?: (mode: 'create' | 'edit' | 'detail', data?: T) => void
  /** 验证函数 */
  validator?: (data: T) => Promise<boolean> | boolean
}

/**
 * useDeleteConfirm 返回类型
 */
export interface UseDeleteConfirmReturn {
  /** 显示删除确认对话框 */
  confirm: (config?: DeleteConfirmConfig) => Promise<boolean>
  /** 删除处理（带确认） */
  handleDelete: (item: any, deleteApi: (id: any) => Promise<void>, config?: DeleteConfirmConfig) => Promise<boolean>
  /** 批量删除处理（带确认） */
  handleBatchDelete: (
    items: any[],
    deleteApi: (ids: any[]) => Promise<void>,
    config?: DeleteConfirmConfig,
  ) => Promise<boolean>
}

/**
 * useDeleteConfirm 配置
 */
export interface UseDeleteConfirmOptions {
  /** 默认确认配置 */
  defaultConfig?: DeleteConfirmConfig
  /** 成功回调 */
  onSuccess?: (deletedItems?: any | any[]) => void
  /** 失败回调 */
  onError?: (error: Error) => void
}

/**
 * useKvpTable 返回类型
 */
export interface UseKvpTableReturn<T = any> {
  /** 内部行数据 */
  rows: Ref<T[]>
  /** 是否只有一行 */
  isSingleRow: ComputedRef<boolean>
  /** 最后一行索引 */
  lastRowIndex: ComputedRef<number>
  /** 判断是否为最后一个空行 */
  isLastEmptyRow: (index: number) => boolean
  /** 添加行 */
  addRow: (row?: Partial<T>) => void
  /** 删除行 */
  removeRow: (index: number) => void
  /** 清空行 */
  clearRow: (index: number) => void
  /** 更新行 */
  updateRow: (index: number, updates: Partial<T>) => void
  /** 批量插入行 */
  insertRows: (index: number, rows: T[]) => void
  /** 确保末尾空行 */
  ensureTrailingEmptyRow: () => void
}

/**
 * useKvpTable 配置
 */
export interface UseKvpTableOptions<T = any> {
  /** 行主键 */
  rowKey?: string
  /** 是否隐藏最后一行空行的删除按钮 */
  hideDeleteForLastEmpty?: boolean
  /** 更新回调 */
  onUpdate?: (rows: T[]) => void
  /** 判断空行的函数 */
  isEmptyRow?: (row: T) => boolean
  /** 创建空行的函数 */
  createEmptyRow?: () => T
}

/**
 * useFieldSelector 返回类型
 */
export interface UseFieldSelectorReturn {
  /** 是否显示选择器 */
  visible: Ref<boolean>
  /** 搜索关键字 */
  keyword: Ref<string>
  /** 加载状态 */
  loading: Ref<boolean>
  /** 列表数据 */
  listData: Ref<any[]>
  /** 已选行键值 */
  selectedRowKeys: Ref<Array<string | number>>
  /** 分页配置 */
  pagination: {
    pageSize: number
    total: number
    current: number
  }
  /** 打开选择器 */
  open: () => void
  /** 关闭选择器 */
  close: () => void
  /** 获取数据 */
  fetchData: () => Promise<void>
  /** 搜索关键字变化 */
  handleKeywordChange: () => void
  /** 分页变化 */
  handlePageChange: (pageInfo: { current: number; pageSize: number }) => void
  /** 选择变化 */
  handleSelectChange: (keys: (string | number)[]) => void
  /** 确认选择 */
  confirm: () => void
  /** 获取行 ID */
  getRowId: (row: any) => string | number
}

/**
 * useFieldSelector 配置
 */
export interface UseFieldSelectorOptions {
  /** 过滤条件 */
  filters?: Record<string, any>
  /** 选择回调 */
  onSelect?: (rows: any[]) => void
  /** 获取数据的 API URL */
  apiUrl?: string
  /** 自定义获取行 ID 的函数 */
  getRowId?: (row: any) => string | number
}

/**
 * useTreeData 返回类型
 */
export interface UseTreeDataReturn {
  /** 树数据 */
  treeData: Ref<any[]>
  /** 过滤文本 */
  filterText: Ref<string>
  /** 过滤函数 */
  filterFn: (node: any) => boolean
  /** 显示列配置 */
  displayColumns: ComputedRef<any[]>
  /** 是否可选中 */
  isCheckable: ComputedRef<boolean>
  /** 获取单元格值 */
  getCellValue: (data: any, col: any) => any
  /** 构建树 DTO 列表 */
  buildTreeDTOList: () => any[]
  /** 设置树 DTO 列表 */
  setTreeDTOList: (list: any[]) => void
  /** 插入节点 */
  insertNodes: (action: string, target: any, rows: any[]) => void
  /** 删除节点 */
  removeNode: (node: any) => void
  /** 处理过滤变化 */
  handleFilterChange: () => void
  /** 获取节点路径 */
  getNodePath: (node: any) => any[]
  /** 展开所有节点 */
  expandAll: () => void
  /** 折叠所有节点 */
  collapseAll: () => void
}

/**
 * useTreeData 配置
 */
export interface UseTreeDataOptions {
  /** 树 ref */
  treeRef: Ref<any>
  /** 获取分页数据的函数 */
  fetchPage: (params: { current: number; pageSize: number; keyword?: string }) => Promise<{ rows: any[]; total: number }>
  /** 列配置 */
  columns: any[]
  /** 显示列配置 */
  showColumns?: any[]
  /** 行主键 */
  rowKey?: string
  /** 选择模式 */
  selection?: 'single' | 'multiple'
  /** 自定义字段名 */
  customField: string
  /** 获取自定义值的函数 */
  getCustomValue: (row: any) => unknown
}

/**
 * useTreeSelector 返回类型
 */
export interface UseTreeSelectorReturn {
  /** 打开选择器 */
  openSelector: (action: string, target?: any) => void
  /** 当前操作 */
  currentAction: Ref<string>
  /** 当前目标值 */
  currentTargetValue: Ref<any>
  /** 已存在的值集合 */
  existingValues: ComputedRef<Set<any>>
  /** 处理选择器确认 */
  handleSelectorConfirm: (rows: any[]) => void
}

/**
 * useTreeSelector 配置
 */
export interface UseTreeSelectorOptions {
  /** 树 ref */
  treeRef: Ref<any>
  /** 自定义字段名 */
  customField: string
  /** 获取自定义值的函数 */
  getCustomValue: (row: any) => unknown
  /** 插入回调 */
  onInsert: (action: string, target: any, rows: any[]) => void
}

/**
 * useTreeDragDrop 返回类型
 */
export interface UseTreeDragDropReturn {
  /** 拖拽开始 */
  handleDragStart: (event: DragEvent, node: any) => void
  /** 拖拽结束 */
  handleDragEnd: (event: DragEvent) => void
  /** 拖拽进入 */
  handleDragEnter: (event: DragEvent, node: any) => void
  /** 拖拽离开 */
  handleDragLeave: (event: DragEvent) => void
  /** 拖拽悬停 */
  handleDragOver: (event: DragEvent) => void
  /** 放置 */
  handleDrop: (event: DragEvent, node: any) => void
  /** 当前拖拽节点 */
  dragNode: Ref<any>
  /** 拖拽目标节点 */
  dropNode: Ref<any>
}

/**
 * useTreeDragDrop 配置
 */
export interface UseTreeDragDropOptions {
  /** 树 ref */
  treeRef: Ref<any>
  /** 深度阈值 */
  depthThreshold?: number
  /** 拖拽前校验 */
  beforeDrag?: (node: any) => boolean
  /** 放置前校验 */
  beforeDrop?: (dragNode: any, dropNode: any, position: 'before' | 'after' | 'inner') => boolean
  /** 拖拽成功回调 */
  onDrop?: (dragNode: any, dropNode: any, position: 'before' | 'after' | 'inner') => void
}

/**
 * useSearch 返回类型
 */
export interface UseSearchReturn {
  /** 搜索表单数据 */
  searchForm: Ref<Record<string, any>>
  /** 是否折叠 */
  collapsed: Ref<boolean>
  /** 搜索 */
  handleSearch: () => void
  /** 重置 */
  handleReset: () => void
  /** 切换折叠状态 */
  toggleCollapsed: () => void
  /** 设置搜索值 */
  setSearchValue: (key: string, value: any) => void
  /** 获取搜索参数 */
  getSearchParams: () => Record<string, any>
}

/**
 * useSearch 配置
 */
export interface UseSearchOptions {
  /** 初始搜索值 */
  initialValues?: Record<string, any>
  /** 初始折叠状态 */
  initialCollapsed?: boolean
  /** 搜索回调 */
  onSearch?: (params: Record<string, any>) => void
  /** 重置回调 */
  onReset?: () => void
}

/**
 * useLoading 返回类型
 */
export interface UseLoadingReturn {
  /** 加载状态 */
  loading: Ref<boolean>
  /** 设置加载状态 */
  setLoading: (state: boolean) => void
  /** 包装异步函数（自动处理加载状态） */
  withLoading: <T>(fn: () => Promise<T>) => Promise<T>
}

/**
 * useRequest 返回类型
 */
export interface UseRequestReturn<T = any> {
  /** 响应数据 */
  data: Ref<T | null>
  /** 加载状态 */
  loading: Ref<boolean>
  /** 错误信息 */
  error: Ref<Error | null>
  /** 执行请求 */
  run: (...params: any[]) => Promise<T | undefined>
  /** 刷新（使用上次参数重新请求） */
  refresh: () => Promise<T | undefined>
  /** 取消请求 */
  cancel: () => void
}

/**
 * useRequest 配置
 */
export interface UseRequestOptions<T = any> {
  /** 是否立即执行 */
  immediate?: boolean
  /** 初始数据 */
  initialData?: T
  /** 成功回调 */
  onSuccess?: (data: T) => void
  /** 失败回调 */
  onError?: (error: Error) => void
  /** 完成回调（无论成功失败） */
  onFinally?: () => void
  /** 轮询间隔（毫秒） */
  pollingInterval?: number
  /** 防抖延迟（毫秒） */
  debounceWait?: number
  /** 节流延迟（毫秒） */
  throttleWait?: number
}


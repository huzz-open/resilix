/**
 * j-tree-data 组件类型定义
 */

import type { PrimaryTableCol } from 'tdesign-vue-next'

/**
 * 树节点数据
 */
export interface TreeNodeData {
  /** 节点值（唯一标识） */
  value: string
  /** 节点标签 */
  label?: string
  /** 子节点 */
  children?: TreeNodeData[]
  /** 附加数据 */
  data?: Record<string, any>
}

/**
 * 树 DTO 项
 */
export interface TreeDTOItem {
  /** 唯一标识 */
  ulid: string
  /** 父节点 ulid */
  parentUlid: string | null
  /** 排序顺序 */
  sortOrder: number
  /** 其他字段 */
  [key: string]: any
}

/**
 * 获取分页数据的函数类型
 */
export type FetchPageFn = (params: {
  current: number
  pageSize: number
  keyword?: string
}) => Promise<{ rows: any[]; total: number }>

/**
 * 组件 Props（基础版）
 */
export interface JTreeDataProps {
  /** 获取分页数据的函数 */
  fetchPage: FetchPageFn
  /** 表格列配置 */
  columns: PrimaryTableCol[]
  /** 树节点展示的列（默认与 columns 相同） */
  showColumns?: PrimaryTableCol[]
  /** 行主键 */
  rowKey?: string
  /** 选择模式 */
  selection?: 'single' | 'multiple'
  /** 自定义字段名 */
  customField: string
  /** 获取自定义值的函数 */
  getCustomValue: (row: any) => unknown
  /** 默认分页大小 */
  defaultPageSize?: number
  /** 分页选项 */
  pageSizeOptions?: number[]
  /** 树 DTO 列表（v-model） */
  treeDtoList?: TreeDTOItem[]
}

/**
 * 组件 Props（高级版）
 */
export interface JTreeDataAdvancedProps extends JTreeDataProps {
  /** 深度阈值（用于计算缩进） */
  treeDepthThreshold?: number
  /** 是否启用虚拟滚动 */
  virtualScroll?: boolean
}

/**
 * 插入操作类型
 */
export type InsertAction = 'appendRoot' | 'appendChild' | 'insertBefore' | 'insertAfter'


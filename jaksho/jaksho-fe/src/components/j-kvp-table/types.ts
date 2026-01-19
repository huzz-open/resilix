/**
 * j-kvp-table 组件类型定义
 */

/**
 * 键值对行数据
 */
export interface KvpRow {
  /** 唯一标识 */
  ulid: string
  /** 键 */
  key: string
  /** 值 */
  value: string
  /** 是否必填 */
  isRequired: boolean
  /** 业务字段领域 ID */
  bizFieldDomainId?: number | string
  /** 元数据 */
  __meta?: KvpRowMeta
  [key: string]: any
}

/**
 * 行元数据
 */
export interface KvpRowMeta {
  bizField?: Record<string, any>
  bizFieldType?: Record<string, any>
  bizDomain?: Record<string, any>
  name?: string
  description?: string
  basicFieldType?: string
  minimum?: number | null
  maximum?: number | null
  [key: string]: any
}

/**
 * 组件 Props
 */
export interface KvpTableProps {
  /** 行数据（v-model） */
  rows: KvpRow[]
  /** 行主键 */
  rowKey?: string
  /** 是否隐藏最后一行空行的删除按钮 */
  hideDeleteForLastEmpty?: boolean
  /** Value 列标题 key */
  valueTitleKey?: string
  /** 选择器过滤条件 */
  selectorFilters?: Record<string, any>
  /** 是否通过点击 Key 输入框打开选择器 */
  openByClick?: boolean
  /** 是否禁用（只读模式） */
  disabled?: boolean
}

/**
 * 字段选择器 Props
 */
export interface FieldSelectorProps {
  /** 是否显示 */
  visible: boolean
  /** 搜索关键字 */
  keyword?: string
  /** 分页配置 */
  pageSize?: number
  /** 选择器过滤条件 */
  filters?: Record<string, any>
  /** 已选择的字段 ID 集合（用于禁用） */
  existingIds?: Set<string | number>
}


/**
 * 通用类型定义
 * 包含整个应用中共享的基础类型
 */

/**
 * 分页参数
 */
export interface PageParams {
  /** 当前页码 */
  current: number
  /** 每页条数 */
  pageSize: number
  /** 搜索关键字 */
  keyword?: string
  /** 排序字段 */
  sortBy?: string
  /** 排序方向 */
  sortOrder?: 'asc' | 'desc' | 'ASC' | 'DESC'
  /** 其他查询参数 */
  [key: string]: any
}

/**
 * 分页结果
 */
export interface PageResult<T = any> {
  /** 数据列表 */
  rows: T[]
  /** 总条数 */
  total: number
  /** 当前页码 */
  current?: number
  /** 每页条数 */
  pageSize?: number
}

/**
 * API 响应结构
 */
export interface ApiResponse<T = any> {
  /** 响应码 */
  code: number | string
  /** 响应消息 */
  message?: string
  /** 响应数据 */
  data: T
  /** 是否成功 */
  success?: boolean
}

/**
 * 分页配置
 */
export interface PaginationConfig {
  /** 当前页码 */
  current: number
  /** 每页条数 */
  pageSize: number
  /** 总条数 */
  total: number
  /** 每页条数选项 */
  pageSizeOptions?: number[]
  /** 是否显示页码跳转 */
  showJumper?: boolean
  /** 是否显示总条数 */
  showTotal?: boolean
}

/**
 * 表格列配置（扩展 TDesign 的列定义）
 */
export interface TableColumn<T = any> {
  /** 列标题 */
  title: string
  /** 列键值 */
  colKey: string
  /** 列宽度 */
  width?: number | string
  /** 最小宽度 */
  minWidth?: number | string
  /** 是否固定列 */
  fixed?: 'left' | 'right'
  /** 是否显示省略号 */
  ellipsis?: boolean
  /** 排序配置 */
  sorter?: boolean | ((a: T, b: T) => number)
  /** 自定义渲染 */
  cell?: (h: any, context: { row: T; rowIndex: number; col: TableColumn<T>; colIndex: number }) => any
  /** 对齐方式 */
  align?: 'left' | 'center' | 'right'
}

/**
 * 对话框配置
 */
export interface DialogConfig {
  /** 是否显示 */
  visible: boolean
  /** 标题 */
  title?: string
  /** 宽度 */
  width?: string | number
  /** 是否显示确认按钮 */
  confirmBtn?: boolean | string
  /** 是否显示取消按钮 */
  cancelBtn?: boolean | string
  /** 确认按钮加载状态 */
  confirmLoading?: boolean
  /** 关闭时销毁子元素 */
  destroyOnClose?: boolean
  /** 是否可通过点击遮罩关闭 */
  closeOnOverlayClick?: boolean
}

/**
 * 表单项配置
 */
export interface FormItemConfig {
  /** 字段名 */
  name: string
  /** 标签 */
  label: string
  /** 控件类型 */
  type: 'input' | 'textarea' | 'select' | 'date' | 'number' | 'switch' | 'radio' | 'checkbox' | 'custom'
  /** 占位符 */
  placeholder?: string
  /** 默认值 */
  defaultValue?: any
  /** 是否必填 */
  required?: boolean
  /** 校验规则 */
  rules?: Array<{
    required?: boolean
    message?: string
    pattern?: RegExp
    validator?: (val: any) => boolean | Promise<boolean>
    trigger?: 'change' | 'blur'
  }>
  /** 选项（用于 select、radio、checkbox） */
  options?: Array<{ label: string; value: any; disabled?: boolean }>
  /** 是否禁用 */
  disabled?: boolean
  /** 提示信息 */
  tips?: string
  /** 栅格占位 */
  span?: number
  /** 自定义渲染（type 为 custom 时使用） */
  render?: (h: any, formData: any) => any
}

/**
 * 搜索配置
 */
export interface SearchConfig {
  /** 搜索字段配置 */
  fields: Array<{
    /** 字段名 */
    name: string
    /** 标签 */
    label: string
    /** 控件类型 */
    type: 'input' | 'select' | 'date' | 'daterange'
    /** 占位符 */
    placeholder?: string
    /** 选项（用于 select） */
    options?: Array<{ label: string; value: any }>
  }>
  /** 是否折叠 */
  collapsed?: boolean
  /** 是否显示重置按钮 */
  showReset?: boolean
}

/**
 * 操作按钮配置
 */
export interface ActionButton {
  /** 按钮文本 */
  label: string
  /** 按钮主题 */
  theme?: 'default' | 'primary' | 'success' | 'warning' | 'danger'
  /** 按钮变体 */
  variant?: 'base' | 'outline' | 'dashed' | 'text'
  /** 图标 */
  icon?: string
  /** 是否禁用 */
  disabled?: boolean | ((row?: any) => boolean)
  /** 是否显示 */
  visible?: boolean | ((row?: any) => boolean)
  /** 点击事件 */
  onClick: (row?: any) => void
  /** 确认配置（需要二次确认时使用） */
  confirm?: {
    title?: string
    content?: string
    theme?: 'warning' | 'danger' | 'info'
  }
}

/**
 * 业务领域实体（通用）
 */
export interface BizDomainEntity {
  id?: number | string
  name?: string
  description?: string
  remark?: string
  createTime?: string
  updateTime?: string
  [key: string]: any
}

/**
 * 业务字段实体（通用）
 */
export interface BizFieldEntity {
  id?: number | string
  name?: string
  description?: string
  remark?: string
  createTime?: string
  updateTime?: string
  [key: string]: any
}

/**
 * 业务字段类型实体（通用）
 */
export interface BizFieldTypeEntity {
  id?: number | string
  name?: string
  basicFieldType?: string
  collectionType?: string
  description?: string
  minimum?: number | null
  maximum?: number | null
  remark?: string
  createTime?: string
  updateTime?: string
  [key: string]: any
}

/**
 * 业务字段领域实体（组合）
 */
export interface BizFieldDomainEntity {
  id?: number | string
  bizDomain?: BizDomainEntity
  bizField?: BizFieldEntity
  bizFieldType?: BizFieldTypeEntity
  // 向后兼容的直接属性
  name?: string
  description?: string
  basicFieldType?: string
  minimum?: number | null
  maximum?: number | null
  [key: string]: any
}

/**
 * 删除确认配置
 */
export interface DeleteConfirmConfig {
  /** 标题 */
  title?: string
  /** 内容 */
  content?: string
  /** 主题 */
  theme?: 'warning' | 'danger' | 'info'
  /** 确认按钮文本 */
  confirmText?: string
  /** 取消按钮文本 */
  cancelText?: string
}

/**
 * 加载状态
 */
export interface LoadingState {
  /** 是否加载中 */
  loading: boolean
  /** 加载文本 */
  text?: string
}

/**
 * 错误状态
 */
export interface ErrorState {
  /** 是否有错误 */
  hasError: boolean
  /** 错误消息 */
  message?: string
  /** 错误代码 */
  code?: string | number
}

/**
 * 通用状态
 */
export interface CommonState<T = any> {
  /** 数据 */
  data: T | null
  /** 加载状态 */
  loading: boolean
  /** 错误状态 */
  error: ErrorState | null
}

/**
 * 树节点数据
 */
export interface TreeNode<T = any> {
  /** 节点值（唯一标识） */
  value: string | number
  /** 节点标签 */
  label: string
  /** 子节点 */
  children?: TreeNode<T>[]
  /** 是否禁用 */
  disabled?: boolean
  /** 是否选中 */
  checked?: boolean
  /** 是否展开 */
  expanded?: boolean
  /** 附加数据 */
  data?: T
  /** 父节点值 */
  parentValue?: string | number | null
  /** 排序顺序 */
  sortOrder?: number
}

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
  __meta?: {
    bizField?: BizFieldEntity
    bizFieldType?: BizFieldTypeEntity
    bizDomain?: BizDomainEntity
    name?: string
    description?: string
    basicFieldType?: string
    minimum?: number | null
    maximum?: number | null
    [key: string]: any
  }
  [key: string]: any
}

/**
 * 密度类型
 */
export type Density = 'compact' | 'comfortable' | 'spacious'

/**
 * 尺寸类型
 */
export type Size = 'small' | 'medium' | 'large'

/**
 * 主题类型
 */
export type Theme = 'light' | 'dark'

/**
 * 状态类型
 */
export type Status = 'success' | 'warning' | 'error' | 'info' | 'default'


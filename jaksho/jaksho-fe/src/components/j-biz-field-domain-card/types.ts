/**
 * j-biz-field-domain-card 组件类型定义
 */

import type { Density } from '@/types/common'

/**
 * 业务字段领域实体
 */
export interface BizFieldDomainEntity {
  // 业务领域
  bizDomain?: {
    name?: string
    description?: string
    [key: string]: any
  }
  // 字段类型
  bizFieldType?: {
    name?: string
    basicFieldType?: string
    description?: string
    minimum?: number | null
    maximum?: number | null
    collectionType?: string
    [key: string]: any
  }
  // 字段（兼容多种数据结构）
  bizField?: {
    name?: string
    description?: string
    [key: string]: any
  }
  // 直接属性（向后兼容）
  name?: string
  description?: string
  basicFieldType?: string
  [key: string]: any
}

/**
 * 组件 Props
 */
export interface JBizFieldDomainCardProps {
  /** 实体数据 */
  entity: BizFieldDomainEntity | null | undefined
  /** 密度模式 */
  density?: Density
  /** 是否显示领域标签 */
  showDomain?: boolean
  /** 是否显示取值区间 */
  showRange?: boolean
  /** 是否显示描述 */
  showDescription?: boolean
}


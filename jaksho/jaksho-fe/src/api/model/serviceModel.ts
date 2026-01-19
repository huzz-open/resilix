import type { PageParams } from '@/types/common'

export interface ServiceModel {
  id?: number
  serviceCode: number
  name: string
  description: string
  remark?: string
  createTime?: string
  updateTime?: string
  workspaceId?: number
}

export interface ServiceListParams extends PageParams {
  name?: string
  serviceCode?: number
}

export interface ServiceListResult {
  rows: ServiceModel[]
  total: number
}

export interface CreateServiceRequest {
  serviceCode: number
  name: string
  description?: string
  remark?: string
}

export interface UpdateServiceRequest extends CreateServiceRequest {
  id: number
}


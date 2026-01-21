export interface BizCodeModel {
  id?: number
  serviceId: number
  serviceName?: string
  serviceCode?: number
  code: number
  sequenceNumber: number
  httpStatus: number
  shortDesc: string
  detailDesc?: string
  i18nKey?: string
  remark?: string
  createTime?: string
  updateTime?: string
  workspaceId?: number
}

export interface BizCodeListParams {
  current: number
  pageSize: number
  serviceId?: number
  code?: number
  httpStatus?: number
  shortDesc?: string
}

export interface CreateBizCodeRequest {
  serviceId: number
  sequenceNumber?: number
  httpStatus: number
  shortDesc: string
  detailDesc?: string
  i18nKey?: string
  remark?: string
}

export interface UpdateBizCodeRequest {
  shortDesc: string
  detailDesc?: string
  remark?: string
}

export interface BizCodeConfigModel {
  serviceCodeLength: number
  sequenceLength: number
  sequenceMode: 'auto' | 'manual'
  i18nEnabled: boolean
  defaultLocale: string
}

export interface BindBizCodeRequest {
  apiId: number
  bizCodeIds: number[]
}


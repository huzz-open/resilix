import { request } from '@/utils/request'
import type { PageResult } from '@/types/common'
import type {
  ServiceModel,
  ServiceListParams,
  CreateServiceRequest,
} from './model/serviceModel'

/**
 * 获取服务列表
 */
export function getServiceList(params: ServiceListParams): Promise<PageResult<ServiceModel>> {
  return request.post({
    url: '/sr/service/page',
    data: params,
  })
}

/**
 * 创建服务
 */
export function createService(data: CreateServiceRequest): Promise<number> {
  return request.post({
    url: '/sr/service',
    data,
  })
}

/**
 * 删除服务
 */
export function deleteService(id: number): Promise<number> {
  return request.delete({
    url: `/sr/service/${id}`,
  })
}

/**
 * 获取服务详情
 */
export function getServiceDetail(id: number): Promise<ServiceModel> {
  return request.get({
    url: `/sr/service/${id}`,
  })
}

/**
 * 更新服务
 */
export function updateService(id: number, data: Partial<CreateServiceRequest>): Promise<number> {
  return request.put({
    url: `/sr/service/${id}`,
    data,
  })
}


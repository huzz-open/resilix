import { request } from '@/utils/request'
import type { PageResult } from '@/types/common'
import type {
  BizCodeModel,
  BizCodeListParams,
  CreateBizCodeRequest,
  UpdateBizCodeRequest,
  BizCodeConfigModel,
  BindBizCodeRequest,
} from './model/bizCodeModel'

/**
 * 获取业务码列表
 */
export function getBizCodeList(params: BizCodeListParams): Promise<PageResult<BizCodeModel>> {
  return request.post({
    url: '/sr/biz-code/page',
    data: params,
  })
}

/**
 * 创建业务码
 */
export function createBizCode(data: CreateBizCodeRequest): Promise<number> {
  return request.post({
    url: '/sr/biz-code',
    data,
  })
}

/**
 * 删除业务码
 */
export function deleteBizCode(id: number): Promise<number> {
  return request.delete({
    url: `/sr/biz-code/${id}`,
  })
}

/**
 * 获取业务码详情
 */
export function getBizCodeDetail(id: number): Promise<BizCodeModel> {
  return request.get({
    url: `/sr/biz-code/${id}`,
  })
}

/**
 * 更新业务码
 */
export function updateBizCode(id: number, data: UpdateBizCodeRequest): Promise<number> {
  return request.put({
    url: `/sr/biz-code/${id}`,
    data,
  })
}

/**
 * 获取指定服务的下一个可用序号
 */
export function getNextSequence(serviceId: number): Promise<number> {
  return request.get({
    url: `/sr/biz-code/next-sequence/${serviceId}`,
  })
}

/**
 * 获取业务码配置
 */
export function getBizCodeConfig(): Promise<BizCodeConfigModel> {
  return request.get({
    url: '/sr/biz-code/config',
  })
}

/**
 * 根据接口ID获取关联的业务码列表
 */
export function getBizCodesByApiId(apiId: number): Promise<BizCodeModel[]> {
  return request.get({
    url: `/sr/biz-code/by-api/${apiId}`,
  })
}

/**
 * 为接口绑定业务码
 */
export function bindBizCodeToApi(data: BindBizCodeRequest): Promise<number> {
  return request.post({
    url: '/sr/biz-code/bind-to-api',
    data,
  })
}

/**
 * 解除接口与业务码的绑定
 */
export function unbindBizCodeFromApi(apiId: number, bizCodeId: number): Promise<number> {
  return request.delete({
    url: `/sr/biz-code/unbind-from-api/${apiId}/${bizCodeId}`,
  })
}


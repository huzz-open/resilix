import type { BizDomainListResult, BizDomainModel, CreateBizDomainRequest } from '@/api/model/bizDomainModel';
import { request } from '@/utils/request';

const Api = {
  BizDomainList: '/sr/biz-domain/page',
  CreateBizDomain: '/sr/biz-domain',
  DeleteBizDomain: '/sr/biz-domain',
  GetBizDomainDetail: '/sr/biz-domain',
  UpdateBizDomain: '/sr/biz-domain',
};

export function getBizDomainList(params: { 
  current: number; 
  pageSize: number;
  bizFieldId?: number;
}) {
  return request.post<BizDomainListResult>({
    url: Api.BizDomainList,
    data: params,
  });
}

export function createBizDomain(data: CreateBizDomainRequest) {
  return request.post<number>({
    url: Api.CreateBizDomain,
    data,
  });
}

export function deleteBizDomain(id: number) {
  return request.delete<void>({
    url: `${Api.DeleteBizDomain}/${id}`,
  });
}

export function getBizDomainDetail(id: number) {
  return request.get<BizDomainModel>({
    url: `${Api.GetBizDomainDetail}/${id}`,
  });
}

export function updateBizDomain(id: number, data: CreateBizDomainRequest) {
  return request.put<number>({
    url: `${Api.UpdateBizDomain}/${id}`,
    data,
  });
}

import type { BizDomainListResult, CreateBizDomainRequest } from '@/api/model/bizDomainModel';
import { request } from '@/utils/request';

const Api = {
  BizDomainList: '/sr/biz-domain/page',
  CreateBizDomain: '/sr/biz-domain',
  DeleteBizDomain: '/sr/biz-domain',
};

export function getBizDomainList(params: { current: number; pageSize: number }) {
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
  return request.delete<number>({
    url: `${Api.DeleteBizDomain}/${id}`,
  });
}

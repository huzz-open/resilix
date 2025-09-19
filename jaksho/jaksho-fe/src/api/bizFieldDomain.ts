import type { CreateBizFieldDomainRequest } from '@/api/model/bizFieldDomainModel';
import { request } from '@/utils/request';

const Api = {
  CreateBizFieldDomain: '/sr/biz-field-domain',
  BizFieldDomainList: '/sr/biz-field-domain/page',
};

export function createBizFieldDomain(data: CreateBizFieldDomainRequest) {
  return request.post<number>({
    url: Api.CreateBizFieldDomain,
    data,
  });
}

export function getBizFieldDomainList(params: {
  current: number;
  pageSize: number;
  keyword?: string;
  bizFieldId?: number;
}) {
  return request.post<{ rows: any[]; total: number }>({
    url: Api.BizFieldDomainList,
    data: params,
  });
}

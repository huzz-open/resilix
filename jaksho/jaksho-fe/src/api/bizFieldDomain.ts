import type { CreateBizFieldDomainRequest } from '@/api/model/bizFieldDomainModel';
import { request } from '@/utils/request';

const Api = {
  CreateBizFieldDomain: '/sr/biz-field-domain',
};

export function createBizFieldDomain(data: CreateBizFieldDomainRequest) {
  return request.post<number>({
    url: Api.CreateBizFieldDomain,
    data,
  });
}



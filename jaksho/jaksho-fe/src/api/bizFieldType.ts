import type { BizFieldTypeListResult, CreateBizFieldTypeRequest } from '@/api/model/bizFieldTypeModel';
import { request } from '@/utils/request';

const Api = {
  BizFieldTypeList: '/sr/biz-field-type/page',
  CreateBizFieldType: '/sr/biz-field-type',
};

export function getBizFieldTypeList(params: { current: number; pageSize: number }) {
  return request.post<BizFieldTypeListResult>({
    url: Api.BizFieldTypeList,
    data: params,
  });
}

export function createBizFieldType(data: CreateBizFieldTypeRequest) {
  return request.post<number>({
    url: Api.CreateBizFieldType,
    data,
  });
}



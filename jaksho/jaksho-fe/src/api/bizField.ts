import type { BizFieldListResult, CreateBizFieldRequest } from '@/api/model/bizFieldModel';
import { request } from '@/utils/request';

const Api = {
  BizFieldList: '/sr/biz-field/page',
  CreateBizField: '/sr/biz-field',
  DeleteBizField: '/sr/biz-field',
  GetBizFieldDetail: '/sr/biz-field',
};

export function getBizFieldList(params: { current: number; pageSize: number }) {
  return request.post<BizFieldListResult>({
    url: Api.BizFieldList,
    data: params,
  });
}

export function createBizField(data: CreateBizFieldRequest) {
  return request.post<number>({
    url: Api.CreateBizField,
    data,
  });
}

export function deleteBizField(id: number) {
  return request.delete<number>({
    url: `${Api.DeleteBizField}/${id}`,
  });
}

export function getBizFieldDetail(id: number) {
  return request.get<any>({
    url: `${Api.GetBizFieldDetail}/${id}`,
  });
}

import type {
  BasicFieldType,
  BizFieldTypeDetailResponse,
  BizFieldTypeListResult,
  CollectionType,
  CreateBizFieldTypeRequest,
} from '@/api/model/bizFieldTypeModel';
import { request } from '@/utils/request';

const Api = {
  BizFieldTypeList: '/sr/biz-field-type/page',
  CreateBizFieldType: '/sr/biz-field-type',
  DeleteBizFieldType: '/sr/biz-field-type',
};

export function getBizFieldTypeList(params: {
  current: number;
  pageSize: number;
  basicFieldType?: BasicFieldType;
  collectionType?: CollectionType;
}) {
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

export function deleteBizFieldType(id: number) {
  return request.delete<number>({
    url: `${Api.DeleteBizFieldType}/${id}`,
  });
}

export function getBizFieldTypeDetail(id: number) {
  return request.get<BizFieldTypeDetailResponse>({
    url: `${Api.DeleteBizFieldType}/${id}`,
  });
}

export function updateBizFieldType(id: number, data: { name?: string; description?: string }) {
  return request.put<number>({
    url: `${Api.DeleteBizFieldType}/${id}`,
    data,
  });
}

export function updateBizFieldTypeObjectRefs(id: number, data: { objectBizFieldTypeRefDTOList: any[] }) {
  return request.put<number>({
    url: `${Api.DeleteBizFieldType}/${id}/object-refs`,
    data,
  });
}

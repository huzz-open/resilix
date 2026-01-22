import type {
  CreateValueDictRequest,
  UpdateValueDictRequest,
  ValueDictDetailResponse,
  ValueDictListResult,
  ValueDictType,
} from '@/api/model/valueDictModel';
import { request } from '@/utils/request';

const Api = {
  ValueDictList: '/sr/value-dict/page',
  CreateValueDict: '/sr/value-dict',
  DeleteValueDict: '/sr/value-dict',
  DetailValueDict: '/sr/value-dict',
  UpdateValueDict: '/sr/value-dict',
};

export function getValueDictList(params: {
  current: number;
  pageSize: number;
  type?: ValueDictType;
}) {
  return request.post<ValueDictListResult>({
    url: Api.ValueDictList,
    data: params,
  });
}

export function createValueDict(data: CreateValueDictRequest) {
  return request.post<number>({
    url: Api.CreateValueDict,
    data,
  });
}

export function getValueDictDetail(id: number) {
  return request.get<ValueDictDetailResponse>({
    url: `${Api.DetailValueDict}/${id}`,
  });
}

export function updateValueDict(id: number, data: UpdateValueDictRequest) {
  return request.put<number>({
    url: `${Api.UpdateValueDict}/${id}`,
    data,
  });
}

export function deleteValueDict(id: number) {
  return request.delete<number>({
    url: `${Api.DeleteValueDict}/${id}`,
  });
}



import type { ApiDefinitionListResult, CreateApiDefinitionRequest } from '@/api/model/apiDefinitionModel';
import { request } from '@/utils/request';

const Api = {
  Page: '/sr/api-definition/page',
  Base: '/sr/api-definition',
};

export function getApiDefinitionList(params: { current: number; pageSize: number }) {
  return request.post<ApiDefinitionListResult>({
    url: Api.Page,
    data: params,
  });
}

export function createApiDefinition(data: CreateApiDefinitionRequest) {
  return request.post<number>({
    url: Api.Base,
    data,
  });
}

export function deleteApiDefinition(id: number) {
  return request.delete<number>({
    url: `${Api.Base}/${id}`,
  });
}

export function getApiDefinitionDetail(id: number) {
  return request.get<any>({
    url: `${Api.Base}/${id}`,
  });
}

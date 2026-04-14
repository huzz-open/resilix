import type { TreeDTO } from '@/api/model/basic/treeDTO';

export type BodyType = 'NONE' | 'FORM_DATA' | 'FORM_URLENCODED' | 'RAW_JSON' | 'RAW_TEXT' | 'BINARY';

export type FieldType = 'QUERY' | 'PATH' | 'FORM_DATA' | 'FORM_URLENCODED' | 'RAW_JSON' | 'RAW_TEXT' | 'RESPONSE_OK';

export type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE' | 'HEAD' | 'TRACE' | 'OPTIONS' | 'PATCH';

export interface ApiDefinitionModel {
  id: number;
  name: string;
  method: HttpMethod;
  path: string;
  description?: string;
  bodyType: BodyType;
  remark?: string;
  workspaceId: number;
  createTime: string;
  updateTime: string;
}

export interface ApiDefinitionListResult {
  rows: Array<ApiDefinitionModel>;
  total: number;
}

export interface ApiDefinitionFieldDTO extends TreeDTO {
  fieldType: FieldType;
  bizFieldDomainId?: number | string;
  isRequired?: boolean;
  description?: string;
  slotMappings?: string | null;
}

export interface CreateApiDefinitionRequest {
  name: string;
  path: string;
  method: HttpMethod;
  bodyType: BodyType;
  description?: string;
  remark?: string;
  workspaceId?: number;
  apiDefinitionFieldDTOList?: ApiDefinitionFieldDTO[];
}

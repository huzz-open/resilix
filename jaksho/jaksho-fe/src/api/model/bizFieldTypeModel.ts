import type { TreeDTO } from '@/api/model/basic/treeDTO';
import type { ValueDictModel, ValueDictItemModel } from '@/api/model/valueDictModel';

export type CollectionType = 'NONE' | 'LIST' | 'SET' | 'ARRAY';

export type BasicFieldType =
  | 'BOOLEAN'
  | 'INT8'
  | 'INT16'
  | 'INT32'
  | 'INT64'
  | 'FLOAT'
  | 'DOUBLE'
  | 'STRING'
  | 'OBJECT'
  | 'FILE';

export interface BizFieldTypeModel {
  id: number;
  name: string;
  description?: string;
  minimum?: number;
  maximum?: number;
  collectionType: CollectionType;
  basicFieldType: BasicFieldType;
  valueDictId?: number;
  createTime: string;
  updateTime: string;
  workspaceId: number;
}

export interface BizFieldTypeListResult {
  rows: Array<BizFieldTypeModel>;
  total: number;
}

export interface CreateBizFieldTypeRequest {
  name: string;
  description?: string;
  minimum?: number;
  maximum?: number;
  collectionType: CollectionType;
  basicFieldType: BasicFieldType;
  valueDictId?: number;
  objectBizFieldTypeRefDTOList?: ObjectBizFieldTypeRefDTO[];
}

export interface ObjectBizFieldTypeRefDTO extends TreeDTO {
  bizFieldDomainId: number | string;
}

export interface BizFieldTypeDetailResponse extends BizFieldTypeModel {
  objectBizFieldTypeRefList?: any[];
  valueDict?: ValueDictModel;
  valueDictItems?: ValueDictItemModel[];
}

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
}



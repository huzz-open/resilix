import type { BizFieldTypeModel } from '@/api/model/bizFieldTypeModel';

export interface BizFieldModel {
  id: number;
  name: string;
  description?: string;
  bizFieldTypeId: number;
  createTime: string;
  updateTime: string;
  workspaceId: number;
  // 可选：服务端可能返回联表信息
  bizFieldType?: Partial<BizFieldTypeModel>;
}

export interface CombineBizFieldResult {
  bizField: BizFieldModel;
  bizFieldType: BizFieldTypeModel;
}

export interface BizFieldListResult {
  rows: Array<CombineBizFieldResult>;
  total: number;
}

export interface CreateBizFieldRequest {
  name: string;
  description?: string;
  bizFieldTypeId: number;
}

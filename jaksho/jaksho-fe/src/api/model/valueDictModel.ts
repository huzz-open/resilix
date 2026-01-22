export type ValueDictType = 'STR' | 'INT' | 'CHAR' | 'FLOAT';

export interface ValueDictModel {
  id: number;
  name: string; // 下划线命名：login_type
  description: string;
  type: ValueDictType; // STR/INT/CHAR/FLOAT
  remark?: string;
  workspaceId: number;
  createTime: string;
  updateTime: string;
}

export interface ValueDictItemModel {
  id?: number;
  valueDictId?: number;
  name: string; // 枚举项名称：PC_WEB
  rawValue: string; // 原始值："PC", "1"
  description: string;
  sortOrder: number;
  remark?: string;
  workspaceId?: number;
}

export interface CreateValueDictRequest {
  name: string;
  description: string;
  type: ValueDictType;
  remark?: string;
  items: ValueDictItemModel[];
}

export interface UpdateValueDictRequest {
  description?: string;
  remark?: string;
  items: ValueDictItemModel[];
}

export interface ValueDictDetailResponse extends ValueDictModel {
  items: ValueDictItemModel[];
}

export interface ValueDictListResult {
  rows?: ValueDictModel[]; // 后端返回的字段名
  records?: ValueDictModel[]; // 兼容字段名
  total: number;
}



export interface CreateBizFieldDomainRequest {
  bizFieldId: number;
  bizDomainId: number;
  bizFieldTypeId: number;
}

export interface BizFieldDomainModel {
  id: number;
  bizFieldId: number;
  bizDomainId: number;
  bizFieldTypeId: number;
  fieldAttributes?: number; // 字段属性（位标识）：1=输入 2=输出 3=输入输出均可
  createTime: string;
  updateTime: string;
  workspaceId: number;
}

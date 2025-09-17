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
  createTime: string;
  updateTime: string;
  workspaceId: number;
}

export interface BizDomainListResult {
  data: Array<BizDomainModel>;
  total: number;
}

export interface BizDomainModel {
  id: number;
  name: string;
  description: string;
  createTime: string;
  updateTime: string;
  workspaceId: number;
}

export interface CreateBizDomainRequest {
  name: string;
  description?: string;
}

# BizDomain 前端功能实现

## 概述

为 Jaksho 项目实现了 BizDomain（业务领域）的前端管理功能，包括列表查询和创建功能。

## 功能特性

### 1. 列表分页查询
- 支持分页查询业务领域列表
- 显示业务领域的基本信息：ID、名称、描述、创建时间、更新时间
- 支持多选操作
- 提供搜索功能（按业务领域名称搜索）
- 支持删除操作（带确认对话框）

### 2. 创建功能
- 提供表单创建新的业务领域
- 包含字段验证：
  - 业务领域名称：必填，长度1-100字符
  - 描述：可选，最大255字符
- 支持表单重置和取消操作
- 创建成功后自动跳转到列表页面

## 文件结构

```
jaksho-fe/src/
├── api/
│   ├── bizDomain.ts                    # BizDomain API 接口
│   └── model/
│       └── bizDomainModel.ts           # BizDomain 数据模型
├── pages/
│   └── biz-domain/
│       ├── list/
│       │   └── index.vue              # 列表页面
│       └── create/
│           └── index.vue              # 创建页面
├── router/
│   └── modules/
│       └── biz-domain.ts              # 路由配置
└── locales/
    └── lang/
        ├── zh_CN/pages/
        │   └── biz-domain.ts          # 中文国际化
        └── en_US/pages/
            └── biz-domain.ts          # 英文国际化
```

## API 接口

### 分页查询
- **URL**: `POST /sr/biz-domain/page`
- **参数**: `{ pageNum: number, pageSize: number }`
- **返回**: `{ list: BizDomainModel[], total: number }`

### 创建
- **URL**: `POST /sr/biz-domain`
- **参数**: `{ name: string, description?: string }`
- **返回**: `number` (新创建的ID)

## 路由配置

- 列表页面：`/biz-domain/list`
- 创建页面：`/biz-domain/create`

## 菜单配置

- 菜单名称：业务领域管理
- 图标：DatabaseIcon
- 子菜单：
  - 业务领域列表
  - 创建业务领域（隐藏菜单项）

## 技术实现

- 使用 TDesign Vue Next 组件库
- 支持中英文国际化
- 响应式设计
- TypeScript 类型安全
- 表单验证
- 错误处理

## 使用说明

1. 启动前端项目：`npm run dev`
2. 访问业务领域管理菜单
3. 在列表页面可以查看所有业务领域
4. 点击"创建业务领域"按钮进入创建页面
5. 填写表单信息并提交创建

## 注意事项

- 业务领域名称必须唯一（后端验证）
- 所有表单字段都有相应的验证规则
- 创建成功后会自动跳转到列表页面
- 删除操作需要确认，防止误操作

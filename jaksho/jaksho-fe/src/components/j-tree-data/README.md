# j-tree-data 组件

树形数据可视化编辑组件，支持拖拽排序、节点插入/删除、字段选择等功能。

## 功能特性

### 核心功能
- ✅ 树形数据展示
- ✅ 插入节点（根节点/子节点/前/后）
- ✅ 删除节点
- ✅ 字段选择器（弹窗 + 搜索 + 分页）
- ✅ 同层级去重（拖拽和插入）
- ✅ 过滤器（关键字搜索）
- ✅ 受控模式（v-model:treeDtoList）

### 高级功能
- ✅ 拖拽排序（带层级限制）
- ✅ 表头自动对齐
- ✅ 虚拟滚动（性能优化）
- ✅ 深度阈值计算

## 使用示例

```vue
<template>
  <j-tree-data
    v-model:tree-dto-list="treeData"
    :fetch-page="fetchBizFieldDomainPage"
    :columns="columns"
    row-key="bizFieldDomain.id"
    selection="multiple"
    custom-field="bizFieldDomainId"
    :get-custom-value="(row) => row.bizFieldDomain.id"
    :default-page-size="10"
  />
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { PrimaryTableCol } from 'tdesign-vue-next'
import { request } from '@/utils/request'

const treeData = ref([])

const columns: PrimaryTableCol[] = [
  { colKey: 'row-select', type: 'multiple' },
  { title: '名称', colKey: 'bizField.name', ellipsis: true },
  { title: '类型', colKey: 'bizFieldType.basicFieldType', ellipsis: true },
]

async function fetchBizFieldDomainPage(params: { current: number; pageSize: number; keyword?: string }) {
  return request.post<{ rows: any[]; total: number }>({
    url: '/sr/biz-field-domain/page',
    data: params,
  })
}
</script>
```

## Props

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| fetchPage | FetchPageFn | - | 获取分页数据的函数 |
| columns | PrimaryTableCol[] | - | 表格列配置 |
| showColumns | PrimaryTableCol[] | columns | 树节点展示的列 |
| rowKey | string | 'id' | 行主键 |
| selection | 'single' \| 'multiple' | - | 选择模式 |
| customField | string | - | 自定义字段名 |
| getCustomValue | (row: any) => unknown | - | 获取自定义值的函数 |
| defaultPageSize | number | 10 | 默认分页大小 |
| pageSizeOptions | number[] | [10, 20, 50] | 分页选项 |
| treeDtoList | TreeDTOItem[] | [] | 树 DTO 列表（v-model） |
| treeDepthThreshold | number | 3 | 深度阈值（高级版） |

## Events

| 事件名 | 参数 | 说明 |
|--------|------|------|
| update:treeDtoList | list: TreeDTOItem[] | 树数据更新 |

## 方法

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| getTreeDTOList | - | TreeDTOItem[] | 获取当前树 DTO 列表 |

## 类型定义

```typescript
interface TreeDTOItem {
  ulid: string
  parentUlid: string | null
  sortOrder: number
  [key: string]: any
}

type FetchPageFn = (params: {
  current: number
  pageSize: number
  keyword?: string
}) => Promise<{ rows: any[]; total: number }>
```

## 注意事项

1. **性能优化**：对于大量节点（> 50），建议使用虚拟滚动
2. **深度限制**：默认深度阈值为 3，超过此深度的节点拖拽可能受限
3. **数据结构**：树 DTO 列表必须包含 `ulid`、`parentUlid`、`sortOrder` 字段
4. **同层级去重**：同一层级不允许重复的字段（基于 customField）

## 未来优化方向

1. **拆分版本**：
   - 基础版（~300 行）：去除拖拽、虚拟滚动、自动对齐等高级功能
   - 高级版（当前版本）：保留所有功能

2. **代码优化**：
   - 提取 Composables（useTreeData、useTreeDragDrop、useTreeSelector）
   - 简化表头对齐逻辑
   - 增强类型安全

3. **功能增强**：
   - 支持节点编辑
   - 支持批量操作
   - 支持导入/导出


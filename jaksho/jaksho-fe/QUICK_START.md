# 快速开始指南

## 🚀 5 分钟上手新架构

### 1. 创建一个新的列表页（最简单）

```vue
<template>
  <base-list-page
    title="我的列表"
    :columns="columns"
    :fetch-api="getMyList"
    :delete-api="deleteMyItem"
    @create="handleCreate"
  />
</template>

<script setup lang="ts">
import { BaseListPage } from '@/components/base'
import { getMyList, deleteMyItem } from '@/api/myModule'

const columns = [
  { title: '名称', colKey: 'name', width: 200 },
  { title: '描述', colKey: 'description', ellipsis: true },
  { title: '操作', colKey: 'op', width: 150, fixed: 'right' }
]

const handleCreate = () => {
  console.log('打开创建对话框')
}
</script>
```

**就这么简单！** 你已经有了一个完整的列表页，包括：
- ✅ 搜索功能
- ✅ 分页
- ✅ 新建按钮
- ✅ 编辑/详情/删除操作
- ✅ 加载状态
- ✅ 错误处理

---

### 2. 添加搜索功能

```vue
<template>
  <base-list-page
    :columns="columns"
    :fetch-api="getMyList"
  >
    <!-- 自定义搜索字段 -->
    <template #search-fields="{ searchForm }">
      <t-form-item label="名称" name="name">
        <t-input v-model="searchForm.name" placeholder="请输入名称" />
      </t-form-item>
      <t-form-item label="状态" name="status">
        <t-select v-model="searchForm.status" :options="statusOptions" />
      </t-form-item>
    </template>
  </base-list-page>
</template>

<script setup lang="ts">
const statusOptions = [
  { label: '启用', value: 'active' },
  { label: '禁用', value: 'inactive' }
]
</script>
```

---

### 3. 添加创建/编辑对话框

```vue
<template>
  <base-list-page
    :columns="columns"
    :fetch-api="getMyList"
    @create="handleCreate"
    @edit="handleEdit"
  />

  <!-- 对话框 -->
  <t-dialog
    v-model:visible="dialogConfig.visible"
    :header="mode === 'create' ? '新建' : '编辑'"
    @confirm="submit"
  >
    <t-form ref="formRef" :data="formData">
      <t-form-item label="名称" name="name">
        <t-input v-model="formData.name" />
      </t-form-item>
    </t-form>
  </t-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useDialogForm } from '@/composables'
import { createMyItem, updateMyItem } from '@/api/myModule'

const mode = ref<'create' | 'edit'>('create')
const listPageRef = ref()

const { dialogConfig, formData, formRef, openCreate, openEdit, submit } = useDialogForm({
  initialData: { name: '', description: '' },
  onSubmit: async (data, mode) => {
    if (mode === 'create') {
      await createMyItem(data)
    } else {
      await updateMyItem(data)
    }
    listPageRef.value?.refresh()
  }
})

const handleCreate = () => {
  mode.value = 'create'
  openCreate()
}

const handleEdit = (row: any) => {
  mode.value = 'edit'
  openEdit(row)
}
</script>
```

---

### 4. 自定义操作列

```vue
<template>
  <base-list-page
    :columns="columns"
    :fetch-api="getMyList"
    :show-default-actions="false"
  >
    <!-- 完全自定义操作列 -->
    <template #op="{ row }">
      <t-space>
        <t-link theme="primary" @click="handleApprove(row)">审批</t-link>
        <t-link theme="warning" @click="handleReject(row)">拒绝</t-link>
        <t-link theme="danger" @click="handleDelete(row)">删除</t-link>
      </t-space>
    </template>
  </base-list-page>
</template>
```

---

## 📚 常用模式

### 模式 1：带搜索的列表页

```vue
<template>
  <base-list-page
    ref="listPageRef"
    :columns="columns"
    :fetch-api="fetchApi"
  >
    <template #search-fields="{ searchForm }">
      <t-form-item label="关键字" name="keyword">
        <t-input v-model="searchForm.keyword" />
      </t-form-item>
    </template>
  </base-list-page>
</template>
```

### 模式 2：带批量操作的列表页

```vue
<template>
  <base-list-page
    :columns="columns"
    :fetch-api="fetchApi"
    :batch-delete-api="batchDeleteApi"
    :show-batch-delete="true"
  >
    <template #toolbar-extra="{ selectedRows }">
      <t-button
        v-if="selectedRows.length > 0"
        @click="handleBatchApprove(selectedRows)"
      >
        批量审批
      </t-button>
    </template>
  </base-list-page>
</template>
```

### 模式 3：只读列表页（无操作）

```vue
<template>
  <base-list-page
    :columns="columns"
    :fetch-api="fetchApi"
    :show-toolbar="false"
    :show-default-actions="false"
  />
</template>
```

---

## 🎯 核心概念

### 1. Composables 是什么？

Composables 是可复用的逻辑函数，帮你管理状态和行为。

```typescript
// 不使用 Composable（传统方式）
const listData = ref([])
const loading = ref(false)
const pagination = reactive({ current: 1, pageSize: 10, total: 0 })

const fetchData = async () => {
  loading.value = true
  try {
    const result = await getMyList({ current: pagination.current, pageSize: pagination.pageSize })
    listData.value = result.rows
    pagination.total = result.total
  } finally {
    loading.value = false
  }
}

// 使用 Composable（新方式）
const { listData, loading, pagination, fetchData } = useTableList({
  fetchApi: getMyList
})
```

### 2. 插槽是什么？

插槽让你可以自定义组件的某些部分。

```vue
<!-- 使用默认内容 -->
<base-list-page :columns="columns" :fetch-api="fetchApi" />

<!-- 自定义搜索区域 -->
<base-list-page :columns="columns" :fetch-api="fetchApi">
  <template #search>
    <div>我的自定义搜索</div>
  </template>
</base-list-page>
```

---

## 🔗 相关文档

- [完整重构指南](./REFACTORING_GUIDE.md) - 详细的使用说明和最佳实践
- [重构总结报告](./REFACTORING_SUMMARY.md) - 重构成果和技术细节
- [j-tree-data 文档](./src/components/j-tree-data/README.md) - 树形组件使用指南

---

## ❓ 常见问题

### Q: 我的旧页面需要立即迁移吗？

A: 不需要。新旧代码可以共存。建议：
- 新页面：直接使用新架构
- 旧页面：有时间再逐步迁移

### Q: 如果 BaseListPage 不满足我的需求怎么办？

A: 三种方案：
1. 使用插槽自定义（推荐）
2. 直接使用 Composables，不用 BaseListPage
3. 提需求，我们会持续优化

### Q: 类型错误怎么办？

A: 检查：
1. 是否导入了正确的类型
2. 泛型参数是否正确
3. 查看 `src/types/*.d.ts` 中的类型定义

### Q: 性能会有影响吗？

A: 不会。新架构：
- 使用相同的底层组件（TDesign）
- 减少了重复渲染
- 优化了状态管理

---

## 💬 获取帮助

1. 查看 [REFACTORING_GUIDE.md](./REFACTORING_GUIDE.md)
2. 查看示例页面：`src/pages/biz-domain/list/index-new.vue`
3. 查看类型定义：`src/types/*.d.ts`

---

**开始使用新架构，让开发更高效！** 🎉


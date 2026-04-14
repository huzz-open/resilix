<template>
  <div class="base-list-page">
    <!-- 页面头部 -->
    <div v-if="$slots.header || title" class="page-header">
      <slot name="header">
        <div class="page-title">
          <h3>{{ title }}</h3>
          <p v-if="description" class="page-description">{{ description }}</p>
        </div>
      </slot>
    </div>

    <!-- 搜索区域 -->
    <t-card v-if="$slots.search || showSearch" class="search-card" :bordered="false">
      <slot name="search" :search-form="searchForm" :handle-search="handleSearch" :handle-reset="handleReset">
        <t-form :data="searchForm" layout="inline" @submit="handleSearch" @reset="handleReset">
          <slot name="search-fields" :search-form="searchForm" />
          <t-form-item>
            <t-space>
              <t-button theme="primary" type="submit" :loading="loading">
                <template #icon><search-icon /></template>
                搜索
              </t-button>
              <t-button theme="default" type="reset" variant="outline"> 重置 </t-button>
            </t-space>
          </t-form-item>
        </t-form>
      </slot>
    </t-card>

    <!-- 操作栏 -->
    <t-card v-if="$slots.toolbar || showToolbar" class="toolbar-card" :bordered="false">
      <slot name="toolbar" :selected-rows="selectedRows" :refresh="refresh">
        <t-space>
          <t-button v-if="showCreate" theme="primary" @click="handleCreate">
            <template #icon><add-icon /></template>
            新建
          </t-button>
          <t-button v-if="showBatchDelete && selectedRows.length > 0" theme="danger" variant="outline" @click="handleBatchDelete">
            <template #icon><delete-icon /></template>
            批量删除 ({{ selectedRows.length }})
          </t-button>
          <t-button theme="default" variant="outline" @click="refresh">
            <template #icon><refresh-icon /></template>
            刷新
          </t-button>
          <slot name="toolbar-extra" :selected-rows="selectedRows" />
        </t-space>
      </slot>
    </t-card>

    <!-- 表格 -->
    <t-card class="table-card" :bordered="false">
      <t-table
        v-model:selected-row-keys="selectedRowKeys"
        :data="listData"
        :columns="columns"
        :loading="loading"
        :pagination="pagination"
        :row-key="rowKey"
        :stripe="stripe"
        :bordered="bordered"
        :hover="hover"
        :size="size"
        :max-height="maxHeight"
        @page-change="handlePageChange"
        @select-change="handleSelectChange"
        @row-click="handleRowClick"
      >
        <!-- 透传所有插槽 -->
        <template v-for="(_, name) in $slots" #[name]="slotData">
          <slot :name="name" v-bind="slotData" />
        </template>

        <!-- 默认操作列 -->
        <template v-if="showDefaultActions && !$slots['op']" #op="{ row }">
          <t-space :size="0">
            <t-link v-if="showEdit" theme="primary" @click="handleEdit(row)"> 编辑 </t-link>
            <t-divider v-if="showEdit && showDetail" layout="vertical" />
            <t-link v-if="showDetail" theme="primary" @click="handleDetail(row)"> 详情 </t-link>
            <t-divider v-if="(showEdit || showDetail) && showDelete" layout="vertical" />
            <t-link v-if="showDelete" theme="danger" @click="handleDelete(row)"> 删除 </t-link>
            <slot name="actions-extra" :row="row" />
          </t-space>
        </template>
      </t-table>
    </t-card>
  </div>
</template>

<script setup lang="ts" generic="T extends Record<string, any> = any">
import { ref, computed, watch } from 'vue'
import { AddIcon, DeleteIcon, RefreshIcon, SearchIcon } from 'tdesign-icons-vue-next'
import type { PrimaryTableCol, PageInfo, RowEventContext, TableRowData } from 'tdesign-vue-next'
import { useTableList } from '@/composables/useTableList'
import { useDeleteConfirm } from '@/composables/useDeleteConfirm'
import type { PageParams, PageResult } from '@/types/common'

// ========== Props ==========
interface BaseListPageProps {
  /** 页面标题 */
  title?: string
  /** 页面描述 */
  description?: string
  /** 表格列配置 */
  columns: PrimaryTableCol[]
  /** 获取列表数据的 API */
  fetchApi: (params: PageParams) => Promise<PageResult<T>>
  /** 删除单项的 API */
  deleteApi?: (id: any) => Promise<void>
  /** 批量删除的 API */
  batchDeleteApi?: (ids: any[]) => Promise<void>
  /** 行主键 */
  rowKey?: string
  /** 是否显示搜索区域 */
  showSearch?: boolean
  /** 是否显示工具栏 */
  showToolbar?: boolean
  /** 是否显示新建按钮 */
  showCreate?: boolean
  /** 是否显示批量删除按钮 */
  showBatchDelete?: boolean
  /** 是否显示默认操作列 */
  showDefaultActions?: boolean
  /** 是否显示编辑按钮 */
  showEdit?: boolean
  /** 是否显示详情按钮 */
  showDetail?: boolean
  /** 是否显示删除按钮 */
  showDelete?: boolean
  /** 是否显示斑马纹 */
  stripe?: boolean
  /** 是否显示边框 */
  bordered?: boolean
  /** 是否显示悬停效果 */
  hover?: boolean
  /** 表格尺寸 */
  size?: 'small' | 'medium' | 'large'
  /** 表格最大高度 */
  maxHeight?: string | number
  /** 是否立即加载 */
  immediate?: boolean
  /** 额外的查询参数 */
  extraParams?: Record<string, any>
}

const props = withDefaults(defineProps<BaseListPageProps>(), {
  rowKey: 'id',
  showSearch: true,
  showToolbar: true,
  showCreate: true,
  showBatchDelete: true,
  showDefaultActions: true,
  showEdit: true,
  showDetail: true,
  showDelete: true,
  stripe: false,
  bordered: true,
  hover: true,
  size: 'medium',
  immediate: true,
})

// ========== Emits ==========
const emit = defineEmits<{
  create: []
  edit: [row: T]
  detail: [row: T]
  delete: [row: T]
  batchDelete: [rows: T[]]
  refresh: []
  search: [params: Record<string, any>]
  selectChange: [selectedRows: T[]]
  rowClick: [row: T]
}>()

// ========== 使用 Composables ==========
const { listData, pagination, loading, fetchData, refresh: refreshList, handlePageChange: onPageChange, handleSearch: onSearch } = useTableList<T>({
  fetchApi: props.fetchApi,
  immediate: props.immediate,
  extraParams: props.extraParams,
  onSuccess: () => {
    emit('refresh')
  },
})

const { handleDelete: confirmDelete, handleBatchDelete: confirmBatchDelete } = useDeleteConfirm({
  onSuccess: () => {
    refreshList()
  },
})

// ========== 响应式状态 ==========
const searchForm = ref<Record<string, any>>({})
const selectedRowKeys = ref<Array<string | number>>([])
const selectedRows = computed(() => {
  return listData.value.filter((row) => selectedRowKeys.value.includes(row[props.rowKey]))
})

// ========== 方法 ==========
/**
 * 刷新列表
 */
const refresh = async () => {
  await refreshList()
}

/**
 * 搜索
 */
const handleSearch = () => {
  onSearch(searchForm.value)
  emit('search', searchForm.value)
}

/**
 * 重置搜索
 */
const handleReset = () => {
  searchForm.value = {}
  onSearch({})
  emit('search', {})
}

/**
 * 分页变化
 */
const handlePageChange = (pageInfo: PageInfo) => {
  onPageChange(pageInfo)
}

/**
 * 选择变化
 */
const handleSelectChange = (value: Array<string | number>, context: { selectedRowData: TableRowData[] }) => {
  selectedRowKeys.value = value
  emit('selectChange', context.selectedRowData as T[])
}

/**
 * 行点击
 */
const handleRowClick = (context: RowEventContext<TableRowData>) => {
  emit('rowClick', context.row as T)
}

/**
 * 新建
 */
const handleCreate = () => {
  emit('create')
}

/**
 * 编辑
 */
const handleEdit = (row: T) => {
  emit('edit', row)
}

/**
 * 详情
 */
const handleDetail = (row: T) => {
  emit('detail', row)
}

/**
 * 删除
 */
const handleDelete = async (row: T) => {
  if (!props.deleteApi) {
    console.warn('[BaseListPage] 未提供 deleteApi')
    return
  }

  const success = await confirmDelete(row, props.deleteApi, {
    title: '确认删除',
    content: '删除后数据将无法恢复，确定要删除吗？',
  })

  if (success) {
    emit('delete', row)
  }
}

/**
 * 批量删除
 */
const handleBatchDelete = async () => {
  if (!props.batchDeleteApi) {
    console.warn('[BaseListPage] 未提供 batchDeleteApi')
    return
  }

  const success = await confirmBatchDelete(selectedRows.value, props.batchDeleteApi, {
    title: '确认批量删除',
    content: `确定要删除选中的 ${selectedRows.value.length} 项吗？`,
  })

  if (success) {
    selectedRowKeys.value = []
    emit('batchDelete', selectedRows.value)
  }
}

// ========== 监听 extraParams 变化 ==========
watch(
  () => props.extraParams,
  () => {
    refresh()
  },
  { deep: true },
)

// ========== 暴露方法 ==========
defineExpose({
  refresh,
  fetchData,
  listData,
  loading,
  selectedRows,
})
</script>

<style lang="less" scoped>
.base-list-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 100%;

  .page-header {
    .page-title {
      h3 {
        margin: 0;
        font-size: 20px;
        font-weight: 600;
        color: var(--td-text-color-primary);
      }

      .page-description {
        margin: 8px 0 0;
        font-size: 14px;
        color: var(--td-text-color-secondary);
      }
    }
  }

  .search-card,
  .toolbar-card,
  .table-card {
    :deep(.t-card__body) {
      padding: 16px;
    }
  }

  .search-card {
    :deep(.t-form) {
      .t-form-item {
        margin-bottom: 0;
      }
    }
  }

  .toolbar-card {
    :deep(.t-card__body) {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .table-card {
    flex: 1;
    overflow: hidden;

    :deep(.t-card__body) {
      height: 100%;
      display: flex;
      flex-direction: column;
    }

    :deep(.t-table) {
      flex: 1;
    }
  }
}
</style>


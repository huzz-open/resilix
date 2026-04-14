<template>
  <div>
    <t-card class="list-card-container" :bordered="false">
      <t-row justify="space-between">
        <div class="left-operation-container">
          <t-button @click="handleCreate">新建服务</t-button>
          <p v-if="!!selectedRowKeys.length" class="selected-count">
            已选 {{ selectedRowKeys.length }} 项
          </p>
        </div>
        <div class="search-input">
          <t-space>
            <t-input
              v-model="searchForm.name"
              placeholder="搜索服务名称"
              clearable
              style="width: 200px"
            >
              <template #suffix-icon>
                <search-icon size="16px" />
              </template>
            </t-input>
            <t-input-number
              v-model="searchForm.serviceCode"
              placeholder="搜索服务码"
              clearable
              theme="normal"
              style="width: 150px"
            />
            <t-button theme="primary" @click="fetchData">
              <template #icon><search-icon /></template>
              搜索
            </t-button>
          </t-space>
        </div>
      </t-row>
      <t-table
        :data="listData"
        :columns="COLUMNS"
        :row-key="rowKey"
        vertical-align="top"
        :hover="true"
        :pagination="pagination"
        :selected-row-keys="selectedRowKeys"
        :loading="dataLoading"
        :header-affixed-top="headerAffixedTop"
        @page-change="rehandlePageChange"
        @change="rehandleChange"
        @select-change="(value: (string | number)[]) => rehandleSelectChange(value)"
      >
        <template #serviceCode="{ row }">
          <t-tag theme="primary">{{ row.serviceCode }}</t-tag>
        </template>
        <template #op="slotProps">
          <t-space>
            <t-link theme="primary" @click="goToEdit(slotProps.row.id)">编辑</t-link>
            <t-link theme="danger" @click="handleClickDelete(slotProps)">删除</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <t-dialog
      v-model:visible="confirmVisible"
      header="确认删除"
      :body="confirmBody"
      :on-cancel="onCancelDeleteConfirm"
      @confirm="onConfirmDelete"
    />
  </div>
</template>

<script setup lang="ts">
import { SearchIcon } from 'tdesign-icons-vue-next'
import type { PageInfo, PrimaryTableCol } from 'tdesign-vue-next'
import { computed, onMounted, ref } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'
import { useRouter } from 'vue-router'

import { getServiceList, deleteService } from '@/api/service'
import type { ServiceModel } from '@/api/model/serviceModel'

const router = useRouter()

const COLUMNS: PrimaryTableCol[] = [
  { colKey: 'row-select', type: 'multiple', width: 50, fixed: 'left' },
  {
    title: '服务码',
    colKey: 'serviceCode',
    width: 150,
  },
  {
    title: '服务名称',
    colKey: 'name',
    width: 200,
  },
  {
    title: '服务描述',
    colKey: 'description',
    ellipsis: true,
  },
  {
    title: '备注',
    colKey: 'remark',
    width: 200,
    ellipsis: true,
  },
  {
    title: '创建时间',
    colKey: 'createTime',
    width: 180,
  },
  {
    align: 'left',
    fixed: 'right',
    width: 150,
    colKey: 'op',
    title: '操作',
  },
]

const listData = ref<ServiceModel[]>([])
const selectedRowKeys = ref<(string | number)[]>([])
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
})

const dataLoading = ref(false)
const rowKey = 'id'
const headerAffixedTop = ref({ offsetTop: 0, container: '.main-content' })

const searchForm = ref<{
  name: string
  serviceCode: number | undefined
}>({
  name: '',
  serviceCode: undefined,
})

// 获取数据
const fetchData = async () => {
  dataLoading.value = true
  try {
    const { rows, total } = await getServiceList({
      current: pagination.value.current,
      pageSize: pagination.value.pageSize,
      name: searchForm.value.name || undefined,
      serviceCode: searchForm.value.serviceCode || undefined,
    })
    listData.value = rows
    pagination.value.total = total
  } catch (error) {
    console.error('获取服务列表失败:', error)
    MessagePlugin.error('获取服务列表失败')
  } finally {
    dataLoading.value = false
  }
}

// 分页变化
const rehandlePageChange = (pageInfo: PageInfo) => {
  pagination.value.current = pageInfo.current
  pagination.value.pageSize = pageInfo.pageSize
  fetchData()
}

// 表格变化
const rehandleChange = () => {
  fetchData()
}

// 选择变化
const rehandleSelectChange = (value: (string | number)[]) => {
  selectedRowKeys.value = value
}

// 搜索值
const searchValue = ref('')

// 删除确认
const confirmVisible = ref(false)
const confirmBody = computed(() => {
  if (deleteIdx.value !== -1) {
    const item = listData.value[deleteIdx.value]
    return `确认删除服务「${item.name}（${item.serviceCode}）」吗？`
  }
  return ''
})

const deleteIdx = ref(-1)

const handleClickDelete = (slotProps: { row: ServiceModel; rowIndex: number }) => {
  deleteIdx.value = slotProps.rowIndex
  confirmVisible.value = true
}

const onConfirmDelete = async () => {
  if (deleteIdx.value !== -1) {
    const item = listData.value[deleteIdx.value]
    try {
      await deleteService(item.id!)
      MessagePlugin.success('删除成功')
      confirmVisible.value = false
      deleteIdx.value = -1
      fetchData()
    } catch (error) {
      console.error('删除失败:', error)
      MessagePlugin.error('删除失败')
    }
  }
}

const onCancelDeleteConfirm = () => {
  confirmVisible.value = false
  deleteIdx.value = -1
}

// 跳转到编辑页面
const goToEdit = (id?: number) => {
  if (id) {
    router.push({ name: 'ServiceEdit', params: { id } })
  } else {
    router.push({ name: 'ServiceCreate' })
  }
}

// 创建
const handleCreate = () => {
  goToEdit()
}

onMounted(() => {
  fetchData()
})
</script>

<style lang="less" scoped>
.list-card-container {
  padding: 24px;

  :deep(.t-card__body) {
    padding: 0;
  }
}

.left-operation-container {
  display: flex;
  align-items: center;
  margin-bottom: 24px;

  .selected-count {
    display: inline-block;
    margin-left: 16px;
    color: var(--td-text-color-secondary);
  }
}

.search-input {
  margin-bottom: 24px;
}
</style>


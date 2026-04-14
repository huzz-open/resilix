<template>
  <div>
    <t-card class="list-card-container" :bordered="false">
      <t-row justify="space-between">
        <div class="left-operation-container">
          <t-button @click="handleCreate">新建业务码</t-button>
          <p v-if="!!selectedRowKeys.length" class="selected-count">
            已选 {{ selectedRowKeys.length }} 项
          </p>
        </div>
        <div class="search-input">
          <t-space>
            <t-select
              v-model="searchForm.serviceId"
              placeholder="选择服务"
              clearable
              filterable
              style="width: 200px"
            >
              <t-option
                v-for="service in serviceList"
                :key="service.id"
                :value="service.id"
                :label="`${service.name} (${service.serviceCode})`"
              />
            </t-select>
            <t-input-number
              v-model="searchForm.code"
              placeholder="搜索业务码"
              clearable
              theme="normal"
              style="width: 180px"
            />
            <t-input-number
              v-model="searchForm.httpStatus"
              placeholder="HTTP状态码"
              clearable
              theme="normal"
              style="width: 150px"
            />
            <t-input
              v-model="searchForm.shortDesc"
              placeholder="搜索描述"
              clearable
              style="width: 180px"
            >
              <template #suffix-icon>
                <search-icon size="16px" />
              </template>
            </t-input>
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
        <template #op="slotProps">
          <t-space>
            <t-link theme="primary" @click="goToEdit(slotProps.row?.bizCode?.id ?? slotProps.row?.id)">编辑</t-link>
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

import { getBizCodeList, deleteBizCode } from '@/api/bizCode'
import type { BizCodeModel } from '@/api/model/bizCodeModel'
import { getServiceList } from '@/api/service'
import type { ServiceModel } from '@/api/model/serviceModel'

const router = useRouter()

const COLUMNS: PrimaryTableCol<any>[] = [
  { colKey: 'row-select', type: 'multiple', width: 50, fixed: 'left' },
  {
    title: '业务码',
    colKey: 'bizCode.code',
    width: 180,
    cell(h, { row }) {
      return row?.bizCode?.code ?? row?.code
    },
  },
  {
    title: '服务',
    width: 150,
    cell(h, { row }) {
      const serviceName = row?.service?.name || row?.serviceName
      const serviceCode = row?.service?.serviceCode || row?.serviceServiceCode
      return serviceName ? `${serviceName} (${serviceCode})` : '-'
    },
  },
  {
    title: '序号',
    colKey: 'bizCode.sequenceNumber',
    width: 100,
    cell(h, { row }) {
      return row?.bizCode?.sequenceNumber ?? row?.sequenceNumber
    },
  },
  {
    title: 'HTTP状态',
    width: 120,
    cell(h, { row }) {
      const status = row?.bizCode?.httpStatus ?? row?.httpStatus
      return h('t-tag', { theme: getHttpStatusTheme(status) }, { default: () => status })
    },
  },
  {
    title: '简短描述',
    colKey: 'bizCode.shortDesc',
    width: 200,
    ellipsis: true,
    cell(h, { row }) {
      return row?.bizCode?.shortDesc ?? row?.shortDesc
    },
  },
  {
    title: '详细描述',
    ellipsis: true,
    cell(h, { row }) {
      return row?.bizCode?.detailDesc ?? row?.detailDesc
    },
  },
  {
    title: '创建时间',
    colKey: 'bizCode.createTime',
    width: 180,
    cell(h, { row }) {
      return row?.bizCode?.createTime ?? row?.createTime
    },
  },
  {
    align: 'left',
    fixed: 'right',
    width: 150,
    colKey: 'op',
    title: '操作',
  },
]

const listData = ref<BizCodeModel[]>([])
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
  serviceId: number | undefined
  code: number | undefined
  httpStatus: number | undefined
  shortDesc: string
}>({
  serviceId: undefined,
  code: undefined,
  httpStatus: undefined,
  shortDesc: '',
})

// 服务列表
const serviceList = ref<ServiceModel[]>([])

// 获取HTTP状态码主题
const getHttpStatusTheme = (status: number) => {
  if (status >= 200 && status < 300) return 'success'
  if (status >= 400 && status < 500) return 'warning'
  if (status >= 500) return 'danger'
  return 'default'
}

// 获取服务列表
const fetchServiceList = async () => {
  try {
    const { rows } = await getServiceList({
      current: 1,
      pageSize: 1000,
    })
    serviceList.value = rows
  } catch (error) {
    console.error('获取服务列表失败:', error)
    MessagePlugin.error('获取服务列表失败')
  }
}


// 获取数据
const fetchData = async () => {
  dataLoading.value = true
  try {
    const { rows, total } = await getBizCodeList({
      current: pagination.value.current,
      pageSize: pagination.value.pageSize,
      serviceId: searchForm.value.serviceId || undefined,
      code: searchForm.value.code || undefined,
      httpStatus: searchForm.value.httpStatus || undefined,
      shortDesc: searchForm.value.shortDesc || undefined,
    })
    listData.value = rows.map((r) => ({ ...r, id: r.bizCode?.id ?? r.id }))
    pagination.value.total = total
  } catch (error) {
    console.error('获取业务码列表失败:', error)
    MessagePlugin.error('获取业务码列表失败')
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

// 删除确认
const confirmVisible = ref(false)
const confirmBody = computed(() => {
  if (deleteIdx.value !== -1) {
    const item = listData.value[deleteIdx.value]
    const code = item?.bizCode?.code ?? item?.code
    return `确认删除业务码「${code}」吗？`
  }
  return ''
})

const deleteIdx = ref(-1)

const handleClickDelete = (slotProps: { row: BizCodeModel; rowIndex: number }) => {
  deleteIdx.value = slotProps.rowIndex
  confirmVisible.value = true
}

const onConfirmDelete = async () => {
  if (deleteIdx.value !== -1) {
    const item = listData.value[deleteIdx.value]
    const id = item?.bizCode?.id ?? item?.id
    try {
      await deleteBizCode(id!)
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
    router.push({ name: 'BizCodeEdit', params: { id } })
  } else {
    router.push({ name: 'BizCodeCreate' })
  }
}

// 创建
const handleCreate = () => {
  goToEdit()
}

onMounted(() => {
  fetchServiceList()
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


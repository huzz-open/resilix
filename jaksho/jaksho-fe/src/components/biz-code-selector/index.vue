<template>
  <div class="biz-code-selector">
    <div class="header-container">
      <span class="header-title">已关联的业务码</span>
      <t-button size="small" theme="primary" @click="handleSelectBizCode">
        <template #icon><add-icon /></template>
        选择业务码
      </t-button>
    </div>

    <!-- 已关联的业务码列表 -->
    <div v-if="selectedBizCodes.length > 0" class="biz-code-list">
      <t-table
        :data="selectedBizCodes"
        :columns="COLUMNS"
        row-key="bizCode.id"
        size="small"
        :hover="true"
        bordered
      >
      <template #op="{ row }">
        <t-link theme="danger" @click="handleRemoveBizCode(row)">移除</t-link>
      </template>
      </t-table>
    </div>
    <div v-else class="empty-state">
      <t-empty description="暂无关联的业务码，点击上方按钮添加" />
    </div>

    <!-- 选择业务码对话框 -->
    <t-dialog
      v-model:visible="dialogVisible"
      header="选择业务码"
      width="1000px"
      :footer="false"
      destroy-on-close
    >
      <div class="search-container">
        <t-space>
          <t-select
            v-model="searchForm.serviceId"
            placeholder="选择服务"
            clearable
            filterable
            style="width: 200px"
            @change="fetchBizCodeList"
          >
            <t-option
              v-for="service in serviceList"
              :key="service.id"
              :value="service.id"
              :label="`${service.name} (${service.serviceCode})`"
            />
          </t-select>
          <t-input
            v-model="searchForm.shortDesc"
            placeholder="搜索描述"
            clearable
            style="width: 200px"
            @enter="fetchBizCodeList"
          >
            <template #suffix-icon>
              <search-icon size="16px" />
            </template>
          </t-input>
          <t-button theme="primary" @click="fetchBizCodeList">
            <template #icon><search-icon /></template>
            搜索
          </t-button>
        </t-space>
      </div>

      <t-table
        v-model:selected-row-keys="tempSelectedIds"
        select-on-row-click
        :data="bizCodeList"
        :columns="SELECTOR_COLUMNS"
        row-key="bizCode.id"
        :hover="true"
        :pagination="pagination"
        :loading="loading"
        bordered
        @page-change="handlePageChange"
      >
      </t-table>

      <div class="dialog-footer">
        <t-space>
          <t-button theme="default" @click="onCancelSelect">取消</t-button>
          <t-button theme="primary" @click="onConfirmSelect">确定</t-button>
        </t-space>
      </div>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { AddIcon, SearchIcon } from 'tdesign-icons-vue-next'
import type { PageInfo, PrimaryTableCol } from 'tdesign-vue-next'
import { onMounted, ref, watch } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

import {
  getBizCodeList,
  getBizCodesByApiId,
  bindBizCodeToApi,
} from '@/api/bizCode'
import type { BizCodeModel } from '@/api/model/bizCodeModel'
import { getServiceList } from '@/api/service'
import type { ServiceModel } from '@/api/model/serviceModel'

interface Props {
  apiId?: number
}

const props = defineProps<Props>()

const emit = defineEmits<{
  change: [bizCodes: BizCodeModel[]]
}>()

const COLUMNS: PrimaryTableCol<any>[] = [
  {
    title: '业务码',
    colKey: 'bizCode.code',
    width: 150,
    cell(h, { row }) {
      const code = row?.bizCode?.code ?? row?.code
      return h('t-tag', { theme: 'primary', variant: 'light' }, { default: () => code })
    },
  },
  {
    title: '服务',
    colKey: 'service.name',
    width: 120,
    cell(h, { row }) {
      const serviceName = row?.service?.name || row?.serviceName
      return serviceName || '-'
    },
  },
  {
    title: 'HTTP状态',
    colKey: 'bizCode.httpStatus',
    width: 100,
    cell(h, { row }) {
      const status = row?.bizCode?.httpStatus ?? row?.httpStatus
      return h('t-tag', { theme: getHttpStatusTheme(status) }, { default: () => status })
    },
  },
  {
    title: '简短描述',
    colKey: 'bizCode.shortDesc',
    ellipsis: true,
    cell(h, { row }) {
      return row?.bizCode?.shortDesc ?? row?.shortDesc
    },
  },
  {
    title: '操作',
    colKey: 'op',
    align: 'center',
    fixed: 'right',
    width: 100,
  },
]

const SELECTOR_COLUMNS: PrimaryTableCol<any>[] = [
  { colKey: 'row-select', type: 'multiple', width: 50 },
  {
    title: '业务码',
    colKey: 'bizCode.code',
    width: 150,
    cell(h, { row }) {
      const code = row?.bizCode?.code ?? row?.code
      return h('t-tag', { theme: 'primary', variant: 'light' }, { default: () => code })
    },
  },
  {
    title: '服务',
    colKey: 'service.name',
    width: 120,
    cell(h, { row }) {
      const serviceName = row?.service?.name || row?.serviceName
      return serviceName || '-'
    },
  },
  {
    title: '序号',
    colKey: 'bizCode.sequenceNumber',
    width: 80,
    cell(h, { row }) {
      return row?.bizCode?.sequenceNumber ?? row?.sequenceNumber
    },
  },
  {
    title: 'HTTP状态',
    colKey: 'bizCode.httpStatus',
    width: 100,
    cell(h, { row }) {
      const status = row?.bizCode?.httpStatus ?? row?.httpStatus
      return h('t-tag', { theme: getHttpStatusTheme(status) }, { default: () => status })
    },
  },
  {
    title: '简短描述',
    colKey: 'bizCode.shortDesc',
    ellipsis: true,
    cell(h, { row }) {
      return row?.bizCode?.shortDesc ?? row?.shortDesc
    },
  },
]

const selectedBizCodes = ref<BizCodeModel[]>([])
const dialogVisible = ref(false)
const loading = ref(false)
const bizCodeList = ref<BizCodeModel[]>([])
const serviceList = ref<ServiceModel[]>([])
const tempSelectedIds = ref<(string | number)[]>([])

const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
})

const searchForm = ref<{
  serviceId: number | undefined
  shortDesc: string
}>({
  serviceId: undefined,
  shortDesc: '',
})

const getHttpStatusTheme = (status: number) => {
  if (status >= 200 && status < 300) return 'success'
  if (status >= 400 && status < 500) return 'warning'
  if (status >= 500) return 'danger'
  return 'default'
}

// 加载已关联的业务码
const loadSelectedBizCodes = async () => {
  if (!props.apiId) {
    selectedBizCodes.value = []
    return
  }

  try {
    selectedBizCodes.value = await getBizCodesByApiId(props.apiId)
    emit('change', selectedBizCodes.value)
  } catch (error) {
    console.error('加载已关联的业务码失败:', error)
  }
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
  }
}

const fetchBizCodeList = async () => {
  loading.value = true
  try {
    const { rows, total } = await getBizCodeList({
      current: pagination.value.current,
      pageSize: pagination.value.pageSize,
      serviceId: searchForm.value.serviceId || undefined,
      shortDesc: searchForm.value.shortDesc || undefined,
    })
    bizCodeList.value = rows
    pagination.value.total = total
    console.log('业务码列表前2条:', rows.slice(0, 2))
  } catch (error) {
    console.error('获取业务码列表失败:', error)
    MessagePlugin.error('获取业务码列表失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (pageInfo: PageInfo) => {
  pagination.value.current = pageInfo.current
  pagination.value.pageSize = pageInfo.pageSize
  fetchBizCodeList()
}

const handleSelectBizCode = () => {
  dialogVisible.value = true
  tempSelectedIds.value = selectedBizCodes.value
    .map((bc) => bc?.bizCode?.id ?? bc?.id)
    .filter((id): id is number => id != null)
  console.log('初始已选中的业务码 IDs:', tempSelectedIds.value)
  fetchServiceList()
  fetchBizCodeList()
}

// 确认选择
const onConfirmSelect = async () => {
  if (!props.apiId) {
    MessagePlugin.warning('接口ID不存在，无法绑定业务码')
    return
  }

  console.log('tempSelectedIds:', tempSelectedIds.value)

  const validBizCodeIds = tempSelectedIds.value.filter((id): id is number => typeof id === 'number' && !isNaN(id))
  
  console.log('validBizCodeIds:', validBizCodeIds)
  
  if (validBizCodeIds.length === 0) {
    MessagePlugin.warning('请选择至少一个业务码')
    return
  }

  try {
    await bindBizCodeToApi({
      apiId: props.apiId,
      bizCodeIds: validBizCodeIds,
    })
    MessagePlugin.success('绑定成功')
    dialogVisible.value = false
    await loadSelectedBizCodes()
  } catch (error: any) {
    console.error('绑定失败:', error)
    MessagePlugin.error(error.message || '绑定失败')
  }
}

// 取消选择
const onCancelSelect = () => {
  dialogVisible.value = false
}

// 移除业务码
const handleRemoveBizCode = async (row: any) => {
  if (!props.apiId) {
    MessagePlugin.warning('接口ID不存在')
    return
  }

  const rowId = row?.bizCode?.id ?? row?.id

  try {
    const newSelectedIds = selectedBizCodes.value
      .filter((bc) => {
        const bcId = (bc as any)?.bizCode?.id ?? (bc as any)?.id
        return bcId !== rowId
      })
      .map((bc) => {
        const bcId = (bc as any)?.bizCode?.id ?? (bc as any)?.id
        return bcId!
      })

    await bindBizCodeToApi({
      apiId: props.apiId,
      bizCodeIds: newSelectedIds,
    })

    MessagePlugin.success('移除成功')
    await loadSelectedBizCodes()
  } catch (error: any) {
    console.error('移除失败:', error)
    MessagePlugin.error(error.message || '移除失败')
  }
}

// 监听 apiId 变化
watch(
  () => props.apiId,
  () => {
    loadSelectedBizCodes()
  },
  { immediate: true },
)

onMounted(() => {
  loadSelectedBizCodes()
})

// 暴露方法供父组件调用
defineExpose({
  reload: loadSelectedBizCodes,
})
</script>

<style lang="less" scoped>
.biz-code-selector {
  .header-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--td-border-level-1-color);
  }

  .header-title {
    font-size: 14px;
    font-weight: 500;
    color: var(--td-text-color-primary);
  }

  .biz-code-list {
    margin-top: 16px;
  }

  .empty-state {
    margin-top: 16px;
    padding: 32px 0;
  }

  .search-container {
    margin-bottom: 16px;
    padding: 16px;
    background: var(--td-bg-color-container);
    border-radius: var(--td-radius-default);
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    margin-top: 24px;
    padding-top: 16px;
    border-top: 1px solid var(--td-border-level-1-color);
  }
}
</style>


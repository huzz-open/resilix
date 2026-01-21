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
            <t-link theme="primary" @click="handleClickEdit(slotProps)">编辑</t-link>
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

    <!-- 创建/编辑业务码对话框 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogMode === 'create' ? '新建业务码' : '编辑业务码'"
      width="700px"
      :footer="false"
    >
      <t-form
        ref="formRef"
        :data="formData"
        :rules="formRules"
        label-align="top"
        label-width="120"
        @submit="onSubmit"
      >
        <t-form-item label="服务" name="serviceId">
          <t-select
            v-model="formData.serviceId"
            placeholder="请选择服务"
            filterable
            :disabled="dialogMode === 'edit'"
            @change="onServiceChange"
          >
            <t-option
              v-for="service in serviceList"
              :key="service.id"
              :value="service.id"
              :label="`${service.name} (${service.serviceCode})`"
            />
          </t-select>
          <template v-if="dialogMode === 'edit'" #tips>
            <span>服务不可编辑</span>
          </template>
        </t-form-item>

        <t-form-item label="序号" name="sequenceNumber">
          <t-input-number
            v-model="formData.sequenceNumber"
            :min="1"
            placeholder="请输入序号"
            theme="normal"
            :disabled="dialogMode === 'edit' || config.sequenceMode === 'auto'"
          />
          <template #tips>
            <span v-if="dialogMode === 'edit'">序号不可编辑</span>
            <span v-else-if="config.sequenceMode === 'auto'">
              自动生成模式，当前序号：{{ formData.sequenceNumber }}
            </span>
            <span v-else>
              手动输入模式，序号位数：{{ config.sequenceLength }}
            </span>
          </template>
        </t-form-item>

        <t-form-item label="HTTP状态码" name="httpStatus">
          <t-input-number
            v-model="formData.httpStatus"
            :min="100"
            :max="599"
            placeholder="请输入HTTP状态码"
            theme="normal"
            :disabled="dialogMode === 'edit'"
          />
          <template v-if="dialogMode === 'edit'" #tips>
            <span>HTTP状态码不可编辑</span>
          </template>
        </t-form-item>

        <t-form-item label="简短描述" name="shortDesc">
          <t-input
            v-model="formData.shortDesc"
            placeholder="请输入简短描述"
            :maxlength="100"
            show-word-limit
          />
        </t-form-item>

        <t-form-item label="详细描述" name="detailDesc">
          <t-textarea
            v-model="formData.detailDesc"
            :height="120"
            placeholder="请输入详细描述"
            :maxlength="500"
            show-word-limit
          />
        </t-form-item>

        <t-form-item v-if="config.i18nEnabled" label="国际化Key" name="i18nKey">
          <t-input
            v-model="formData.i18nKey"
            placeholder="如：biz.code.user.not.found"
            :maxlength="100"
            :disabled="dialogMode === 'edit'"
          />
          <template v-if="dialogMode === 'edit'" #tips>
            <span>国际化Key不可编辑</span>
          </template>
        </t-form-item>

        <t-form-item label="备注" name="remark">
          <t-textarea
            v-model="formData.remark"
            :height="80"
            placeholder="请输入备注"
            :maxlength="255"
            show-word-limit
          />
        </t-form-item>

        <t-form-item v-if="dialogMode === 'create' && formData.serviceId && formData.sequenceNumber">
          <t-alert theme="info" message="业务码预览">
            <template #default>
              <div style="margin-top: 8px; font-size: 16px; font-weight: bold; color: #0052d9">
                {{ calculateBizCode() }}
              </div>
            </template>
          </t-alert>
        </t-form-item>

        <div class="dialog-footer">
          <t-space>
            <t-button theme="default" @click="onCancel">取消</t-button>
            <t-button theme="primary" type="submit" :loading="submitLoading">提交</t-button>
          </t-space>
        </div>
      </t-form>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { SearchIcon } from 'tdesign-icons-vue-next'
import type { FormInstanceFunctions, PageInfo, PrimaryTableCol } from 'tdesign-vue-next'
import { computed, onMounted, ref } from 'vue'
import { MessagePlugin } from 'tdesign-vue-next'

import {
  getBizCodeList,
  createBizCode,
  deleteBizCode,
  getBizCodeDetail,
  updateBizCode,
  getNextSequence,
  getBizCodeConfig,
} from '@/api/bizCode'
import type {
  BizCodeModel,
  BizCodeConfigModel,
} from '@/api/model/bizCodeModel'
import { getServiceList } from '@/api/service'
import type { ServiceModel } from '@/api/model/serviceModel'

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
const rowKey = (row: any) => row?.bizCode?.id ?? row?.id
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

// 业务码配置
const config = ref<BizCodeConfigModel>({
  serviceCodeLength: 4,
  sequenceLength: 8,
  sequenceMode: 'auto',
  i18nEnabled: true,
  defaultLocale: 'zh_CN',
})

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

// 获取配置
const fetchConfig = async () => {
  try {
    config.value = await getBizCodeConfig()
  } catch (error) {
    console.error('获取配置失败:', error)
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
    listData.value = rows
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

// 创建/编辑对话框
const dialogVisible = ref(false)
const dialogMode = ref<'create' | 'edit'>('create')
const editingId = ref<number | undefined>(undefined)
const formRef = ref<FormInstanceFunctions>()
const formData = ref<{
  serviceId: number | undefined
  sequenceNumber: number | undefined
  httpStatus: number | undefined
  shortDesc: string
  detailDesc: string
  i18nKey: string
  remark: string
}>({
  serviceId: undefined,
  sequenceNumber: undefined,
  httpStatus: undefined,
  shortDesc: '',
  detailDesc: '',
  i18nKey: '',
  remark: '',
})
const submitLoading = ref(false)

const formRules = computed(() => ({
  serviceId: dialogMode.value === 'create' ? [
    { required: true, message: '请选择服务', type: 'error' },
  ] : [],
  sequenceNumber: dialogMode.value === 'create' && config.value.sequenceMode === 'manual' ? [
    { required: true, message: '请输入序号', type: 'error' },
  ] : [],
  httpStatus: dialogMode.value === 'create' ? [
    { required: true, message: '请输入HTTP状态码', type: 'error' },
    { min: 100, max: 599, message: 'HTTP状态码范围：100-599', type: 'error' },
  ] : [],
  shortDesc: [
    { required: true, message: '请输入简短描述', type: 'error' },
    { max: 100, message: '简短描述最多100个字符', type: 'warning' },
  ],
  detailDesc: [{ max: 500, message: '详细描述最多500个字符', type: 'warning' }],
  i18nKey: [{ max: 100, message: '国际化Key最多100个字符', type: 'warning' }],
  remark: [{ max: 255, message: '备注最多255个字符', type: 'warning' }],
}))

const handleCreate = () => {
  dialogMode.value = 'create'
  dialogVisible.value = true
  formData.value = {
    serviceId: undefined,
    sequenceNumber: undefined,
    httpStatus: undefined,
    shortDesc: '',
    detailDesc: '',
    i18nKey: '',
    remark: '',
  }
  formRef.value?.reset()
}

const handleClickEdit = async (slotProps: { row: any }) => {
  const rowId = slotProps.row?.bizCode?.id ?? slotProps.row?.id
  dialogMode.value = 'edit'
  editingId.value = rowId

  try {
    const detail = await getBizCodeDetail(rowId!)
    // CombineResult 结构：{ bizCode: {...}, service: {...} }
    const bizCodeData = (detail as any)?.bizCode || detail
    formData.value = {
      serviceId: bizCodeData.serviceId,
      sequenceNumber: bizCodeData.sequenceNumber,
      httpStatus: bizCodeData.httpStatus,
      shortDesc: bizCodeData.shortDesc,
      detailDesc: bizCodeData.detailDesc || '',
      i18nKey: bizCodeData.i18nKey || '',
      remark: bizCodeData.remark || '',
    }
    dialogVisible.value = true
  } catch (error) {
    console.error('获取业务码详情失败:', error)
    MessagePlugin.error('获取业务码详情失败')
  }
}

const onServiceChange = async (serviceId: number) => {
  if (config.value.sequenceMode === 'auto' && serviceId) {
    try {
      const nextSeq = await getNextSequence(serviceId)
      formData.value.sequenceNumber = nextSeq
    } catch (error) {
      console.error('获取下一个序号失败:', error)
      MessagePlugin.error('获取下一个序号失败')
    }
  }
}

const calculateBizCode = (): string => {
  if (!formData.value.serviceId || !formData.value.sequenceNumber) {
    return '-'
  }
  const service = serviceList.value.find((s) => s.id === formData.value.serviceId)
  if (!service) {
    return '-'
  }
  const multiplier = Math.pow(10, config.value.sequenceLength)
  const code = service.serviceCode * multiplier + formData.value.sequenceNumber
  return code.toString()
}

const onSubmit = async ({ validateResult }: { validateResult: boolean }) => {
  if (!validateResult) return

  submitLoading.value = true
  try {
    if (dialogMode.value === 'create') {
      await createBizCode({
        serviceId: formData.value.serviceId!,
        sequenceNumber: config.value.sequenceMode === 'manual' ? formData.value.sequenceNumber : undefined,
        httpStatus: formData.value.httpStatus!,
        shortDesc: formData.value.shortDesc,
        detailDesc: formData.value.detailDesc,
        i18nKey: formData.value.i18nKey,
        remark: formData.value.remark,
      })
      MessagePlugin.success('创建成功')
    } else {
      await updateBizCode(editingId.value!, {
        shortDesc: formData.value.shortDesc,
        detailDesc: formData.value.detailDesc,
        remark: formData.value.remark,
      })
      MessagePlugin.success('更新成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error: any) {
    console.error('操作失败:', error)
    MessagePlugin.error(error.message || (dialogMode.value === 'create' ? '创建失败' : '更新失败'))
  } finally {
    submitLoading.value = false
  }
}

const onCancel = () => {
  dialogVisible.value = false
}

onMounted(() => {
  fetchConfig()
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
}
</style>


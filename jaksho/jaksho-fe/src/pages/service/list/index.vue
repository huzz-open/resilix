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
      :on-cancel="onCancel"
      @confirm="onConfirmDelete"
    />

    <!-- 创建/编辑服务对话框 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="dialogMode === 'create' ? '新建服务' : '编辑服务'"
      width="600px"
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
        <t-form-item label="服务码" name="serviceCode">
          <t-input-number
            v-model="formData.serviceCode"
            :min="serviceCodeMinValue"
            :max="serviceCodeMaxValue"
            placeholder="请输入服务码"
            theme="normal"
            :disabled="dialogMode === 'edit'"
          />
          <template #tips>
            <span v-if="dialogMode === 'edit'">服务码不可编辑</span>
            <span v-else>服务码范围：{{ serviceCodeMinValue }} - {{ serviceCodeMaxValue }}（{{ serviceCodeLength }} 位数字）</span>
          </template>
        </t-form-item>
        <t-form-item label="服务名称" name="name">
          <t-input
            v-model="formData.name"
            placeholder="请输入服务名称"
            :maxlength="100"
            show-word-limit
          />
        </t-form-item>
        <t-form-item label="服务描述" name="description">
          <t-textarea
            v-model="formData.description"
            :height="120"
            placeholder="请输入服务描述"
            :maxlength="255"
            show-word-limit
          />
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

import { getServiceList, createService, deleteService, getServiceDetail, updateService } from '@/api/service'
import type { ServiceModel } from '@/api/model/serviceModel'

// 从配置中获取服务码配置（可通过API获取，这里先硬编码）
const serviceCodeLength = ref(4)
const serviceCodeMinValue = ref(1000)
const serviceCodeMaxValue = ref(9999)

const COLUMNS: PrimaryTableCol<ServiceModel>[] = [
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

const onCancel = () => {
  confirmVisible.value = false
  deleteIdx.value = -1
}

// 创建/编辑对话框
const dialogVisible = ref(false)
const dialogMode = ref<'create' | 'edit'>('create')
const editingId = ref<number>()
const formRef = ref<FormInstanceFunctions>()
const formData = ref<{
  serviceCode: number | undefined
  name: string
  description: string
  remark: string
}>({
  serviceCode: undefined,
  name: '',
  description: '',
  remark: '',
})
const submitLoading = ref(false)

// 动态表单验证规则
const formRules = computed(() => ({
  serviceCode: dialogMode.value === 'create' ? [
    { required: true, message: '请输入服务码', type: 'error' as const },
    {
      validator: (val: number) => {
        return val >= serviceCodeMinValue.value && val <= serviceCodeMaxValue.value
      },
      message: `服务码必须在 ${serviceCodeMinValue.value} - ${serviceCodeMaxValue.value} 之间`,
      type: 'error' as const,
    },
  ] : [],
  name: [
    { required: true, message: '请输入服务名称', type: 'error' as const },
    { max: 100, message: '服务名称最多100个字符', type: 'warning' as const },
  ],
  description: [{ max: 255, message: '服务描述最多255个字符', type: 'warning' as const }],
  remark: [{ max: 255, message: '备注最多255个字符', type: 'warning' as const }],
}))

// 创建
const handleCreate = () => {
  dialogMode.value = 'create'
  dialogVisible.value = true
  formData.value = {
    serviceCode: undefined,
    name: '',
    description: '',
    remark: '',
  }
}

// 编辑
const handleClickEdit = async (slotProps: { row: ServiceModel }) => {
  dialogMode.value = 'edit'
  editingId.value = slotProps.row.id

  try {
    const detail = await getServiceDetail(slotProps.row.id!)
    formData.value = {
      serviceCode: detail.serviceCode,
      name: detail.name,
      description: detail.description || '',
      remark: detail.remark || '',
    }
    dialogVisible.value = true
  } catch (error) {
    console.error('获取服务详情失败:', error)
    MessagePlugin.error('获取服务详情失败')
  }
}

// 提交（创建或编辑）
const onSubmit = async ({ validateResult }: { validateResult: boolean }) => {
  if (!validateResult) return

  submitLoading.value = true
  try {
    if (dialogMode.value === 'create') {
      if (formData.value.serviceCode === undefined) {
        MessagePlugin.warning('请输入服务码')
        return
      }
      await createService({
        serviceCode: formData.value.serviceCode,
        name: formData.value.name,
        description: formData.value.description,
        remark: formData.value.remark,
      })
      MessagePlugin.success('创建成功')
    } else {
      await updateService(editingId.value!, {
        name: formData.value.name,
        description: formData.value.description,
        remark: formData.value.remark,
      })
      MessagePlugin.success('更新成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('操作失败:', error)
    MessagePlugin.error(dialogMode.value === 'create' ? '创建失败' : '更新失败')
  } finally {
    submitLoading.value = false
  }
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
}
</style>


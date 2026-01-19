<template>
  <base-list-page
    :title="t('pages.bizDomain.list.title')"
    :columns="columns"
    :fetch-api="getBizDomainList"
    :delete-api="deleteBizDomain"
    row-key="id"
    :show-search="false"
    :show-batch-delete="false"
    :show-detail="false"
    :show-edit="false"
    @create="handleCreate"
    @delete="handleDeleteSuccess"
  >
    <!-- 自定义搜索区域 -->
    <template #search>
      <div class="search-input">
        <t-input
          v-model="searchKeyword"
          :placeholder="t('pages.bizDomain.list.searchPlaceholder')"
          clearable
          @enter="handleSearch"
          @clear="handleSearch"
        >
          <template #suffix-icon>
            <search-icon size="16px" />
          </template>
        </t-input>
      </div>
    </template>

    <!-- 操作列 -->
    <template #op="{ row }">
      <t-space>
        <t-link theme="primary" @click="handleDetail(row)">
          {{ t('pages.bizDomain.list.detail') }}
        </t-link>
        <t-link theme="danger" @click="handleDelete(row)">
          {{ t('pages.bizDomain.list.delete') }}
        </t-link>
      </t-space>
    </template>
  </base-list-page>

  <!-- 创建/详情对话框 -->
  <t-dialog
    v-model:visible="dialogConfig.visible"
    :header="dialogTitle"
    width="600px"
    :confirm-btn="mode !== 'detail'"
    :on-confirm="handleDialogConfirm"
  >
    <t-form
      ref="formRef"
      :data="formData"
      :rules="formRules"
      label-align="top"
      :disabled="mode === 'detail'"
      @submit="handleDialogConfirm"
    >
      <t-form-item :label="t('pages.bizDomain.create.name')" name="name">
        <t-input
          v-model="formData.name"
          :placeholder="t('pages.bizDomain.create.namePlaceholder')"
          :maxlength="100"
          show-word-limit
        />
      </t-form-item>
      <t-form-item :label="t('pages.bizDomain.create.descriptionLabel')" name="description">
        <t-textarea
          v-model="formData.description"
          :height="120"
          :placeholder="t('pages.bizDomain.create.descriptionPlaceholder')"
          :maxlength="255"
          show-word-limit
        />
      </t-form-item>
      <t-form-item :label="t('pages.bizDomain.create.remarkLabel')" name="remark">
        <t-textarea
          v-model="formData.remark"
          :height="120"
          :placeholder="t('pages.bizDomain.create.remarkPlaceholder')"
          :maxlength="255"
          show-word-limit
        />
      </t-form-item>
    </t-form>
  </t-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { SearchIcon } from 'tdesign-icons-vue-next'
import { MessagePlugin } from 'tdesign-vue-next'
import type { PrimaryTableCol } from 'tdesign-vue-next'
import { t } from '@/locales'
import { getBizDomainList, createBizDomain, deleteBizDomain, getBizDomainDetail } from '@/api/bizDomain'
import { BaseListPage } from '@/components/base'
import { useDialogForm } from '@/composables'

// ========== 表格列定义 ==========
const columns: PrimaryTableCol[] = [
  { colKey: 'row-select', type: 'multiple', width: 64, fixed: 'left' },
  { title: t('pages.bizDomain.list.columns.name'), colKey: 'name', width: 200 },
  { title: t('pages.bizDomain.list.columns.description'), colKey: 'description', ellipsis: true },
  { title: t('pages.bizDomain.list.columns.remark'), colKey: 'remark', ellipsis: true },
  {
    title: t('pages.bizDomain.list.columns.createTime'),
    colKey: 'createTime',
    width: 180,
  },
  {
    title: t('pages.bizDomain.list.columns.updateTime'),
    colKey: 'updateTime',
    width: 180,
  },
  {
    title: t('pages.bizDomain.list.columns.operation'),
    colKey: 'op',
    width: 150,
    fixed: 'right',
  },
]

// ========== 搜索 ==========
const searchKeyword = ref('')
const listPageRef = ref()

const handleSearch = () => {
  // 触发列表刷新，将搜索关键字作为额外参数
  listPageRef.value?.fetchData({ keyword: searchKeyword.value })
}

// ========== 对话框表单 ==========
interface BizDomainForm {
  name: string
  description: string
  remark: string
}

const mode = ref<'create' | 'detail'>('create')

const { dialogConfig, formData, formRef, openCreate, openDetail, close, submit } = useDialogForm<BizDomainForm>({
  initialData: {
    name: '',
    description: '',
    remark: '',
  },
  onSubmit: async (data) => {
    await createBizDomain(data)
    listPageRef.value?.refresh()
  },
})

const dialogTitle = computed(() => {
  return mode.value === 'create' ? t('pages.bizDomain.create.title') : t('pages.bizDomain.list.detail')
})

const formRules = {
  name: [{ required: true, message: t('pages.bizDomain.create.nameRequired'), trigger: 'blur' }],
}

// ========== 事件处理 ==========
const handleCreate = () => {
  mode.value = 'create'
  openCreate()
}

const handleDetail = async (row: any) => {
  mode.value = 'detail'
  try {
    const detail = await getBizDomainDetail(row.id)
    openDetail(detail as any)
  } catch (error) {
    console.error('[BizDomain] 获取详情失败:', error)
    await MessagePlugin.error('获取详情失败')
  }
}

const handleDelete = async (row: any) => {
  // BaseListPage 已经处理了删除确认和 API 调用
  // 这里只需要处理删除成功后的逻辑
}

const handleDeleteSuccess = () => {
  // 删除成功后的额外处理（如果需要）
}

const handleDialogConfirm = async () => {
  if (mode.value === 'detail') {
    close()
    return
  }
  await submit()
}
</script>

<style lang="less" scoped>
.search-input {
  width: 360px;
}
</style>


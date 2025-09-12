<template>
  <div>
    <t-card class="list-card-container" :bordered="false">
      <t-row justify="space-between">
        <div class="left-operation-container">
          <t-button @click="handleCreate">{{ t('pages.bizDomain.list.create') }}</t-button>
          <p v-if="!!selectedRowKeys.length" class="selected-count">
            {{ t('pages.bizDomain.list.selectedCount', { count: selectedRowKeys.length }) }}
          </p>
        </div>
        <div class="search-input">
          <t-input v-model="searchValue" :placeholder="t('pages.bizDomain.list.searchPlaceholder')" clearable>
            <template #suffix-icon>
              <search-icon size="16px" />
            </template>
          </t-input>
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
            <t-link theme="primary" @click="handleClickDetail(slotProps)"
              >{{ t('pages.bizDomain.list.detail') }}
            </t-link>
            <t-link theme="danger" @click="handleClickDelete(slotProps)">{{ t('pages.bizDomain.list.delete') }}</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <t-dialog
      v-model:visible="confirmVisible"
      :header="t('pages.bizDomain.list.confirmDelete')"
      :body="confirmBody"
      :on-cancel="onCancel"
      @confirm="onConfirmDelete"
    />

    <!-- 创建业务领域对话框 -->
    <t-dialog
      v-model:visible="createDialogVisible"
      :header="t('pages.bizDomain.create.title')"
      width="600px"
      :footer="false"
    >
      <t-form
        ref="createFormRef"
        :data="createFormData"
        :rules="createFormRules"
        label-align="top"
        label-width="120"
        @submit="onCreateSubmit"
      >
        <t-form-item :label="t('pages.bizDomain.create.name')" name="name">
          <t-input
            v-model="createFormData.name"
            :placeholder="t('pages.bizDomain.create.namePlaceholder')"
            :maxlength="100"
            show-word-limit
          />
        </t-form-item>
        <t-form-item :label="t('pages.bizDomain.create.descriptionLabel')" name="description">
          <t-textarea
            v-model="createFormData.description"
            :height="120"
            :placeholder="t('pages.bizDomain.create.descriptionPlaceholder')"
            :maxlength="255"
            show-word-limit
          />
        </t-form-item>
        <div class="dialog-footer">
          <t-space>
            <t-button theme="default" @click="onCreateCancel">
              {{ t('pages.bizDomain.create.cancel') }}
            </t-button>
            <t-button theme="primary" type="submit" :loading="createSubmitLoading">
              {{ t('pages.bizDomain.create.submit') }}
            </t-button>
          </t-space>
        </div>
      </t-form>
    </t-dialog>
  </div>
</template>
<script setup lang="ts">
import { SearchIcon } from 'tdesign-icons-vue-next';
import type { FormInstanceFunctions, FormRule, PageInfo, PrimaryTableCol } from 'tdesign-vue-next';
import { MessagePlugin } from 'tdesign-vue-next';
import { computed, onMounted, ref } from 'vue';

import { createBizDomain, getBizDomainList } from '@/api/bizDomain';
import type { CreateBizDomainRequest } from '@/api/model/bizDomainModel';
import { prefix } from '@/config/global';
import { t } from '@/locales';
import { useSettingStore } from '@/store';

defineOptions({
  name: 'BizDomainList',
});

const store = useSettingStore();

const COLUMNS: PrimaryTableCol[] = [
  { colKey: 'row-select', type: 'multiple', width: 64, fixed: 'left' },
  {
    title: 'ID',
    align: 'left',
    width: 80,
    colKey: 'id',
    fixed: 'left',
  },
  {
    title: t('pages.bizDomain.list.name'),
    align: 'left',
    width: 200,
    colKey: 'name',
  },
  {
    title: t('pages.bizDomain.list.description'),
    width: 300,
    ellipsis: true,
    colKey: 'description',
  },
  {
    title: t('pages.bizDomain.list.createTime'),
    width: 180,
    colKey: 'createTime',
  },
  {
    title: t('pages.bizDomain.list.updateTime'),
    width: 180,
    colKey: 'updateTime',
  },
  {
    title: t('pages.bizDomain.list.operation'),
    align: 'left',
    fixed: 'right',
    width: 160,
    colKey: 'op',
  },
];

const listData = ref([]);
const pagination = ref({
  pageSize: 20,
  total: 0,
  current: 1,
});

const searchValue = ref('');
const dataLoading = ref(false);

// 创建对话框相关
const createDialogVisible = ref(false);
const createFormRef = ref<FormInstanceFunctions>();
const createSubmitLoading = ref(false);
const createFormData = ref<CreateBizDomainRequest>({
  name: '',
  description: '',
});

// 创建表单验证规则
const createFormRules: Record<string, FormRule[]> = {
  name: [
    { required: true, message: t('pages.bizDomain.create.nameRequired'), type: 'error' },
    { min: 1, max: 100, message: t('pages.bizDomain.create.nameLength'), type: 'error' },
  ],
  description: [{ max: 255, message: t('pages.bizDomain.create.descriptionLength'), type: 'error' }],
};

const fetchData = async () => {
  dataLoading.value = true;
  try {
    const { rows, total } = await getBizDomainList({
      current: pagination.value.current,
      pageSize: pagination.value.pageSize,
    });
    // 设置表格数据
    listData.value = rows;
    // 设置分页总数
    pagination.value = {
      ...pagination.value,
      total,
    };
  } catch (e) {
    console.log(e);
    await MessagePlugin.error(t('pages.bizDomain.list.fetchFailed'));
  } finally {
    dataLoading.value = false;
  }
};

const deleteIdx = ref(-1);
const confirmBody = computed(() => {
  if (deleteIdx.value > -1) {
    const { name } = listData.value[deleteIdx.value];
    return `删除后，${name}的所有信息将被清空，且无法恢复`;
  }
  return '';
});

onMounted(() => {
  fetchData();
});

const confirmVisible = ref(false);
const selectedRowKeys = ref<(string | number)[]>([]);

const resetIdx = () => {
  deleteIdx.value = -1;
};

const onConfirmDelete = () => {
  // 真实业务请发起请求
  listData.value.splice(deleteIdx.value, 1);
  pagination.value.total = listData.value.length;
  const selectedIdx = selectedRowKeys.value.indexOf(deleteIdx.value);
  if (selectedIdx > -1) {
    selectedRowKeys.value.splice(selectedIdx, 1);
  }
  confirmVisible.value = false;
  MessagePlugin.success(t('pages.bizDomain.list.deleteSuccess'));
  resetIdx();
};

const onCancel = () => {
  resetIdx();
};

const rowKey = 'id';

const rehandleSelectChange = (val: (string | number)[]) => {
  selectedRowKeys.value = val;
};

const rehandlePageChange = (pageInfo: PageInfo, newDataSource: any) => {
  pagination.value.current = pageInfo.current;
  console.log('分页变化', newDataSource);
  fetchData();
};

const rehandleChange = (changeParams: unknown, triggerAndData: unknown) => {
  console.log('统一Change', changeParams, triggerAndData);
};

const handleClickDetail = (row: any) => {
  console.log('查看详情', row);
  // TODO: 实现详情页面
};

const handleCreate = () => {
  createDialogVisible.value = true;
  // 重置表单数据
  createFormData.value = {
    name: '',
    description: '',
  };
};

const handleClickDelete = (row: { rowIndex: any }) => {
  deleteIdx.value = row.rowIndex;
  confirmVisible.value = true;
};

// 创建表单相关方法
const onCreateSubmit = async () => {
  if (!createFormRef.value) return;

  const validateResult = await createFormRef.value.validate();
  if (validateResult === true) {
    createSubmitLoading.value = true;
    try {
      await createBizDomain(createFormData.value);
      await MessagePlugin.success(t('pages.bizDomain.create.createSuccess'));
      createDialogVisible.value = false;
      // 刷新列表数据
      await fetchData();
    } catch (error) {
      console.error('创建失败:', error);
      await MessagePlugin.error(t('pages.bizDomain.create.createFailed'));
    } finally {
      createSubmitLoading.value = false;
    }
  }
};

const onCreateCancel = () => {
  createDialogVisible.value = false;
  // 重置表单数据
  createFormData.value = {
    name: '',
    description: '',
  };
};

const headerAffixedTop = computed(
  () =>
    ({
      offsetTop: store.isUseTabsRouter ? 48 : 0,
      container: `.${prefix}-layout`,
    }) as any,
);
</script>
<style lang="less" scoped>
.list-card-container {
  padding: var(--td-comp-paddingTB-xxl) var(--td-comp-paddingLR-xxl);

  :deep(.t-card__body) {
    padding: 0;
  }
}

.left-operation-container {
  display: flex;
  align-items: center;
  margin-bottom: var(--td-comp-margin-xxl);

  .selected-count {
    display: inline-block;
    margin-left: var(--td-comp-margin-l);
    color: var(--td-text-color-secondary);
  }
}

.search-input {
  width: 360px;
}

.dialog-footer {
  margin-top: var(--td-comp-margin-l);
  text-align: right;
}
</style>

<template>
  <div>
    <t-card class="list-card-container" :bordered="false">
      <t-row justify="space-between">
        <div class="left-operation-container">
          <t-button @click="handleCreate">{{ t('pages.bizField.list.create') }}</t-button>
          <p v-if="!!selectedRowKeys.length" class="selected-count">
            {{ t('pages.bizField.list.selectedCount', { count: selectedRowKeys.length }) }}
          </p>
        </div>
        <div class="search-input">
          <t-input v-model="searchValue" :placeholder="t('pages.bizField.list.searchPlaceholder')" clearable>
            <template #suffix-icon>
              <search-icon size="16px" />
            </template>
          </t-input>
        </div>
      </t-row>
      <t-table
        :data="listData"
        :columns="COLUMNS"
        row-key="bizField.id"
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
            <t-link theme="primary" @click="handleClickDetail(slotProps)">{{ t('pages.bizField.list.detail') }}</t-link>
            <t-link theme="danger" @click="handleClickDelete(slotProps)">{{ t('pages.bizField.list.delete') }}</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <t-dialog
      v-model:visible="confirmVisible"
      :header="t('pages.bizField.list.confirmDelete')"
      :body="confirmBody"
      :on-cancel="onCancel"
      @confirm="onConfirmDelete"
    />

    <t-dialog
      v-model:visible="createDialogVisible"
      :header="t('pages.bizField.create.title')"
      width="50%"
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
        <t-form-item :label="t('pages.bizField.create.name')" name="name">
          <t-input
            v-model="createFormData.name"
            :placeholder="t('pages.bizField.create.namePlaceholder')"
            :maxlength="100"
            show-word-limit
          />
        </t-form-item>
        <t-form-item :label="t('pages.bizField.create.descriptionLabel')" name="description">
          <t-textarea
            v-model="createFormData.description"
            :height="120"
            :placeholder="t('pages.bizField.create.descriptionPlaceholder')"
            :maxlength="255"
            show-word-limit
          />
        </t-form-item>
        <t-form-item :label="t('pages.bizField.create.bizFieldTypeId')" name="bizFieldTypeId">
          <t-table
            :data="typeListData"
            :columns="TYPE_COLUMNS"
            select-on-row-click
            row-key="id"
            :hover="true"
            :pagination="typePagination"
            :selected-row-keys="selectedTypeKeys"
            :loading="typeLoading"
            @page-change="onTypePageChange"
            @change="onTypeChange"
            @select-change="onTypeSelectChange"
          />
        </t-form-item>
        <div class="dialog-footer">
          <t-space>
            <t-button theme="default" @click="onCreateCancel">{{ t('pages.bizField.create.cancel') }}</t-button>
            <t-button theme="primary" type="submit" :loading="createSubmitLoading">{{
              t('pages.bizField.create.submit')
            }}</t-button>
          </t-space>
        </div>
      </t-form>
    </t-dialog>
  </div>
</template>
<script setup lang="ts">
import { SearchIcon } from 'tdesign-icons-vue-next';
import type { FormInstanceFunctions, FormRule, PageInfo, PrimaryTableCol } from 'tdesign-vue-next';
import { MessagePlugin, Tooltip } from 'tdesign-vue-next';
import { computed, onMounted, ref } from 'vue';

import { createBizField, getBizFieldList, deleteBizField } from '@/api/bizField';
import { getBizFieldTypeList } from '@/api/bizFieldType';
import type { CreateBizFieldRequest } from '@/api/model/bizFieldModel';
import type { BizFieldTypeModel } from '@/api/model/bizFieldTypeModel';
import { prefix } from '@/config/global';
import { t } from '@/locales';
import { useSettingStore } from '@/store';

defineOptions({
  name: 'BizFieldList',
});

const store = useSettingStore();

const COLUMNS: PrimaryTableCol[] = [
  { colKey: 'row-select', type: 'multiple', width: 64, fixed: 'left' },
  { title: t('pages.bizField.list.name'), align: 'left', width: 200, colKey: 'bizField.name' },
  { title: t('pages.bizField.list.description'), width: 300, ellipsis: true, colKey: 'bizField.description' },
  {
    title: t('pages.bizField.list.bizFieldTypeName'),
    width: 200,
    colKey: 'bizFieldType.name',
    ellipsis: true,
    cell(h, { row }) {
      const name = row?.bizFieldType?.name;
      const desc = row?.bizFieldType?.description;
      if (!name) return name;
      return h(Tooltip, { content: desc || '' }, { default: () => name });
    },
  },
  { title: t('pages.bizField.list.collectionType'), width: 140, colKey: 'bizFieldType.collectionType' },
  { title: t('pages.bizField.list.minimum'), width: 140, colKey: 'bizFieldType.minimum' },
  { title: t('pages.bizField.list.maximum'), width: 140, colKey: 'bizFieldType.maximum' },
  { title: t('pages.bizField.list.createTime'), width: 180, colKey: 'bizField.createTime' },
  { title: t('pages.bizField.list.updateTime'), width: 180, colKey: 'bizField.updateTime' },
  { title: t('pages.bizField.list.operation'), align: 'left', fixed: 'right', width: 160, colKey: 'op' },
];

const listData = ref<any[]>([]);
const pagination = ref({ pageSize: 20, total: 0, current: 1 });

const searchValue = ref('');
const dataLoading = ref(false);

// 创建对话框相关
const createDialogVisible = ref(false);
const createFormRef = ref<FormInstanceFunctions>();
const createSubmitLoading = ref(false);
const createFormData = ref<CreateBizFieldRequest>({
  name: '',
  description: '',
  bizFieldTypeId: undefined as unknown as number,
});

// 字段类型选择（表格单选）
const TYPE_COLUMNS: PrimaryTableCol[] = [
  { colKey: 'row-select', type: 'single', width: 64, fixed: 'left' },
  { title: t('pages.bizField.list.bizFieldTypeName'), width: 200, colKey: 'name', ellipsis: true },
  { title: t('pages.bizField.list.collectionType'), width: 140, colKey: 'collectionType' },
  { title: t('pages.bizField.list.minimum'), width: 140, colKey: 'minimum' },
  { title: t('pages.bizField.list.maximum'), width: 140, colKey: 'maximum' },
  { title: t('pages.bizField.list.description'), width: 300, colKey: 'description', ellipsis: true },
];
const typeListData = ref<BizFieldTypeModel[]>([]);
const typePagination = ref({ pageSize: 10, total: 0, current: 1 });
const typeLoading = ref(false);
const selectedTypeKeys = ref<(string | number)[]>([]);

const fetchTypeData = async () => {
  typeLoading.value = true;
  try {
    const { rows, total } = await getBizFieldTypeList({
      current: typePagination.value.current,
      pageSize: typePagination.value.pageSize,
    });
    typeListData.value = rows as BizFieldTypeModel[];
    typePagination.value = { ...typePagination.value, total };
  } finally {
    typeLoading.value = false;
  }
};

const onTypePageChange = (pageInfo: PageInfo) => {
  typePagination.value.current = pageInfo.current;
  typePagination.value.pageSize = pageInfo.pageSize;
  fetchTypeData();
};
const onTypeChange = () => {
  // no-op for now
};
const onTypeSelectChange = (keys: (string | number)[]) => {
  selectedTypeKeys.value = keys;
  createFormData.value.bizFieldTypeId = keys[0] as number as number;
};

// 创建表单验证规则
const createFormRules: Record<string, FormRule[]> = {
  name: [
    { required: true, message: t('pages.bizField.create.nameRequired'), type: 'error' },
    { min: 1, max: 100, message: t('pages.bizField.create.nameLength'), type: 'error' },
  ],
  description: [{ max: 255, message: t('pages.bizField.create.descriptionLength'), type: 'error' }],
  bizFieldTypeId: [{ required: true, message: t('pages.bizField.create.bizFieldTypeRequired'), type: 'error' }],
};

const fetchData = async () => {
  dataLoading.value = true;
  try {
    const { rows, total } = await getBizFieldList({
      current: pagination.value.current,
      pageSize: pagination.value.pageSize,
    });
    listData.value = rows;
    pagination.value = { ...pagination.value, total };
  } catch (e) {
    console.log(e);
    await MessagePlugin.error(t('pages.bizField.list.fetchFailed'));
  } finally {
    dataLoading.value = false;
  }
};

const deleteIdx = ref(-1);
const confirmBody = computed(() => {
  if (deleteIdx.value > -1) {
    const { name } = listData.value[deleteIdx.value];
    return t('pages.bizField.list.confirmDeleteBody', { name });
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

const onConfirmDelete = async () => {
  try {
    const id = listData.value[deleteIdx.value]?.bizField?.id ?? listData.value[deleteIdx.value]?.id;
    if (id == null) return;
    await deleteBizField(id);
    await MessagePlugin.success(t('pages.bizField.list.deleteSuccess'));
    confirmVisible.value = false;
    resetIdx();
    await fetchData();
  } catch (e) {
    console.error(e);
  }
};

const onCancel = () => {
  resetIdx();
};
const rehandleSelectChange = (val: (string | number)[]) => {
  selectedRowKeys.value = val;
};

const rehandlePageChange = (pageInfo: PageInfo, newDataSource: any) => {
  pagination.value.current = pageInfo.current;
  pagination.value.pageSize = pageInfo.pageSize;
  console.log('分页变化', newDataSource);
  fetchData();
};

const rehandleChange = (changeParams: unknown, triggerAndData: unknown) => {
  console.log('统一Change', changeParams, triggerAndData);
};

const handleClickDetail = (row: any) => {
  console.log('查看详情', row);
};

const handleCreate = () => {
  createDialogVisible.value = true;
  createFormData.value = { name: '', description: '', bizFieldTypeId: undefined as unknown as number };
  selectedTypeKeys.value = [];
  typePagination.value = { pageSize: 10, total: 0, current: 1 } as any;
  fetchTypeData();
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
      await createBizField(createFormData.value);
      await MessagePlugin.success(t('pages.bizField.create.createSuccess'));
      createDialogVisible.value = false;
      await fetchData();
    } catch (error) {
      console.error('创建失败:', error);
      await MessagePlugin.error(t('pages.bizField.create.createFailed'));
    } finally {
      createSubmitLoading.value = false;
    }
  }
};

const onCreateCancel = () => {
  createDialogVisible.value = false;
  createFormData.value = { name: '', description: '', bizFieldTypeId: undefined as unknown as number };
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

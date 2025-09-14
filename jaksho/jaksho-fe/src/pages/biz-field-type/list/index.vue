<template>
  <div>
    <t-card class="list-card-container" :bordered="false">
      <t-row justify="space-between">
        <div class="left-operation-container">
          <t-button @click="handleCreate">{{ t('pages.bizFieldType.list.create') }}</t-button>
          <p v-if="!!selectedRowKeys.length" class="selected-count">
            {{ t('pages.bizFieldType.list.selectedCount', { count: selectedRowKeys.length }) }}
          </p>
        </div>
        <div class="search-input">
          <t-input v-model="searchValue" :placeholder="t('pages.bizFieldType.list.searchPlaceholder')" clearable>
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
              >{{ t('pages.bizFieldType.list.detail') }}
            </t-link>
            <t-link theme="danger" @click="handleClickDelete(slotProps)"
              >{{ t('pages.bizFieldType.list.delete') }}
            </t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <t-dialog
      v-model:visible="confirmVisible"
      :header="t('pages.bizFieldType.list.confirmDelete')"
      :body="confirmBody"
      :on-cancel="onCancel"
      @confirm="onConfirmDelete"
    />

    <t-dialog
      v-model:visible="createDialogVisible"
      :header="t('pages.bizFieldType.create.title')"
      width="60%"
      :footer="false"
    >
      <t-form
        ref="createFormRef"
        :data="createFormData"
        :rules="createFormRules"
        label-align="top"
        label-width="140"
        @submit="onCreateSubmit"
      >
        <t-row :gutter="16">
          <t-col :span="6">
            <t-form-item :label="t('pages.bizFieldType.create.name')" name="name">
              <t-input
                v-model="createFormData.name"
                :placeholder="t('pages.bizFieldType.create.namePlaceholder')"
                :maxlength="100"
                show-word-limit
              />
            </t-form-item>
          </t-col>
          <t-col :span="6">
            <t-form-item :label="t('pages.bizFieldType.create.basicFieldType')" name="basicFieldType">
              <t-select v-model="createFormData.basicFieldType">
                <t-option v-for="op in basicFieldTypeOptions" :key="op.value" :value="op.value" :label="op.label" />
              </t-select>
            </t-form-item>
          </t-col>
        </t-row>
        <t-row :gutter="16">
          <t-col :span="6">
            <t-form-item :label="t('pages.bizFieldType.create.collectionType')" name="collectionType" required>
              <t-select v-model="createFormData.collectionType">
                <t-option v-for="op in collectionTypeOptions" :key="op.value" :value="op.value" :label="op.label" />
              </t-select>
            </t-form-item>
          </t-col>
          <t-col v-if="shouldShowMinMax" :span="3">
            <t-form-item :label="minLabelText" name="minimum">
              <t-input-number
                v-model="createFormData.minimum"
                :min="0"
                theme="column"
                :disabled="minimumAndMaximumDisabled"
                style="width: 100%"
              />
            </t-form-item>
          </t-col>
          <t-col v-if="shouldShowMinMax" :span="3">
            <t-form-item :label="maxLabelText" name="maximum">
              <t-input-number
                v-model="createFormData.maximum"
                :min="0"
                theme="column"
                :disabled="minimumAndMaximumDisabled"
                style="width: 100%"
              />
            </t-form-item>
          </t-col>
        </t-row>
        <t-form-item :label="t('pages.bizFieldType.create.descriptionLabel')" name="description">
          <t-textarea
            v-model="createFormData.description"
            :height="120"
            :placeholder="t('pages.bizFieldType.create.descriptionPlaceholder')"
            :maxlength="255"
            show-word-limit
          />
        </t-form-item>
        <t-form-item v-if="createFormData.basicFieldType === 'OBJECT'" name="objectBizFieldTypeRefDTOList">
          <j-tree-data
            ref="jTreeRef"
            :fetch-page="fetchBizFieldDomainPage"
            :columns="objectRefColumns"
            row-key="bizFieldDomain.id"
            selection="multiple"
            custom-field="bizFieldDomainId"
            :get-custom-value="(row: any) => row.bizFieldDomain.id"
          />
        </t-form-item>
        <div class="dialog-footer">
          <t-space>
            <t-button theme="default" @click="onCreateCancel">{{ t('pages.bizFieldType.create.cancel') }}</t-button>
            <t-button theme="primary" type="submit" :loading="createSubmitLoading">
              {{ t('pages.bizFieldType.create.submit') }}
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

import { createBizFieldType, getBizFieldTypeList } from '@/api/bizFieldType';
import type { CreateBizFieldTypeRequest } from '@/api/model/bizFieldTypeModel';
import JTreeData from '@/components/j-tree-data/index.vue';
import { prefix } from '@/config/global';
import { t } from '@/locales';
import { useSettingStore } from '@/store';
import { request } from '@/utils/request';

defineOptions({
  name: 'BizFieldTypeList',
});

const store = useSettingStore();
const jTreeRef = ref<InstanceType<typeof JTreeData> | null>(null);

const COLUMNS: PrimaryTableCol[] = [
  { colKey: 'row-select', type: 'multiple', width: 64, fixed: 'left' },
  { title: t('pages.bizFieldType.list.id'), align: 'left', width: 80, colKey: 'id', fixed: 'left' },
  { title: t('pages.bizFieldType.list.name'), align: 'left', width: 200, colKey: 'name' },
  { title: t('pages.bizFieldType.list.collectionType'), width: 140, colKey: 'collectionType' },
  { title: t('pages.bizFieldType.list.basicFieldType'), width: 140, colKey: 'basicFieldType' },
  { title: t('pages.bizFieldType.list.minimum'), width: 140, colKey: 'minimum' },
  { title: t('pages.bizFieldType.list.maximum'), width: 140, colKey: 'maximum' },
  { title: t('pages.bizFieldType.list.description'), width: 300, ellipsis: true, colKey: 'description' },
  { title: t('pages.bizFieldType.list.createTime'), width: 180, colKey: 'createTime' },
  { title: t('pages.bizFieldType.list.updateTime'), width: 180, colKey: 'updateTime' },
  { title: t('pages.bizFieldType.list.operation'), align: 'left', fixed: 'right', width: 160, colKey: 'op' },
];

const listData = ref([]);
const pagination = ref({ pageSize: 20, total: 0, current: 1 });

const searchValue = ref('');
const dataLoading = ref(false);

// 创建对话框相关
const createDialogVisible = ref(false);
const createFormRef = ref<FormInstanceFunctions>();
const createSubmitLoading = ref(false);
const createFormData = ref<CreateBizFieldTypeRequest>({
  name: '',
  description: '',
  minimum: undefined,
  maximum: undefined,
  collectionType: 'NONE',
  basicFieldType: 'STRING',
  objectBizFieldTypeRefDTOList: [],
});

const collectionTypeOptions = [
  { label: t('pages.bizFieldType.create.collectionTypeOptions.NONE'), value: 'NONE' },
  { label: t('pages.bizFieldType.create.collectionTypeOptions.LIST'), value: 'LIST' },
  { label: t('pages.bizFieldType.create.collectionTypeOptions.SET'), value: 'SET' },
  { label: t('pages.bizFieldType.create.collectionTypeOptions.ARRAY'), value: 'ARRAY' },
];

const basicFieldTypeOptions = [
  'BOOLEAN',
  'INT8',
  'INT16',
  'INT32',
  'INT64',
  'FLOAT',
  'DOUBLE',
  'STRING',
  'OBJECT',
  'FILE',
].map((x) => ({ label: t(`pages.bizFieldType.create.basicFieldTypeOptions.${x}`), value: x }));

// 创建表单验证规则
const createFormRules: Record<string, FormRule[]> = {
  name: [
    { required: true, message: t('pages.bizFieldType.create.nameRequired'), type: 'error' },
    { min: 1, max: 100, message: t('pages.bizFieldType.create.nameLength'), type: 'error' },
  ],
  collectionType: [{ required: true, message: t('pages.bizFieldType.create.collectionTypeRequired'), type: 'error' }],
  basicFieldType: [{ required: true, message: t('pages.bizFieldType.create.basicFieldTypeRequired'), type: 'error' }],
  minimum: [{ type: 'error', message: t('pages.bizFieldType.create.minimumInvalid') }],
  maximum: [{ type: 'error', message: t('pages.bizFieldType.create.maximumInvalid') }],
};

// ========== 动态标签/占位符：最小/最大值 ==========
const numericTypes = new Set(['INT8', 'INT16', 'INT32', 'INT64', 'FLOAT', 'DOUBLE']);
const isCollection = computed(() => createFormData.value.collectionType !== 'NONE');
const minimumAndMaximumDisabled = computed(() => {
  return !(isCollection.value || createFormData.value.basicFieldType !== 'OBJECT');
});
const isNumeric = computed(() => numericTypes.has(createFormData.value.basicFieldType as string));
const isStringType = computed(() => createFormData.value.basicFieldType === 'STRING');
const isFileType = computed(() => createFormData.value.basicFieldType === 'FILE');
const supportsMinMax = computed(() => isCollection.value || isNumeric.value || isStringType.value || isFileType.value);
const minLabelText = computed(() => {
  if (isCollection.value) return t('pages.bizFieldType.create.minElements');
  if (isNumeric.value) return t('pages.bizFieldType.create.minValue');
  if (isStringType.value) return t('pages.bizFieldType.create.minLength');
  if (isFileType.value) return t('pages.bizFieldType.create.minFileSize');
  return t('pages.bizFieldType.create.minimum');
});
const maxLabelText = computed(() => {
  if (isCollection.value) return t('pages.bizFieldType.create.maxElements');
  if (isNumeric.value) return t('pages.bizFieldType.create.maxValue');
  if (isStringType.value) return t('pages.bizFieldType.create.maxLength');
  if (isFileType.value) return t('pages.bizFieldType.create.maxFileSize');
  return t('pages.bizFieldType.create.maximum');
});
const shouldShowMinMax = computed(() => supportsMinMax.value);
// 移除占位符，改由 label 完整表达语义

// ========== OBJECT 类型：引用字段树 ==========
const objectRefColumns: PrimaryTableCol[] = [
  { colKey: 'row-select', type: 'multiple' },
  { title: t('pages.bizFieldType.create.objectRef.name'), colKey: 'bizFieldType.name', ellipsis: true },
  {
    title: t('pages.bizFieldType.create.objectRef.basicFieldType'),
    colKey: 'bizFieldType.basicFieldType',
    ellipsis: true,
  },
  {
    title: t('pages.bizFieldType.create.objectRef.collectionType'),
    colKey: 'bizFieldType.collectionType',
    ellipsis: true,
  },
  { title: t('pages.bizFieldType.create.objectRef.minimum'), colKey: 'bizFieldType.minimum', ellipsis: true },
  { title: t('pages.bizFieldType.create.objectRef.maximum'), colKey: 'bizFieldType.maximum', ellipsis: true },
  { title: t('pages.bizFieldType.create.objectRef.description'), colKey: 'bizFieldType.description', ellipsis: true },
];

async function fetchBizFieldDomainPage(params: { current: number; pageSize: number; keyword?: string }) {
  return request.post<{ rows: any[]; total: number }>({
    url: '/sr/biz-field-domain/page',
    data: { current: params.current, pageSize: params.pageSize, keyword: params.keyword },
  });
}

const fetchData = async () => {
  dataLoading.value = true;
  try {
    const { rows, total } = await getBizFieldTypeList({
      current: pagination.value.current,
      pageSize: pagination.value.pageSize,
    });
    listData.value = rows;
    pagination.value = { ...pagination.value, total };
  } catch (e) {
    console.log(e);
    await MessagePlugin.error(t('pages.bizFieldType.list.fetchFailed'));
  } finally {
    dataLoading.value = false;
  }
};

const deleteIdx = ref(-1);
const confirmBody = computed(() => {
  if (deleteIdx.value > -1) {
    const { name } = listData.value[deleteIdx.value];
    return t('pages.bizFieldType.list.confirmDeleteBody', { name });
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
  listData.value.splice(deleteIdx.value, 1);
  pagination.value.total = listData.value.length;
  const selectedIdx = selectedRowKeys.value.indexOf(deleteIdx.value);
  if (selectedIdx > -1) {
    selectedRowKeys.value.splice(selectedIdx, 1);
  }
  confirmVisible.value = false;
  MessagePlugin.success(t('pages.bizFieldType.list.deleteSuccess'));
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
  createFormData.value = {
    name: '',
    description: '',
    minimum: undefined,
    maximum: undefined,
    collectionType: 'NONE',
    basicFieldType: 'STRING',
    objectBizFieldTypeRefDTOList: [],
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
      const payload: CreateBizFieldTypeRequest = {
        ...createFormData.value,
      };
      // 若为 OBJECT，则从 j-tree-data 读取真实列表
      if (createFormData.value.basicFieldType === 'OBJECT') {
        const list = (jTreeRef.value?.getTreeDTOList?.() || []) as any[];
        (payload as any).objectBizFieldTypeRefDTOList = list.map((x: any) => ({
          bizFieldDomainId: x.bizFieldDomainId,
          sortOrder: x.sortOrder,
          ulid: x.ulid,
          parentUlid: x.parentUlid,
        }));
      } else {
        delete (payload as any).objectBizFieldTypeRefDTOList;
      }
      await createBizFieldType(payload);
      await MessagePlugin.success(t('pages.bizFieldType.create.createSuccess'));
      createDialogVisible.value = false;
      await fetchData();
    } catch (error) {
      console.error('创建失败:', error);
      await MessagePlugin.error(t('pages.bizFieldType.create.createFailed'));
    } finally {
      createSubmitLoading.value = false;
    }
  }
};

const onCreateCancel = () => {
  createDialogVisible.value = false;
  createFormData.value = {
    name: '',
    description: '',
    minimum: undefined,
    maximum: undefined,
    collectionType: 'NONE',
    basicFieldType: 'STRING',
    objectBizFieldTypeRefDTOList: [],
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

.create-dialog-body :deep(.t-dialog__body),
:deep(.t-dialog__body) {
  /* 防止表单栅格负外边距导致的横向滚动条 */
  overflow-x: hidden;
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

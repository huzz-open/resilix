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
        <t-row :gutter="16">
          <t-col :span="12">
            <t-form-item label="关联值字典（可选）" name="valueDictId">
              <t-select
                v-model="createFormData.valueDictId"
                :placeholder="'选择值字典（用于枚举类型）'"
                clearable
                filterable
              >
                <t-option
                  v-for="dict in valueDictList"
                  :key="dict.id"
                  :value="dict.id"
                  :label="`${dict.name} (${dict.description})`"
                />
              </t-select>
              <template #tips>
                选择值字典后，该字段类型将作为枚举类型，用于代码生成
              </template>
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

    <!-- 详情抽屉 -->
    <t-drawer v-model:visible="detailDrawerVisible" placement="right" size="40%" :footer="false">
      <template #header>
        <span>{{ t('pages.bizFieldType.detail.title') }}</span>
      </template>
      <div>
        <t-card :title="t('pages.bizFieldType.detail.basicInfo')" :bordered="false" style="margin-bottom: 16px">
          <t-descriptions :column="2" item-layout="horizontal" size="small" :label-width="100">
            <!-- 名称 独占一行 - 可编辑 -->
            <t-descriptions-item :label="t('pages.bizFieldType.list.name')" :span="2">
              <div v-if="!editingField.name" class="editable-field" @click="startEdit('name')">
                <span>{{ detailRecord?.name }}</span>
                <t-icon name="edit" size="14px" class="edit-icon" />
              </div>
              <div v-else class="editing-field">
                <t-input
                  v-model="editingValue.name"
                  size="small"
                  :maxlength="100"
                  autofocus
                  @blur="saveEdit('name')"
                  @keyup.enter="saveEdit('name')"
                  @keyup.esc="cancelEdit('name')"
                />
                <t-space size="4px" style="margin-left: 8px">
                  <t-button size="small" theme="primary" @click="saveEdit('name')">
                    <t-icon name="check" />
                  </t-button>
                  <t-button size="small" theme="default" @click="cancelEdit('name')">
                    <t-icon name="close" />
                  </t-button>
                </t-space>
              </div>
            </t-descriptions-item>
            <!-- 基础字段类型、集合类型 -->
            <t-descriptions-item :label="t('pages.bizFieldType.list.basicFieldType')">
              {{ detailRecord?.basicFieldType }}
            </t-descriptions-item>
            <t-descriptions-item :label="t('pages.bizFieldType.list.collectionType')">
              {{ detailRecord?.collectionType }}
            </t-descriptions-item>
            <!-- 最小值、最大值 -->
            <t-descriptions-item :label="t('pages.bizFieldType.list.minimum')">
              {{ detailRecord?.minimum }}
            </t-descriptions-item>
            <t-descriptions-item :label="t('pages.bizFieldType.list.maximum')">
              {{ detailRecord?.maximum }}
            </t-descriptions-item>
            <!-- 关联值字典 -->
            <t-descriptions-item label="关联值字典" :span="2">
              {{ getValueDictName(detailRecord?.valueDictId) || '-' }}
            </t-descriptions-item>
            <!-- 描述 独占一行 - 可编辑 -->
            <t-descriptions-item :label="t('pages.bizFieldType.list.description')" :span="2">
              <div v-if="!editingField.description" class="editable-field" @click="startEdit('description')">
                <span>{{ detailRecord?.description || '-' }}</span>
                <t-icon name="edit" size="14px" class="edit-icon" />
              </div>
              <div v-else class="editing-field">
                <t-textarea
                  v-model="editingValue.description"
                  :maxlength="255"
                  :autosize="{ minRows: 3, maxRows: 6 }"
                  autofocus
                  @blur="saveEdit('description')"
                  @keyup.esc="cancelEdit('description')"
                />
                <t-space size="4px" style="margin-top: 8px">
                  <t-button size="small" theme="primary" @click="saveEdit('description')">
                    <t-icon name="check" />
                  </t-button>
                  <t-button size="small" theme="default" @click="cancelEdit('description')">
                    <t-icon name="close" />
                  </t-button>
                </t-space>
              </div>
            </t-descriptions-item>
            <!-- 创建时间、更新时间 -->
            <t-descriptions-item :label="t('pages.bizFieldType.list.createTime')">
              {{ detailRecord?.createTime }}
            </t-descriptions-item>
            <t-descriptions-item :label="t('pages.bizFieldType.list.updateTime')">
              {{ detailRecord?.updateTime }}
            </t-descriptions-item>
          </t-descriptions>
        </t-card>

        <!-- OBJECT 类型的关联字段列表 - 使用树形组件 -->
        <t-card
          v-if="detailRecord?.basicFieldType === 'OBJECT'"
          :title="t('pages.bizFieldType.detail.objectRefList')"
          :bordered="false"
        >
          <j-tree-data
            v-if="detailObjectRefTreeData.length > 0"
            ref="jTreeRefDetail"
            :tree-dto-list="detailObjectRefTreeData"
            :fetch-page="fetchBizFieldDomainPage"
            :columns="DETAIL_OBJECT_REF_COLUMNS"
            :show-columns="DETAIL_OBJECT_REF_SHOW_COLUMNS"
            row-key="bizFieldDomain.id"
            selection="multiple"
            custom-field="bizFieldDomainId"
            :get-custom-value="(row: any) => row.bizFieldDomain.id"
          />
          <t-empty v-else description="暂无关联字段" />
          <div v-if="detailObjectRefTreeData.length > 0" style="margin-top: 16px; text-align: right">
            <t-button theme="primary" size="small" @click="saveObjectRefTree">
              {{ t('pages.bizFieldType.detail.saveFieldList') }}
            </t-button>
          </div>
        </t-card>
      </div>
    </t-drawer>
  </div>
</template>
<script setup lang="ts">
import { SearchIcon } from 'tdesign-icons-vue-next';
import type { FormInstanceFunctions, FormRule, PageInfo, PrimaryTableCol } from 'tdesign-vue-next';
import { MessagePlugin } from 'tdesign-vue-next';
import { computed, onMounted, ref } from 'vue';

import {
  createBizFieldType,
  deleteBizFieldType,
  getBizFieldTypeDetail,
  getBizFieldTypeList,
  updateBizFieldType,
  updateBizFieldTypeObjectRefs,
} from '@/api/bizFieldType';
import type { BizFieldTypeDetailResponse, CreateBizFieldTypeRequest } from '@/api/model/bizFieldTypeModel';
import type { ValueDictModel } from '@/api/model/valueDictModel';
import { getValueDictList } from '@/api/valueDict';
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
const jTreeRefDetail = ref<any>(null);

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
  valueDictId: undefined,
});

// 值字典列表
const valueDictList = ref<ValueDictModel[]>([]);

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
// 插入根节点弹窗表格列：bizField.name、bizFieldType.basicFieldType、bizFieldType.collectionType、
// bizFieldType.minimum、bizFieldType.maximum、bizField.createTime、bizField.updateTime
const objectRefColumns: PrimaryTableCol[] = [
  { colKey: 'row-select', type: 'multiple' },
  { title: t('pages.bizFieldType.create.objectRef.name'), colKey: 'bizField.name', ellipsis: true },
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
  { title: t('pages.bizFieldType.list.createTime'), colKey: 'bizField.createTime', ellipsis: true },
  { title: t('pages.bizFieldType.list.updateTime'), colKey: 'bizField.updateTime', ellipsis: true },
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

// 加载值字典列表
const loadValueDictList = async () => {
  try {
    const res = await getValueDictList({ current: 1, pageSize: 1000 });
    valueDictList.value = res.rows || res.records || [];
  } catch (error) {
    console.error('Failed to load value dict list:', error);
  }
};

// 获取值字典名称
const getValueDictName = (valueDictId?: number) => {
  if (!valueDictId) return '';
  const dict = valueDictList.value.find((d) => d.id === valueDictId);
  return dict ? `${dict.name} (${dict.description})` : '';
};

onMounted(() => {
  fetchData();
  loadValueDictList();
});

const confirmVisible = ref(false);
const selectedRowKeys = ref<(string | number)[]>([]);

const resetIdx = () => {
  deleteIdx.value = -1;
};

const onConfirmDelete = async () => {
  try {
    const id = listData.value[deleteIdx.value]?.id;
    if (id == null) return;
    await deleteBizFieldType(id);
    await MessagePlugin.success(t('pages.bizFieldType.list.deleteSuccess'));
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

const handleClickDetail = async (row: any) => {
  try {
    const id = row.row?.id;
    if (!id) return;
    const detail = await getBizFieldTypeDetail(id);
    detailRecord.value = detail;
    detailDrawerVisible.value = true;

    // 如果是 OBJECT 类型，准备展示关联字段列表（树形结构）
    if (detail.basicFieldType === 'OBJECT' && detail.objectBizFieldTypeRefList) {
      detailObjectRefList.value = detail.objectBizFieldTypeRefList;
      // 转换为树形数据格式
      detailObjectRefTreeData.value = detail.objectBizFieldTypeRefList.map((item: any) => ({
        ulid: item.objectBizFieldTypeRef?.ulid,
        parentUlid: item.objectBizFieldTypeRef?.parentUlid || '',
        sortOrder: item.objectBizFieldTypeRef?.sortOrder || 0,
        bizFieldDomainId: item.objectBizFieldTypeRef?.bizFieldDomainId,
        ...item,
      }));
      detailObjectRefPagination.value = {
        ...detailObjectRefPagination.value,
        total: detail.objectBizFieldTypeRefList.length,
      };
    } else {
      detailObjectRefTreeData.value = [];
    }
  } catch (error) {
    console.error('获取详情失败:', error);
    await MessagePlugin.error(t('pages.bizFieldType.list.fetchFailed'));
  }
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
    valueDictId: undefined,
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

// 详情抽屉状态与数据
const detailDrawerVisible = ref(false);
const detailRecord = ref<BizFieldTypeDetailResponse | null>(null);
const DETAIL_OBJECT_REF_COLUMNS: PrimaryTableCol[] = [
  { colKey: 'row-select', type: 'multiple' },
  { title: t('pages.bizFieldType.detail.fieldName'), colKey: 'bizField.name', ellipsis: true },
  {
    title: t('pages.bizFieldType.detail.fieldBasicType'),
    colKey: 'bizFieldType.basicFieldType',
    ellipsis: true,
  },
  {
    title: t('pages.bizFieldType.detail.fieldCollectionType'),
    colKey: 'bizFieldType.collectionType',
    ellipsis: true,
  },
  { title: t('pages.bizFieldType.detail.fieldMinimum'), colKey: 'bizFieldType.minimum', ellipsis: true },
  { title: t('pages.bizFieldType.detail.fieldMaximum'), colKey: 'bizFieldType.maximum', ellipsis: true },
];
const DETAIL_OBJECT_REF_SHOW_COLUMNS: PrimaryTableCol[] = [
  { title: t('pages.bizFieldType.detail.fieldName'), colKey: 'bizField.name', ellipsis: true },
  {
    title: t('pages.bizFieldType.detail.fieldBasicType'),
    colKey: 'bizFieldType.basicFieldType',
    ellipsis: true,
  },
  {
    title: t('pages.bizFieldType.detail.fieldCollectionType'),
    colKey: 'bizFieldType.collectionType',
    ellipsis: true,
  },
];
const detailObjectRefList = ref<any[]>([]);
const detailObjectRefTreeData = ref<any[]>([]);
const detailObjectRefPagination = ref({ pageSize: 10, total: 0, current: 1 });
const detailObjectRefLoading = ref(false);

const onDetailObjectRefPageChange = (pageInfo: PageInfo) => {
  detailObjectRefPagination.value.current = pageInfo.current;
  detailObjectRefPagination.value.pageSize = pageInfo.pageSize;
};

// 保存字段列表
const saveObjectRefTree = async () => {
  if (!detailRecord.value || !jTreeRefDetail.value) return;
  
  try {
    // 从树形组件获取当前的树形数据（只包含结构信息）
    const treeStructure = jTreeRefDetail.value.getTreeDTOList();
    
    // 需要将结构信息与原始数据合并
    // 因为 getTreeDTOList 只返回 ulid/parentUlid/sortOrder/bizFieldDomainId
    // 我们需要保留其他字段以便后续显示
    const dataMap = new Map();
    detailObjectRefTreeData.value.forEach((item: any) => {
      dataMap.set(item.ulid, item);
    });
    
    // 合并数据：保留原始的显示字段，更新结构字段
    const mergedData = treeStructure.map((structItem: any) => {
      const originalItem = dataMap.get(structItem.ulid);
      return {
        ...originalItem, // 保留所有原始字段（bizField, bizFieldType 等）
        ...structItem, // 更新结构字段（ulid, parentUlid, sortOrder）
      };
    });
    
    // 准备后端需要的数据格式
    const objectBizFieldTypeRefDTOList = treeStructure.map((item: any) => ({
      ulid: item.ulid,
      parentUlid: item.parentUlid || '',
      sortOrder: item.sortOrder,
      bizFieldDomainId: item.bizFieldDomainId,
    }));
    
    // 调用后端接口更新
    await updateBizFieldTypeObjectRefs(detailRecord.value.id!, {
      objectBizFieldTypeRefDTOList,
    });
    
    // 更新本地数据
    detailObjectRefTreeData.value = mergedData;
    
    await MessagePlugin.success(t('pages.bizFieldType.detail.saveFieldListSuccess'));
    
    // 刷新详情数据
    const detail = await getBizFieldTypeDetail(detailRecord.value.id!);
    detailRecord.value = detail;
    
    // 重新转换树形数据
    if (detail.basicFieldType === 'OBJECT' && detail.objectBizFieldTypeRefList) {
      detailObjectRefTreeData.value = detail.objectBizFieldTypeRefList.map((item: any) => ({
        ulid: item.objectBizFieldTypeRef?.ulid,
        parentUlid: item.objectBizFieldTypeRef?.parentUlid || '',
        sortOrder: item.objectBizFieldTypeRef?.sortOrder || 0,
        bizFieldDomainId: item.objectBizFieldTypeRef?.bizFieldDomainId,
        ...item,
      }));
    }
  } catch (error) {
    console.error('保存字段列表失败:', error);
    await MessagePlugin.error(t('pages.bizFieldType.detail.saveFieldListFailed'));
  }
};

// 内联编辑功能
const editingField = ref<Record<string, boolean>>({
  name: false,
  description: false,
});
const editingValue = ref<Record<string, string>>({
  name: '',
  description: '',
});
const originalValue = ref<Record<string, string>>({
  name: '',
  description: '',
});

const startEdit = (field: string) => {
  if (!detailRecord.value) return;
  editingField.value[field] = true;
  const value = (detailRecord.value as any)[field] || '';
  editingValue.value[field] = value;
  originalValue.value[field] = value;
};

const cancelEdit = (field: string) => {
  editingField.value[field] = false;
  editingValue.value[field] = originalValue.value[field];
};

const saveEdit = async (field: string) => {
  if (!detailRecord.value) return;
  
  // 验证
  if (field === 'name' && (!editingValue.value.name || editingValue.value.name.trim() === '')) {
    await MessagePlugin.warning(t('pages.bizFieldType.create.nameRequired'));
    return;
  }
  
  if (field === 'name' && (editingValue.value.name.length < 1 || editingValue.value.name.length > 100)) {
    await MessagePlugin.warning(t('pages.bizFieldType.create.nameLength'));
    return;
  }
  
  // 如果没有变化，直接取消编辑
  if (editingValue.value[field] === originalValue.value[field]) {
    editingField.value[field] = false;
    return;
  }
  
  try {
    const updateData: any = {};
    updateData[field] = editingValue.value[field];
    
    await updateBizFieldType(detailRecord.value.id!, updateData);
    await MessagePlugin.success(t('pages.bizFieldType.detail.editSuccess'));
    
    // 更新本地数据
    (detailRecord.value as any)[field] = editingValue.value[field];
    originalValue.value[field] = editingValue.value[field];
    editingField.value[field] = false;
    
    // 刷新列表
    await fetchData();
  } catch (error) {
    console.error('保存失败:', error);
    await MessagePlugin.error(t('pages.bizFieldType.detail.editFailed'));
    // 恢复原值
    editingValue.value[field] = originalValue.value[field];
  }
};
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

.editable-field {
  display: inline-flex;
  align-items: center;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 3px;
  transition: background-color 0.2s;
  min-height: 28px;

  &:hover {
    background-color: var(--td-bg-color-container-hover);

    .edit-icon {
      opacity: 1;
    }
  }

  .edit-icon {
    margin-left: 8px;
    opacity: 0;
    transition: opacity 0.2s;
    color: var(--td-text-color-placeholder);
  }
}

.editing-field {
  display: flex;
  align-items: flex-start;
  width: 100%;

  :deep(.t-input),
  :deep(.t-textarea) {
    flex: 1;
  }
}
</style>

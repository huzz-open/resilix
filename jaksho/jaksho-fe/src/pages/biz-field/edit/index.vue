<template>
  <div class="biz-field-edit-container">
    <t-card :bordered="false">
      <template #header>
        <div class="header-container">
          <t-button theme="default" @click="handleBack">{{ t('pages.bizField.edit.back') }}</t-button>
          <span>{{ isEdit ? t('pages.bizField.edit.title') : t('pages.bizField.create.title') }}</span>
        </div>
      </template>

      <t-form
        ref="formRef"
        :data="formData"
        :rules="formRules"
        label-align="top"
        label-width="120"
        @submit="onSubmit"
      >
        <t-form-item :label="t('pages.bizField.create.name')" name="name">
          <t-input
            v-model="formData.name"
            :placeholder="t('pages.bizField.create.namePlaceholder')"
            :maxlength="100"
            show-word-limit
            :disabled="isEdit"
          />
        </t-form-item>
        <t-form-item :label="t('pages.bizField.create.descriptionLabel')" name="description">
          <t-textarea
            v-model="formData.description"
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
            :disabled="isEdit"
            @page-change="onTypePageChange"
            @change="onTypeChange"
            @select-change="onTypeSelectChange"
          />
        </t-form-item>

        <t-form-item label="字段属性" name="fieldAttributes">
          <t-checkbox-group v-model="fieldAttributesCheckbox" @change="onFieldAttributesChange">
            <t-checkbox :value="1">可作为输入（请求参数）</t-checkbox>
            <t-checkbox :value="2">可作为输出（响应数据）</t-checkbox>
          </t-checkbox-group>
          <div style="margin-top: 8px; color: var(--td-text-color-placeholder); font-size: 12px;">
            用于区分字段的使用场景，便于在接口定义时自动过滤。默认推荐同时勾选输入和输出
          </div>
        </t-form-item>
        
        <!-- 详情展示区域（仅在编辑模式下显示）-->
        <template v-if="isEdit">
          <t-divider />
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
            <h4 style="margin: 0;">{{ t('pages.bizField.detail.domainList') }}</h4>
            <t-button theme="primary" @click="handleAddDomain">{{ t('pages.bizField.list.addDomainField') }}</t-button>
          </div>
          <t-table
            :data="detailDomainList"
            :columns="DETAIL_DOMAIN_COLUMNS"
            row-key="id"
            :hover="true"
            :pagination="detailDomainPagination"
            :loading="detailDomainLoading"
            @page-change="onDetailDomainPageChange"
          />
        </template>

        <div v-if="!isEdit" style="text-align: right; margin-top: 24px;">
          <t-button theme="primary" type="submit" :loading="submitLoading">
            创建
          </t-button>
        </div>
      </t-form>
    </t-card>

    <!-- 添加领域字段对话框 -->
    <t-dialog
      v-model:visible="addDomainDialogVisible"
      :header="t('pages.bizField.addDomain.title')"
      width="45%"
      :footer="false"
    >
      <t-form
        ref="addDomainFormRef"
        :data="addDomainFormData"
        :rules="addDomainFormRules"
        label-align="top"
        label-width="120"
        @submit="onAddDomainSubmit"
      >
        <t-form-item :label="t('pages.bizField.addDomain.bizDomainId')" name="bizDomainId">
          <t-table
            :data="domainListData"
            :columns="DOMAIN_COLUMNS"
            select-on-row-click
            row-key="id"
            :hover="true"
            :pagination="domainPagination"
            :selected-row-keys="selectedDomainKeys"
            :loading="domainLoading"
            @page-change="onDomainPageChange"
            @change="onDomainChange"
            @select-change="onDomainSelectChange"
          />
        </t-form-item>
        <t-form-item :label="t('pages.bizField.create.bizFieldTypeId')" name="bizFieldTypeId">
          <t-table
            :data="addTypeListData"
            :columns="ADD_TYPE_COLUMNS"
            select-on-row-click
            row-key="id"
            :hover="true"
            :pagination="addTypePagination"
            :selected-row-keys="selectedAddTypeKeys"
            :loading="addTypeLoading"
            @page-change="onAddTypePageChange"
            @change="onAddTypeChange"
            @select-change="onAddTypeSelectChange"
          />
        </t-form-item>
        <div class="dialog-footer">
          <t-space>
            <t-button theme="default" @click="onAddDomainCancel">{{ t('pages.bizField.addDomain.cancel') }}</t-button>
            <t-button theme="primary" type="submit" :loading="addDomainSubmitLoading">
              {{ t('pages.bizField.addDomain.submit') }}
            </t-button>
          </t-space>
        </div>
      </t-form>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import type { FormInstanceFunctions, FormRule, PageInfo, PrimaryTableCol } from 'tdesign-vue-next';
import { MessagePlugin, Tooltip } from 'tdesign-vue-next';
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { getBizDomainList } from '@/api/bizDomain';
import { createBizField, getBizFieldDetail } from '@/api/bizField';
import { createBizFieldDomain, getBizFieldDomainList } from '@/api/bizFieldDomain';
import { getBizFieldTypeList } from '@/api/bizFieldType';
import type { BizDomainModel } from '@/api/model/bizDomainModel';
import type { CreateBizFieldRequest } from '@/api/model/bizFieldModel';
import type { CreateBizFieldDomainRequest } from '@/api/model/bizFieldDomainModel';
import type { BasicFieldType, BizFieldTypeModel, CollectionType } from '@/api/model/bizFieldTypeModel';
import { t } from '@/locales';
import { useTabsRouterStore } from '@/store';
import { FieldAttribute } from '@/utils/fieldAttribute';

const route = useRoute();
const router = useRouter();
const tabsRouterStore = useTabsRouterStore();

const isEdit = computed(() => !!route.params.id);
const editingId = computed(() => (route.params.id ? Number(route.params.id) : undefined));

const formRef = ref<FormInstanceFunctions>();
const formData = ref<CreateBizFieldRequest>({
  name: '',
  description: '',
  bizFieldTypeId: undefined as unknown as number,
  fieldAttributes: 3, // 默认值：输入输出均可
});

// 字段属性复选框状态（位运算：1=输入, 2=输出）
const fieldAttributesCheckbox = ref<number[]>(FieldAttribute.toCheckboxArray(FieldAttribute.DEFAULT));

// 字段属性复选框变化处理
const onFieldAttributesChange = (value: number[]) => {
  if (value.length === 0) {
    // 如果都不选，恢复默认值
    formData.value.fieldAttributes = FieldAttribute.DEFAULT;
    fieldAttributesCheckbox.value = FieldAttribute.toCheckboxArray(FieldAttribute.DEFAULT);
    MessagePlugin.warning('至少需要选择一个字段属性，已恢复为默认值');
  } else {
    // 使用工具类进行位运算
    formData.value.fieldAttributes = FieldAttribute.fromCheckboxArray(value);
  }
};

const submitLoading = ref(false);

// 字段类型选择表格
const TYPE_COLUMNS: PrimaryTableCol[] = [
  { colKey: 'row-select', type: 'single', width: 64, fixed: 'left' },
  { title: t('pages.bizField.list.bizFieldTypeName'), width: 200, colKey: 'name', ellipsis: true },
  { title: t('pages.bizField.list.basicFieldType'), width: 120, colKey: 'basicFieldType', ellipsis: true },
  { title: t('pages.bizField.list.collectionType'), width: 120, colKey: 'collectionType' },
  { title: t('pages.bizField.list.minimum'), width: 100, colKey: 'minimum' },
  { title: t('pages.bizField.list.maximum'), width: 100, colKey: 'maximum' },
  { title: t('pages.bizField.list.description'), colKey: 'description', ellipsis: true },
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
const onTypeChange = () => {};
const onTypeSelectChange = (keys: (string | number)[]) => {
  selectedTypeKeys.value = keys;
  formData.value.bizFieldTypeId = keys[0] as number;
};

// 详情-领域列表（仅在编辑模式下）
const DETAIL_DOMAIN_COLUMNS: PrimaryTableCol[] = [
  { title: t('pages.bizField.detail.domainName'), width: 200, colKey: 'bizDomain.name', ellipsis: true },
  { title: t('pages.bizField.detail.domainDescription'), colKey: 'bizDomain.description', ellipsis: true },
  { title: t('pages.bizField.detail.createTime'), width: 180, colKey: 'createTime' },
  { title: t('pages.bizField.detail.updateTime'), width: 180, colKey: 'updateTime' },
];
const detailDomainList = ref<any[]>([]);
const detailDomainPagination = ref({ pageSize: 10, total: 0, current: 1 });
const detailDomainLoading = ref(false);

const fetchDetailDomainData = async () => {
  if (!editingId.value) return;
  detailDomainLoading.value = true;
  try {
    const { rows, total } = await getBizFieldDomainList({
      current: detailDomainPagination.value.current,
      pageSize: detailDomainPagination.value.pageSize,
      bizFieldId: editingId.value,
      excludeDefaultFieldDomain: true,
    });
    detailDomainList.value = rows as any[];
    detailDomainPagination.value = { ...detailDomainPagination.value, total } as any;
  } finally {
    detailDomainLoading.value = false;
  }
};

const onDetailDomainPageChange = (pageInfo: PageInfo) => {
  detailDomainPagination.value.current = pageInfo.current;
  detailDomainPagination.value.pageSize = pageInfo.pageSize;
  fetchDetailDomainData();
};

// 添加领域字段
const addDomainDialogVisible = ref(false);
const addDomainFormRef = ref<FormInstanceFunctions>();
const addDomainSubmitLoading = ref(false);
const addDomainFormData = ref<CreateBizFieldDomainRequest>({
  bizFieldId: 0,
  bizDomainId: 0,
  bizFieldTypeId: 0,
});

// 领域选择表格
const DOMAIN_COLUMNS: PrimaryTableCol[] = [
  { colKey: 'row-select', type: 'single', width: 64, fixed: 'left' },
  { title: t('pages.bizDomain.list.name'), width: 200, colKey: 'name', ellipsis: true },
  { title: t('pages.bizDomain.list.description'), width: 300, colKey: 'description', ellipsis: true },
  { title: t('pages.bizDomain.list.createTime'), width: 180, colKey: 'createTime' },
  { title: t('pages.bizDomain.list.updateTime'), width: 180, colKey: 'updateTime' },
];
const domainListData = ref<BizDomainModel[]>([]);
const domainPagination = ref({ pageSize: 10, total: 0, current: 1 });
const domainLoading = ref(false);
const selectedDomainKeys = ref<(string | number)[]>([]);

const fetchDomainData = async () => {
  domainLoading.value = true;
  try {
    const { rows, total } = await getBizDomainList({
      current: domainPagination.value.current,
      pageSize: domainPagination.value.pageSize,
      bizFieldId: editingId.value,
    });
    domainListData.value = rows as any;
    domainPagination.value = { ...domainPagination.value, total } as any;
  } finally {
    domainLoading.value = false;
  }
};
const onDomainPageChange = (pageInfo: PageInfo) => {
  domainPagination.value.current = pageInfo.current;
  domainPagination.value.pageSize = pageInfo.pageSize;
  fetchDomainData();
};
const onDomainChange = () => {};
const onDomainSelectChange = (keys: (string | number)[]) => {
  selectedDomainKeys.value = keys;
  addDomainFormData.value.bizDomainId = keys[0] as number;
};

// 添加领域字段-字段类型选择表格
const ADD_TYPE_COLUMNS: PrimaryTableCol[] = [
  { colKey: 'row-select', type: 'single', width: 64, fixed: 'left' },
  { title: t('pages.bizField.list.bizFieldTypeName'), width: 200, colKey: 'name', ellipsis: true },
  { title: t('pages.bizField.list.basicFieldType'), width: 80, colKey: 'basicFieldType', ellipsis: true },
  { title: t('pages.bizField.list.collectionType'), width: 80, colKey: 'collectionType' },
  { title: t('pages.bizField.list.minimum'), width: 80, colKey: 'minimum' },
  { title: t('pages.bizField.list.maximum'), width: 80, colKey: 'maximum' },
  { title: t('pages.bizField.list.description'), width: 300, colKey: 'description', ellipsis: true },
];
const addTypeListData = ref<BizFieldTypeModel[]>([]);
const addTypePagination = ref({ pageSize: 10, total: 0, current: 1 });
const addTypeLoading = ref(false);
const selectedAddTypeKeys = ref<(string | number)[]>([]);
// 依据当前字段的类型进行过滤
const filterBasicFieldType = ref<BasicFieldType | undefined>(undefined);
const filterCollectionType = ref<CollectionType | undefined>(undefined);

const fetchAddTypeData = async () => {
  addTypeLoading.value = true;
  try {
    const { rows, total } = await getBizFieldTypeList({
      current: addTypePagination.value.current,
      pageSize: addTypePagination.value.pageSize,
      basicFieldType: filterBasicFieldType.value,
      collectionType: filterCollectionType.value,
    });
    addTypeListData.value = rows as BizFieldTypeModel[];
    addTypePagination.value = { ...addTypePagination.value, total } as any;
  } finally {
    addTypeLoading.value = false;
  }
};

const onAddTypePageChange = (pageInfo: PageInfo) => {
  addTypePagination.value.current = pageInfo.current;
  addTypePagination.value.pageSize = pageInfo.pageSize;
  fetchAddTypeData();
};
const onAddTypeChange = () => {};
const onAddTypeSelectChange = (keys: (string | number)[]) => {
  selectedAddTypeKeys.value = keys;
  addDomainFormData.value.bizFieldTypeId = keys[0] as number;
};

const addDomainFormRules: Record<string, FormRule[]> = {
  bizDomainId: [{ required: true, message: t('pages.bizField.addDomain.bizDomainRequired'), type: 'error' }],
  bizFieldTypeId: [{ required: true, message: t('pages.bizField.create.bizFieldTypeRequired'), type: 'error' }],
};

// 打开添加领域字段对话框
const handleAddDomain = async () => {
  if (!editingId.value) return;

  // 从已加载的详情中获取字段类型信息
  const bizFieldTypeId = formData.value.bizFieldTypeId;
  if (!bizFieldTypeId) {
    MessagePlugin.warning('请先选择字段类型');
    return;
  }

  // 获取字段类型详情以获取基础类型和集合类型
  try {
    const typeDetail = typeListData.value.find((t) => t.id === bizFieldTypeId);
    if (typeDetail) {
      filterBasicFieldType.value = typeDetail.basicFieldType as BasicFieldType | undefined;
      filterCollectionType.value = typeDetail.collectionType as CollectionType | undefined;
    }
  } catch (error) {
    console.error('获取字段类型详情失败:', error);
  }

  addDomainFormData.value = {
    bizFieldId: editingId.value,
    bizDomainId: 0,
    bizFieldTypeId: bizFieldTypeId,
  } as any;

  selectedDomainKeys.value = [];
  domainPagination.value = { pageSize: 10, total: 0, current: 1 } as any;
  addDomainDialogVisible.value = true;
  await fetchDomainData();

  // 预选字段类型并拉取字段类型列表
  selectedAddTypeKeys.value = [bizFieldTypeId];
  addTypePagination.value = { pageSize: 10, total: 0, current: 1 } as any;
  await fetchAddTypeData();
};

const onAddDomainSubmit = async () => {
  if (!addDomainFormRef.value) return;
  const validateResult = await addDomainFormRef.value.validate();
  if (validateResult === true) {
    addDomainSubmitLoading.value = true;
    try {
      await createBizFieldDomain(addDomainFormData.value);
      await MessagePlugin.success(t('pages.bizField.addDomain.createSuccess'));
      addDomainDialogVisible.value = false;
      await fetchDetailDomainData(); // 刷新领域列表
    } catch (error) {
      console.error('添加领域字段失败:', error);
      await MessagePlugin.error(t('pages.bizField.addDomain.createFailed'));
    } finally {
      addDomainSubmitLoading.value = false;
    }
  }
};

const onAddDomainCancel = () => {
  addDomainDialogVisible.value = false;
};

// 表单验证规则
const formRules: Record<string, FormRule[]> = {
  name: [
    { required: true, message: t('pages.bizField.create.nameRequired'), type: 'error' },
    { min: 1, max: 100, message: t('pages.bizField.create.nameLength'), type: 'error' },
  ],
  description: [{ max: 255, message: t('pages.bizField.create.descriptionLength'), type: 'error' }],
  bizFieldTypeId: [{ required: true, message: t('pages.bizField.create.bizFieldTypeRequired'), type: 'error' }],
};

// 加载详情（编辑模式）
const loadDetail = async () => {
  if (!editingId.value) return;

  try {
    const detail = await getBizFieldDetail(editingId.value);
    const bizFieldData = (detail as any)?.bizField || detail;
    const bizFieldTypeData = (detail as any)?.bizFieldType;
    
    formData.value = {
      name: bizFieldData.name,
      description: bizFieldData.description || '',
      bizFieldTypeId: bizFieldData.bizFieldTypeId,
      fieldAttributes: bizFieldData.fieldAttributes || 3, // 如果没有值则默认为3
    };
    
    // 根据 fieldAttributes 值设置复选框状态（使用工具类）
    fieldAttributesCheckbox.value = FieldAttribute.toCheckboxArray(
      bizFieldData.fieldAttributes || FieldAttribute.DEFAULT,
    );
    
    // 预选字段类型
    if (bizFieldTypeData?.id) {
      selectedTypeKeys.value = [bizFieldTypeData.id];
    }
    
    // 加载关联的领域列表
    await fetchDetailDomainData();
  } catch (error) {
    console.error('获取业务字段详情失败:', error);
    MessagePlugin.error(t('pages.bizField.edit.fetchFailed'));
  }
};

// 提交（创建）
const onSubmit = async () => {
  if (!formRef.value) return;
  const validateResult = await formRef.value.validate();
  if (validateResult === true) {
    submitLoading.value = true;
    try {
      await createBizField(formData.value);
      MessagePlugin.success(t('pages.bizField.create.createSuccess'));
      handleBack();
    } catch (error) {
      console.error('创建失败:', error);
      MessagePlugin.error(t('pages.bizField.create.createFailed'));
    } finally {
      submitLoading.value = false;
    }
  }
};

// 返回列表
const handleBack = () => {
  // 关闭当前标签
  const currentPath = route.path;
  const currentIndex = tabsRouterStore.tabRouters.findIndex((r) => r.path === currentPath);
  if (currentIndex !== -1) {
    tabsRouterStore.subtractCurrentTabRouter({ path: currentPath, routeIdx: currentIndex });
  }
  // 返回列表页
  router.push({ name: 'BizFieldList' });
};

onMounted(async () => {
  await fetchTypeData();
  if (isEdit.value) {
    await loadDetail();
  }
});
</script>

<style lang="less" scoped>
.biz-field-edit-container {
  padding: 24px;
}

.header-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>

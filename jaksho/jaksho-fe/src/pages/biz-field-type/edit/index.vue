<template>
  <div class="biz-field-type-edit">
    <t-card :bordered="false">
      <template #header>
        <div style="display: flex; align-items: center; gap: 8px;">
          <t-button theme="default" @click="goBack">
            <template #icon><chevron-left-icon /></template>
            返回
          </t-button>
          <span style="font-size: 16px; font-weight: 500;">
            {{ isEditing ? '编辑业务字段类型' : t('pages.bizFieldType.create.title') }}
          </span>
        </div>
      </template>

      <t-form
        ref="formRef"
        :data="formData"
        :rules="formRules"
        label-align="top"
        label-width="140"
        @submit="onSubmit"
      >
        <t-row :gutter="16">
          <t-col :span="6">
            <t-form-item :label="t('pages.bizFieldType.create.name')" name="name">
              <t-input
                v-model="formData.name"
                :placeholder="t('pages.bizFieldType.create.namePlaceholder')"
                :maxlength="100"
                show-word-limit
              />
            </t-form-item>
          </t-col>
          <t-col :span="6">
            <t-form-item :label="t('pages.bizFieldType.create.basicFieldType')" name="basicFieldType">
              <t-select v-model="formData.basicFieldType" :disabled="isEditing">
                <t-option v-for="op in basicFieldTypeOptions" :key="op.value" :value="op.value" :label="op.label" />
              </t-select>
            </t-form-item>
          </t-col>
        </t-row>
        <t-row :gutter="16">
          <t-col :span="6">
            <t-form-item :label="t('pages.bizFieldType.create.collectionType')" name="collectionType" required>
              <t-select v-model="formData.collectionType">
                <t-option v-for="op in collectionTypeOptions" :key="op.value" :value="op.value" :label="op.label" />
              </t-select>
            </t-form-item>
          </t-col>
          <t-col v-if="shouldShowMinMax" :span="3">
            <t-form-item :label="minLabelText" name="minimum">
              <t-input-number
                v-model="formData.minimum"
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
                v-model="formData.maximum"
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
                v-model="formData.valueDictId"
                placeholder="选择值字典（用于枚举类型）"
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
            v-model="formData.description"
            :height="120"
            :placeholder="t('pages.bizFieldType.create.descriptionPlaceholder')"
            :maxlength="255"
            show-word-limit
          />
        </t-form-item>
        <t-form-item v-if="formData.basicFieldType === 'OBJECT'" name="objectBizFieldTypeRefDTOList">
          <j-tree-data
            ref="jTreeRef"
            v-model:tree-dto-list="formData.objectBizFieldTypeRefDTOList"
            :fetch-page="fetchBizFieldDomainPage"
            :columns="objectRefColumns"
            row-key="bizFieldDomain.id"
            selection="multiple"
            custom-field="bizFieldDomainId"
            :get-custom-value="(row: any) => row.bizFieldDomain.id"
          />
        </t-form-item>
        <div class="form-actions" style="text-align: right; margin-top: 24px;">
          <t-button theme="primary" type="submit" :loading="submitLoading">
            {{ isEditing ? '保存' : '创建' }}
          </t-button>
        </div>
      </t-form>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ChevronLeftIcon } from 'tdesign-icons-vue-next';
import { MessagePlugin } from 'tdesign-vue-next';
import type { FormInstanceFunctions, FormRule, PrimaryTableCol } from 'tdesign-vue-next';
import { createBizFieldType, getBizFieldTypeDetail, updateBizFieldType } from '@/api/bizFieldType';
import type { BizFieldTypeDetailResponse, CreateBizFieldTypeRequest } from '@/api/model/bizFieldTypeModel';
import type { ValueDictModel } from '@/api/model/valueDictModel';
import { getValueDictList } from '@/api/valueDict';
import JTreeData from '@/components/j-tree-data/index.vue';
import { t } from '@/locales';
import { useTabsRouterStore } from '@/store';
import { request } from '@/utils/request';

const route = useRoute();
const router = useRouter();
const tabsRouterStore = useTabsRouterStore();

const formRef = ref<FormInstanceFunctions>();
const submitLoading = ref(false);
const loading = ref(false);
const jTreeRef = ref();

const isEditing = computed(() => !!route.params.id);

const formData = ref<CreateBizFieldTypeRequest>({
  name: '',
  description: '',
  minimum: undefined,
  maximum: undefined,
  collectionType: 'NONE',
  basicFieldType: 'STRING',
  objectBizFieldTypeRefDTOList: [],
  valueDictId: undefined,
});

const valueDictList = ref<ValueDictModel[]>([]);

const collectionTypeOptions = [
  { label: t('pages.bizFieldType.create.collectionTypeOptions.NONE'), value: 'NONE' },
  { label: t('pages.bizFieldType.create.collectionTypeOptions.LIST'), value: 'LIST' },
  { label: t('pages.bizFieldType.create.collectionTypeOptions.SET'), value: 'SET' },
  { label: t('pages.bizFieldType.create.collectionTypeOptions.ARRAY'), value: 'ARRAY' },
];

const basicFieldTypeOptions = [
  'BOOLEAN', 'INT8', 'INT16', 'INT32', 'INT64', 'FLOAT', 'DOUBLE', 'STRING', 'OBJECT', 'FILE', 'SLOT',
].map((x) => ({ label: t(`pages.bizFieldType.create.basicFieldTypeOptions.${x}`), value: x }));

const numericTypes = new Set(['INT8', 'INT16', 'INT32', 'INT64', 'FLOAT', 'DOUBLE']);
const isNumeric = computed(() => numericTypes.has(formData.value.basicFieldType as string));
const isStringType = computed(() => formData.value.basicFieldType === 'STRING');
const isFileType = computed(() => formData.value.basicFieldType === 'FILE');
const isCollection = computed(() => formData.value.collectionType && formData.value.collectionType !== 'NONE');
const shouldShowMinMax = computed(() => isCollection.value || isNumeric.value || isStringType.value || isFileType.value);
const minimumAndMaximumDisabled = computed(() => isCollection.value);

const minLabelText = computed(() => {
  if (isCollection.value) return t('pages.bizFieldType.create.minItemsCountLabel');
  if (isNumeric.value) return t('pages.bizFieldType.create.minimumLabel');
  if (isStringType.value) return t('pages.bizFieldType.create.minLengthLabel');
  if (isFileType.value) return t('pages.bizFieldType.create.minFileSizeLabel');
  return '';
});

const maxLabelText = computed(() => {
  if (isCollection.value) return t('pages.bizFieldType.create.maxItemsCountLabel');
  if (isNumeric.value) return t('pages.bizFieldType.create.maximumLabel');
  if (isStringType.value) return t('pages.bizFieldType.create.maxLengthLabel');
  if (isFileType.value) return t('pages.bizFieldType.create.maxFileSizeLabel');
  return '';
});

const formRules = computed<Record<string, FormRule[]>>(() => ({
  name: [{ required: true, message: t('pages.bizFieldType.create.nameRequired'), type: 'error' }],
  basicFieldType: [{ required: true, message: t('pages.bizFieldType.create.basicFieldTypeRequired'), type: 'error' }],
  collectionType: [{ required: true, message: t('pages.bizFieldType.create.collectionTypeRequired'), type: 'error' }],
}));

const objectRefColumns: PrimaryTableCol[] = [
  { colKey: 'row-select', type: 'multiple' },
  { title: t('pages.bizFieldType.create.objectRef.name'), colKey: 'bizField.name', ellipsis: true },
  { title: t('pages.bizFieldType.create.objectRef.basicFieldType'), colKey: 'bizFieldType.basicFieldType', ellipsis: true },
  { title: t('pages.bizFieldType.create.objectRef.collectionType'), colKey: 'bizFieldType.collectionType', ellipsis: true },
  { title: t('pages.bizFieldType.create.objectRef.minimum'), colKey: 'bizFieldType.minimum', ellipsis: true },
  { title: t('pages.bizFieldType.create.objectRef.maximum'), colKey: 'bizFieldType.maximum', ellipsis: true },
];

async function fetchBizFieldDomainPage(params: { current: number; pageSize: number; keyword?: string }) {
  return request.post<{ rows: any[]; total: number }>({
    url: '/sr/biz-field-domain/page',
    data: { current: params.current, pageSize: params.pageSize, keyword: params.keyword },
  });
}

const loadValueDictList = async () => {
  try {
    const res = await getValueDictList({ current: 1, pageSize: 1000 });
    valueDictList.value = res.rows || res.records || [];
  } catch (error) {
    console.error('Failed to load value dict list:', error);
  }
};

const loadDetail = async () => {
  if (!isEditing.value) return;
  
  try {
    loading.value = true;
    const id = Number(route.params.id);
    const detail = await getBizFieldTypeDetail(id);
    
    // 转换 objectBizFieldTypeRefList 为树形 DTO 格式
    let objectBizFieldTypeRefDTOList: any[] = [];
    if (detail.basicFieldType === 'OBJECT' && detail.objectBizFieldTypeRefList) {
      objectBizFieldTypeRefDTOList = detail.objectBizFieldTypeRefList.map((item: any) => ({
        ulid: item.objectBizFieldTypeRef?.ulid,
        parentUlid: item.objectBizFieldTypeRef?.parentUlid || '',
        sortOrder: item.objectBizFieldTypeRef?.sortOrder || 0,
        bizFieldDomainId: item.objectBizFieldTypeRef?.bizFieldDomainId,
        ...item,
      }));
    }
    
    formData.value = {
      name: detail.name,
      description: detail.description || '',
      minimum: detail.minimum,
      maximum: detail.maximum,
      collectionType: detail.collectionType,
      basicFieldType: detail.basicFieldType,
      objectBizFieldTypeRefDTOList,
      valueDictId: detail.valueDictId,
    };
  } catch (error) {
    console.error('Failed to load detail:', error);
    MessagePlugin.error('加载数据失败');
  } finally {
    loading.value = false;
  }
};

const goBack = () => {
  const currentPath = route.path;
  const currentIndex = tabsRouterStore.tabRouters.findIndex(r => r.path === currentPath);
  if (currentIndex !== -1) {
    tabsRouterStore.subtractCurrentTabRouter({ path: currentPath, routeIdx: currentIndex });
  }
  router.push({ name: 'BizFieldTypeList' });
};

const onSubmit = async ({ validateResult }: { validateResult: boolean }) => {
  if (!validateResult) return;

  try {
    submitLoading.value = true;
    
    if (formData.value.basicFieldType === 'OBJECT' && jTreeRef.value) {
      const { treeData } = jTreeRef.value.getTreeInfo();
      formData.value.objectBizFieldTypeRefDTOList = treeData;
    }

    if (isEditing.value) {
      const id = Number(route.params.id);
      await updateBizFieldType(id, formData.value);
      MessagePlugin.success('更新成功');
    } else {
      await createBizFieldType(formData.value);
      MessagePlugin.success('创建成功');
    }
    goBack();
  } catch (error) {
    console.error('Failed to submit:', error);
    MessagePlugin.error('操作失败');
  } finally {
    submitLoading.value = false;
  }
};

onMounted(() => {
  loadValueDictList();
  loadDetail();
});
</script>

<style scoped lang="less">
.biz-field-type-edit {
  padding: 20px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  padding-top: 24px;
  border-top: 1px solid var(--td-component-border);
  margin-top: 24px;
}
</style>

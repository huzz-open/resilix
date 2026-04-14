<template>
  <div class="value-dict-edit">
    <t-card :bordered="false">
      <template #header>
        <div style="display: flex; align-items: center; gap: 8px;">
          <t-button theme="default" @click="goBack">
            <template #icon><chevron-left-icon /></template>
            {{ t('pages.valueDict.edit.back') }}
          </t-button>
          <span style="font-size: 16px; font-weight: 500;">
            {{ isEditing ? t('pages.valueDict.create.editTitle') : t('pages.valueDict.create.title') }}
          </span>
        </div>
      </template>

      <t-form
        ref="formRef"
        :data="formData"
        :rules="formRules"
        label-align="top"
        @submit="onSubmit"
      >
        <t-row :gutter="16">
          <t-col :span="6">
            <t-form-item :label="t('pages.valueDict.create.nameLabel')" name="name">
              <t-input
                v-model="formData.name"
                :placeholder="t('pages.valueDict.create.namePlaceholder')"
                :maxlength="100"
                show-word-limit
                :disabled="isEditing"
              />
              <template #tips>{{ t('pages.valueDict.create.nameHelp') }}</template>
            </t-form-item>
          </t-col>
          <t-col :span="6">
            <t-form-item :label="t('pages.valueDict.create.typeLabel')" name="type">
              <t-select 
                v-model="formData.type" 
                :placeholder="t('pages.valueDict.create.typeRequired')"
                :disabled="isEditing"
              >
                <t-option value="STR" :label="t('pages.valueDict.create.typeStr')" />
                <t-option value="INT" :label="t('pages.valueDict.create.typeInt')" />
                <t-option value="CHAR" :label="t('pages.valueDict.create.typeChar')" />
                <t-option value="FLOAT" :label="t('pages.valueDict.create.typeFloat')" />
              </t-select>
            </t-form-item>
          </t-col>
          <t-col :span="12">
            <t-form-item :label="t('pages.valueDict.create.descriptionLabel')" name="description">
              <t-input
                v-model="formData.description"
                :placeholder="t('pages.valueDict.create.descriptionPlaceholder')"
                :maxlength="255"
                show-word-limit
              />
            </t-form-item>
          </t-col>
        </t-row>

        <t-form-item :label="t('pages.valueDict.create.itemsLabel')" name="items">
          <dict-item-manager v-model="formData.items" :dict-type="formData.type" />
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
import { useI18n } from 'vue-i18n';
import { ChevronLeftIcon } from 'tdesign-icons-vue-next';
import { MessagePlugin } from 'tdesign-vue-next';
import type { FormInstanceFunctions, FormRule, SubmitContext } from 'tdesign-vue-next';
import { getValueDictDetail, createValueDict, updateValueDict } from '@/api/valueDict';
import type { ValueDictItemModel, ValueDictType } from '@/api/model/valueDictModel';
import { useTabsRouterStore } from '@/store';
import DictItemManager from '../components/DictItemManager.vue';

const { t } = useI18n();
const route = useRoute();
const router = useRouter();
const tabsRouterStore = useTabsRouterStore();

const formRef = ref<FormInstanceFunctions>();
const submitLoading = ref(false);
const loading = ref(false);

const isEditing = computed(() => !!route.params.id);

interface FormData {
  id?: number;
  name: string;
  description: string;
  type: ValueDictType;
  remark: string;
  items: ValueDictItemModel[];
}

const formData = ref<FormData>({
  name: '',
  description: '',
  type: 'STR',
  remark: '',
  items: [],
});

const formRules = computed<Record<string, FormRule[]>>(() => ({
  name: [{ required: true, message: t('pages.valueDict.create.nameRequired'), type: 'error' }],
  type: [{ required: true, message: t('pages.valueDict.create.typeRequired'), type: 'error' }],
  items: [
    {
      required: true,
      message: t('pages.valueDict.create.itemsRequired'),
      type: 'error',
      validator: (val: ValueDictItemModel[]) => val && val.length > 0,
    },
  ],
}));

// 加载详情数据
const loadDetail = async () => {
  if (!isEditing.value) return;
  
  try {
    loading.value = true;
    const id = Number(route.params.id);
    const detail = await getValueDictDetail(id);
    
    formData.value = {
      id: detail.id,
      name: detail.name,
      description: detail.description || '',
      type: detail.type,
      remark: detail.remark || '',
      items: detail.items || [],
    };
  } catch (error) {
    console.error('Failed to load value dict detail:', error);
    MessagePlugin.error('加载数据失败');
  } finally {
    loading.value = false;
  }
};

// 返回列表页
const goBack = () => {
  // 关闭当前标签
  const currentPath = route.path;
  const currentIndex = tabsRouterStore.tabRouters.findIndex(r => r.path === currentPath);
  if (currentIndex !== -1) {
    tabsRouterStore.subtractCurrentTabRouter({ path: currentPath, routeIdx: currentIndex });
  }
  // 返回列表页
  router.push({ name: 'ValueDictList' });
};

// 提交表单
const onSubmit = async ({ validateResult }: SubmitContext) => {
  if (validateResult !== true) return;

  try {
    submitLoading.value = true;
    if (isEditing.value) {
      await updateValueDict(formData.value.id!, formData.value);
      MessagePlugin.success(t('pages.valueDict.message.updateSuccess'));
    } else {
      await createValueDict(formData.value);
      MessagePlugin.success(t('pages.valueDict.message.createSuccess'));
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
  loadDetail();
});
</script>

<style scoped lang="less">
.value-dict-edit {
  padding: 20px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  padding-top: 24px;
  border-top: 1px solid var(--td-component-border);
}
</style>

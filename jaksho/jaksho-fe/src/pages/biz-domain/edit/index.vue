<template>
  <div class="biz-domain-edit-container">
    <t-card :bordered="false">
      <template #header>
        <div class="header-container">
          <t-button theme="default" @click="handleBack">{{ t('pages.bizDomain.edit.back') }}</t-button>
          <span>{{ isEdit ? t('pages.bizDomain.edit.title') : t('pages.bizDomain.create.title') }}</span>
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
        <div style="text-align: right; margin-top: 24px;">
          <t-button theme="primary" type="submit" :loading="submitLoading">
            {{ isEdit ? '保存' : '创建' }}
          </t-button>
        </div>
      </t-form>
    </t-card>
  </div>
</template>

<script setup lang="ts">
import type { FormInstanceFunctions, FormRule } from 'tdesign-vue-next';
import { MessagePlugin } from 'tdesign-vue-next';
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { createBizDomain, getBizDomainDetail, updateBizDomain } from '@/api/bizDomain';
import type { CreateBizDomainRequest } from '@/api/model/bizDomainModel';
import { t } from '@/locales';
import { useTabsRouterStore } from '@/store';

const route = useRoute();
const router = useRouter();
const tabsRouterStore = useTabsRouterStore();

const isEdit = computed(() => !!route.params.id);
const editingId = computed(() => (route.params.id ? Number(route.params.id) : undefined));

const formRef = ref<FormInstanceFunctions>();
const formData = ref<CreateBizDomainRequest>({
  name: '',
  description: '',
});

const submitLoading = ref(false);

// 表单验证规则
const formRules: Record<string, FormRule[]> = {
  name: [
    { required: true, message: t('pages.bizDomain.create.nameRequired'), type: 'error' },
    { min: 1, max: 100, message: t('pages.bizDomain.create.nameLength'), type: 'error' },
  ],
  description: [{ max: 255, message: t('pages.bizDomain.create.descriptionLength'), type: 'error' }],
};

// 加载详情（编辑模式）
const loadDetail = async () => {
  if (!editingId.value) return;

  try {
    const detail = await getBizDomainDetail(editingId.value);
    formData.value = {
      name: detail.name,
      description: detail.description || '',
    };
  } catch (error) {
    console.error('获取业务领域详情失败:', error);
    MessagePlugin.error(t('pages.bizDomain.edit.fetchFailed'));
  }
};

// 提交（创建或编辑）
const onSubmit = async () => {
  if (!formRef.value) return;

  const validateResult = await formRef.value.validate();
  if (validateResult === true) {
    submitLoading.value = true;
    try {
      if (isEdit.value) {
        await updateBizDomain(editingId.value!, formData.value);
        MessagePlugin.success(t('pages.bizDomain.edit.updateSuccess'));
      } else {
        await createBizDomain(formData.value);
        MessagePlugin.success(t('pages.bizDomain.create.createSuccess'));
      }
      handleBack();
    } catch (error) {
      console.error('操作失败:', error);
      MessagePlugin.error(isEdit.value ? t('pages.bizDomain.edit.updateFailed') : t('pages.bizDomain.create.createFailed'));
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
  router.push({ name: 'BizDomainList' });
};

onMounted(() => {
  if (isEdit.value) {
    loadDetail();
  }
});
</script>

<style lang="less" scoped>
.biz-domain-edit-container {
  padding: 24px;
}

.header-container {
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>

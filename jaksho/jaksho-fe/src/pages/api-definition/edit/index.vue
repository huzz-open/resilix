<template>
  <div class="api-definition-edit-container">
    <t-card :bordered="false">
      <template #header>
        <div class="header-container">
          <t-button theme="default" @click="handleBack">{{ t('pages.apiDefinition.edit.back') }}</t-button>
          <span>{{ isEdit ? t('pages.apiDefinition.drawer.titleDetail') : t('pages.apiDefinition.drawer.titleCreate') }}</span>
        </div>
      </template>

      <api-definition-drawer
        :mode="isEdit ? 'detail' : 'create'"
        :value="detailRecord"
        @success="onSuccess"
        @close="handleBack"
      />
    </t-card>
  </div>
</template>

<script setup lang="ts">
import { MessagePlugin } from 'tdesign-vue-next';
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { t } from '@/locales';
import { useTabsRouterStore } from '@/store';

import ApiDefinitionDrawer from '../components/ApiDefinitionDrawer.vue';

const route = useRoute();
const router = useRouter();
const tabsRouterStore = useTabsRouterStore();

const isEdit = computed(() => !!route.params.id);
const editingId = computed(() => (route.params.id ? Number(route.params.id) : undefined));

// 传递给子组件的数据：子组件的watch会根据id自动加载详情，避免重复调用接口
const detailRecord = computed(() => {
  return editingId.value ? { id: editingId.value } : null;
});

// 成功回调
const onSuccess = () => {
  MessagePlugin.success(isEdit.value ? t('pages.apiDefinition.edit.updateSuccess') : t('pages.apiDefinition.edit.createSuccess'));
  handleBack();
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
  router.push({ name: 'ApiDefinitionList' });
};

// 不需要在onMounted中加载详情，子组件ApiDefinitionDrawer的watch会自动加载
</script>

<style lang="less" scoped>
.api-definition-edit-container {
  padding: 24px;
}

.header-container {
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>

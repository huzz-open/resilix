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
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { getApiDefinitionDetail } from '@/api/apiDefinition';
import { t } from '@/locales';
import { useTabsRouterStore } from '@/store';

import ApiDefinitionDrawer from '../components/ApiDefinitionDrawer.vue';

const route = useRoute();
const router = useRouter();
const tabsRouterStore = useTabsRouterStore();

const isEdit = computed(() => !!route.params.id);
const editingId = computed(() => (route.params.id ? Number(route.params.id) : undefined));

const detailRecord = ref<any | null>(null);

// 加载详情（编辑模式）
const loadDetail = async () => {
  if (!editingId.value) return;

  try {
    detailRecord.value = await getApiDefinitionDetail(editingId.value);
  } catch (error) {
    console.error('获取API定义详情失败:', error);
    MessagePlugin.error(t('pages.apiDefinition.edit.fetchFailed'));
  }
};

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

onMounted(() => {
  if (isEdit.value) {
    loadDetail();
  }
});
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

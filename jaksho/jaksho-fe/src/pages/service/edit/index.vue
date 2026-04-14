<template>
  <div class="service-edit-container">
    <t-card :bordered="false">
      <template #header>
        <div class="header-container">
          <t-button theme="default" @click="handleBack">返回</t-button>
          <span>{{ isEdit ? '编辑服务' : '新建服务' }}</span>
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
        <t-form-item label="服务码" name="serviceCode">
          <t-input-number
            v-model="formData.serviceCode"
            :min="serviceCodeMinValue"
            :max="serviceCodeMaxValue"
            placeholder="请输入服务码"
            theme="normal"
            :disabled="isEdit"
          />
          <template #tips>
            <span v-if="isEdit">服务码不可编辑</span>
            <span v-else>服务码范围：{{ serviceCodeMinValue }} - {{ serviceCodeMaxValue }}（{{ serviceCodeLength }} 位数字）</span>
          </template>
        </t-form-item>
        <t-form-item label="服务名称" name="name">
          <t-input
            v-model="formData.name"
            placeholder="请输入服务名称"
            :maxlength="100"
            show-word-limit
          />
        </t-form-item>
        <t-form-item label="服务描述" name="description">
          <t-textarea
            v-model="formData.description"
            :height="120"
            placeholder="请输入服务描述"
            :maxlength="255"
            show-word-limit
          />
        </t-form-item>
        <t-form-item label="备注" name="remark">
          <t-textarea
            v-model="formData.remark"
            :height="80"
            placeholder="请输入备注"
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
import type { FormInstanceFunctions, SubmitContext } from 'tdesign-vue-next';
import { MessagePlugin } from 'tdesign-vue-next';
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { createService, getServiceDetail, updateService } from '@/api/service';
import { useTabsRouterStore } from '@/store';

const route = useRoute();
const router = useRouter();
const tabsRouterStore = useTabsRouterStore();

const isEdit = computed(() => !!route.params.id);
const editingId = computed(() => (route.params.id ? Number(route.params.id) : undefined));

// 从配置中获取服务码配置（可通过API获取，这里先硬编码）
const serviceCodeLength = ref(4);
const serviceCodeMinValue = ref(1000);
const serviceCodeMaxValue = ref(9999);

const formRef = ref<FormInstanceFunctions>();
const formData = ref<{
  serviceCode: number | undefined;
  name: string;
  description: string;
  remark: string;
}>({
  serviceCode: undefined,
  name: '',
  description: '',
  remark: '',
});

const submitLoading = ref(false);

// 动态表单验证规则
const formRules = computed(() => ({
  serviceCode: !isEdit.value
    ? [
        { required: true, message: '请输入服务码', type: 'error' as const },
        {
          validator: (val: number) => {
            return val >= serviceCodeMinValue.value && val <= serviceCodeMaxValue.value;
          },
          message: `服务码必须在 ${serviceCodeMinValue.value} - ${serviceCodeMaxValue.value} 之间`,
          type: 'error' as const,
        },
      ]
    : [],
  name: [
    { required: true, message: '请输入服务名称', type: 'error' as const },
    { max: 100, message: '服务名称最多100个字符', type: 'warning' as const },
  ],
  description: [{ max: 255, message: '服务描述最多255个字符', type: 'warning' as const }],
  remark: [{ max: 255, message: '备注最多255个字符', type: 'warning' as const }],
}));

// 加载详情（编辑模式）
const loadDetail = async () => {
  if (!editingId.value) return;

  try {
    const detail = await getServiceDetail(editingId.value);
    formData.value = {
      serviceCode: detail.serviceCode,
      name: detail.name,
      description: detail.description || '',
      remark: detail.remark || '',
    };
  } catch (error) {
    console.error('获取服务详情失败:', error);
    MessagePlugin.error('获取服务详情失败');
  }
};

// 提交（创建或编辑）
const onSubmit = async ({ validateResult }: SubmitContext) => {
  if (validateResult !== true) return;

  submitLoading.value = true;
  try {
    if (isEdit.value) {
      await updateService(editingId.value!, {
        name: formData.value.name,
        description: formData.value.description,
        remark: formData.value.remark,
      });
      MessagePlugin.success('更新成功');
    } else {
      if (formData.value.serviceCode === undefined) {
        MessagePlugin.warning('请输入服务码');
        return;
      }
      await createService({
        serviceCode: formData.value.serviceCode,
        name: formData.value.name,
        description: formData.value.description,
        remark: formData.value.remark,
      });
      MessagePlugin.success('创建成功');
    }
    handleBack();
  } catch (error) {
    console.error('操作失败:', error);
    MessagePlugin.error(isEdit.value ? '更新失败' : '创建失败');
  } finally {
    submitLoading.value = false;
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
  router.push({ name: 'ServiceList' });
};

onMounted(() => {
  if (isEdit.value) {
    loadDetail();
  }
});
</script>

<style lang="less" scoped>
.service-edit-container {
  padding: 24px;
}

.header-container {
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>

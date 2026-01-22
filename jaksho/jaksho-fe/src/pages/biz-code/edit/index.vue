<template>
  <div class="biz-code-edit-container">
    <t-card :bordered="false">
      <template #header>
        <div class="header-container">
          <t-button theme="default" @click="handleBack">返回</t-button>
          <span>{{ isEdit ? '编辑业务码' : '新建业务码' }}</span>
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
        <t-form-item label="服务" name="serviceId">
          <t-select
            v-model="formData.serviceId"
            placeholder="请选择服务"
            filterable
            :disabled="isEdit"
            @change="onServiceChange"
          >
            <t-option
              v-for="service in serviceList"
              :key="service.id"
              :value="service.id"
              :label="`${service.name} (${service.serviceCode})`"
            />
          </t-select>
          <template v-if="isEdit" #tips>
            <span>服务不可编辑</span>
          </template>
        </t-form-item>

        <t-form-item label="序号" name="sequenceNumber">
          <t-input-number
            v-model="formData.sequenceNumber"
            :min="1"
            placeholder="请输入序号"
            theme="normal"
            :disabled="isEdit || config.sequenceMode === 'auto'"
          />
          <template #tips>
            <span v-if="isEdit">序号不可编辑</span>
            <span v-else-if="config.sequenceMode === 'auto'">
              自动生成模式，当前序号：{{ formData.sequenceNumber }}
            </span>
            <span v-else>
              手动输入模式，序号位数：{{ config.sequenceLength }}
            </span>
          </template>
        </t-form-item>

        <t-form-item label="HTTP状态码" name="httpStatus">
          <t-input-number
            v-model="formData.httpStatus"
            :min="100"
            :max="599"
            placeholder="请输入HTTP状态码"
            theme="normal"
            :disabled="isEdit"
          />
          <template v-if="isEdit" #tips>
            <span>HTTP状态码不可编辑</span>
          </template>
        </t-form-item>

        <t-form-item label="简短描述" name="shortDesc">
          <t-input
            v-model="formData.shortDesc"
            placeholder="请输入简短描述"
            :maxlength="100"
            show-word-limit
          />
        </t-form-item>

        <t-form-item label="详细描述" name="detailDesc">
          <t-textarea
            v-model="formData.detailDesc"
            :height="120"
            placeholder="请输入详细描述"
            :maxlength="500"
            show-word-limit
          />
        </t-form-item>

        <t-form-item v-if="config.i18nEnabled" label="国际化Key" name="i18nKey">
          <t-input
            v-model="formData.i18nKey"
            placeholder="如：biz.code.user.not.found"
            :maxlength="100"
            :disabled="isEdit"
          />
          <template v-if="isEdit" #tips>
            <span>国际化Key不可编辑</span>
          </template>
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

        <t-form-item v-if="!isEdit && formData.serviceId && formData.sequenceNumber">
          <t-alert theme="info" message="业务码预览">
            <template #default>
              <div style="margin-top: 8px; font-size: 16px; font-weight: bold; color: #0052d9">
                {{ calculateBizCode() }}
              </div>
            </template>
          </t-alert>
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
import type { FormInstanceFunctions } from 'tdesign-vue-next';
import { MessagePlugin } from 'tdesign-vue-next';
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import {
  createBizCode,
  getBizCodeConfig,
  getBizCodeDetail,
  getNextSequence,
  updateBizCode,
} from '@/api/bizCode';
import type { BizCodeConfigModel } from '@/api/model/bizCodeModel';
import type { ServiceModel } from '@/api/model/serviceModel';
import { getServiceList } from '@/api/service';
import { useTabsRouterStore } from '@/store';

const route = useRoute();
const router = useRouter();
const tabsRouterStore = useTabsRouterStore();

const isEdit = computed(() => !!route.params.id);
const editingId = computed(() => (route.params.id ? Number(route.params.id) : undefined));

const formRef = ref<FormInstanceFunctions>();
const formData = ref<{
  serviceId: number | undefined;
  sequenceNumber: number | undefined;
  httpStatus: number | undefined;
  shortDesc: string;
  detailDesc: string;
  i18nKey: string;
  remark: string;
}>({
  serviceId: undefined,
  sequenceNumber: undefined,
  httpStatus: undefined,
  shortDesc: '',
  detailDesc: '',
  i18nKey: '',
  remark: '',
});

const submitLoading = ref(false);
const serviceList = ref<ServiceModel[]>([]);
const config = ref<BizCodeConfigModel>({
  serviceCodeLength: 4,
  sequenceLength: 8,
  sequenceMode: 'auto',
  i18nEnabled: true,
  defaultLocale: 'zh_CN',
});

// 动态表单验证规则
const formRules = computed(() => ({
  serviceId: !isEdit.value
    ? [{ required: true, message: '请选择服务', type: 'error' as const }]
    : [],
  sequenceNumber:
    !isEdit.value && config.value.sequenceMode === 'manual'
      ? [{ required: true, message: '请输入序号', type: 'error' as const }]
      : [],
  httpStatus: !isEdit.value
    ? [
        { required: true, message: '请输入HTTP状态码', type: 'error' as const },
        { min: 100, max: 599, message: 'HTTP状态码范围：100-599', type: 'error' as const },
      ]
    : [],
  shortDesc: [
    { required: true, message: '请输入简短描述', type: 'error' as const },
    { max: 100, message: '简短描述最多100个字符', type: 'warning' as const },
  ],
  detailDesc: [{ max: 500, message: '详细描述最多500个字符', type: 'warning' as const }],
  i18nKey: [{ max: 100, message: '国际化Key最多100个字符', type: 'warning' as const }],
  remark: [{ max: 255, message: '备注最多255个字符', type: 'warning' as const }],
}));

// 获取服务列表
const fetchServiceList = async () => {
  try {
    const { rows } = await getServiceList({
      current: 1,
      pageSize: 1000,
    });
    serviceList.value = rows;
  } catch (error) {
    console.error('获取服务列表失败:', error);
    MessagePlugin.error('获取服务列表失败');
  }
};

// 获取配置
const fetchConfig = async () => {
  try {
    config.value = await getBizCodeConfig();
  } catch (error) {
    console.error('获取配置失败:', error);
  }
};

// 加载详情（编辑模式）
const loadDetail = async () => {
  if (!editingId.value) return;

  try {
    const detail = await getBizCodeDetail(editingId.value);
    const bizCodeData = (detail as any)?.bizCode || detail;
    formData.value = {
      serviceId: bizCodeData.serviceId,
      sequenceNumber: bizCodeData.sequenceNumber,
      httpStatus: bizCodeData.httpStatus,
      shortDesc: bizCodeData.shortDesc,
      detailDesc: bizCodeData.detailDesc || '',
      i18nKey: bizCodeData.i18nKey || '',
      remark: bizCodeData.remark || '',
    };
  } catch (error) {
    console.error('获取业务码详情失败:', error);
    MessagePlugin.error('获取业务码详情失败');
  }
};

// 服务变化时获取下一个序号
const onServiceChange = async (serviceId: number) => {
  if (config.value.sequenceMode === 'auto' && serviceId) {
    try {
      const nextSeq = await getNextSequence(serviceId);
      formData.value.sequenceNumber = nextSeq;
    } catch (error) {
      console.error('获取下一个序号失败:', error);
      MessagePlugin.error('获取下一个序号失败');
    }
  }
};

// 计算业务码
const calculateBizCode = (): string => {
  if (!formData.value.serviceId || !formData.value.sequenceNumber) {
    return '-';
  }
  const service = serviceList.value.find((s) => s.id === formData.value.serviceId);
  if (!service) {
    return '-';
  }
  const multiplier = Math.pow(10, config.value.sequenceLength);
  const code = service.serviceCode * multiplier + formData.value.sequenceNumber;
  return code.toString();
};

// 提交（创建或编辑）
const onSubmit = async ({ validateResult }: { validateResult: boolean }) => {
  if (!validateResult) return;

  submitLoading.value = true;
  try {
    if (isEdit.value) {
      await updateBizCode(editingId.value!, {
        shortDesc: formData.value.shortDesc,
        detailDesc: formData.value.detailDesc,
        remark: formData.value.remark,
      });
      MessagePlugin.success('更新成功');
    } else {
      await createBizCode({
        serviceId: formData.value.serviceId!,
        sequenceNumber:
          config.value.sequenceMode === 'manual' ? formData.value.sequenceNumber : undefined,
        httpStatus: formData.value.httpStatus!,
        shortDesc: formData.value.shortDesc,
        detailDesc: formData.value.detailDesc,
        i18nKey: formData.value.i18nKey,
        remark: formData.value.remark,
      });
      MessagePlugin.success('创建成功');
    }
    handleBack();
  } catch (error: any) {
    console.error('操作失败:', error);
    MessagePlugin.error(error.message || (isEdit.value ? '更新失败' : '创建失败'));
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
  router.push({ name: 'BizCodeList' });
};

onMounted(async () => {
  await fetchConfig();
  await fetchServiceList();
  if (isEdit.value) {
    await loadDetail();
  }
});
</script>

<style lang="less" scoped>
.biz-code-edit-container {
  padding: 24px;
}

.header-container {
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>

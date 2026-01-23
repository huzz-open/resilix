<template>
  <div class="api-output-config">
    <t-alert theme="info" message="选择响应字段来定义响应体结构，推荐使用字段属性为【可作为输出】的字段" closable />
    
    <div style="margin-top: 16px;">
      <j-tree-data
        ref="jTreeRef"
        v-model:tree-dto-list="responseTreeDTO"
        :fetch-page="fetchBizFieldDomainPage"
        :columns="responseFieldColumns"
        row-key="bizFieldDomain.id"
        selection="multiple"
        custom-field="bizFieldDomainId"
        :get-custom-value="(row: any) => row.bizFieldDomain.id"
        :default-page-size="10"
      />
    </div>

    <t-collapse style="margin-top: 16px;" :default-value="[]">
      <t-collapse-panel header="使用提示" value="tips">
        <ul style="margin: 0; padding-left: 20px; font-size: 13px; color: var(--td-text-color-secondary);">
          <li>在字段管理中创建字段时，可设置"字段属性"为"可作为输出"，这样的字段会自动标记为响应字段</li>
          <li>如果需要创建同名但不同类型的字段（如 data 字段既有 STRING 类型又有 SLOT 类型），可以使用"添加领域字段"功能，在"插槽领域"下创建 SLOT 类型版本</li>
          <li>SLOT 类型字段可作为占位符使用，便于在不同接口中灵活映射到不同的实际字段</li>
          <li>响应字段支持 OBJECT 类型，可以定义嵌套的响应结构</li>
        </ul>
      </t-collapse-panel>
    </t-collapse>
  </div>
</template>

<script setup lang="ts">
import type { PrimaryTableCol } from 'tdesign-vue-next';
import { ref, watch } from 'vue';

import { getBizFieldDomainList } from '@/api/bizFieldDomain';
import JTreeData from '@/components/j-tree-data/index.vue';
import { t } from '@/locales';

interface Props {
  modelValue?: any[];
  workspaceId?: number;
}

interface Emits {
  (e: 'update:modelValue', value: any[]): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const jTreeRef = ref<InstanceType<typeof JTreeData>>();
const responseTreeDTO = ref<any[]>([]);

// 响应字段列配置
const responseFieldColumns: PrimaryTableCol[] = [
  { title: t('pages.apiDefinition.drawer.fieldName'), colKey: 'bizField.name', width: 200 },
  { title: t('pages.apiDefinition.drawer.fieldType'), colKey: 'bizFieldType.name', width: 200 },
  { title: t('pages.apiDefinition.drawer.fieldDescription'), colKey: 'bizField.description', ellipsis: true },
];

// 获取字段领域分页数据（获取所有字段，组件内会根据 fieldAttributes 进行位运算筛选）
async function fetchBizFieldDomainPage(params: any) {
  const res = await getBizFieldDomainList({
    ...params,
    workspaceId: props.workspaceId,
  });
  
  // 前端过滤：只显示可作为输出的字段（fieldAttributes & 2 > 0）
  if (res && res.rows) {
    res.rows = res.rows.filter((row: any) => {
      const attrs = row?.bizFieldDomain?.fieldAttributes || 3; // 默认为3（输入输出均可）
      return (attrs & 2) > 0; // 检查是否包含输出位
    });
  }
  
  return res;
}

// 监听 tree 数据变化，同步到父组件
watch(
  () => responseTreeDTO.value,
  (newVal) => {
    emit('update:modelValue', newVal);
  },
  { deep: true },
);

// 监听父组件传入的值，回显到树组件
watch(
  () => props.modelValue,
  (newVal) => {
    console.log('ApiOutputConfig 接收到的 modelValue:', newVal);
    if (newVal && JSON.stringify(newVal) !== JSON.stringify(responseTreeDTO.value)) {
      responseTreeDTO.value = newVal;
      console.log('ApiOutputConfig 已更新 responseTreeDTO:', responseTreeDTO.value);
    }
  },
  { immediate: true, deep: true },
);
</script>

<style scoped lang="less">
.api-output-config {
  padding: 16px 0;
}
</style>

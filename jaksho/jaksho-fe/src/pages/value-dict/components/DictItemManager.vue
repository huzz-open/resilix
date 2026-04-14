<template>
  <div class="dict-item-manager">
    <t-button theme="primary" @click="handleAdd" style="margin-bottom: 12px">
      <template #icon><add-icon /></template>
      {{ t('pages.valueDict.item.addItem') }}
    </t-button>

    <t-table
      :data="items"
      :columns="columns"
      row-key="tempId"
      bordered
      hover
      :loading="loading"
      drag-sort="row"
      @drag-sort="handleDragSort"
    >
      <template #name="{ row, rowIndex }">
        <t-input
          v-model="row.name"
          :placeholder="t('pages.valueDict.item.namePlaceholder')"
          :disabled="!row.editing"
          :status="row.editing && row.name && !validateEnumName(row.name) ? 'error' : undefined"
          :tips="row.editing && row.name && !validateEnumName(row.name) ? '只能以字母或下划线开头，后续可包含字母、数字、下划线' : ''"
        />
      </template>

      <template #rawValue="{ row }">
        <t-input
          v-model="row.rawValue"
          :placeholder="getRawValuePlaceholder(dictType)"
          :disabled="!row.editing"
          :status="row.editing && row.rawValue && !validateRawValue(row.rawValue, dictType) ? 'error' : undefined"
          :tips="row.editing && row.rawValue && !validateRawValue(row.rawValue, dictType) ? getValidationErrorTips(dictType) : ''"
        />
      </template>

      <template #description="{ row }">
        <t-input
          v-model="row.description"
          :placeholder="t('pages.valueDict.item.descriptionPlaceholder')"
          :disabled="!row.editing"
        />
      </template>

      <template #op="{ row, rowIndex }">
        <t-space>
          <t-link
            v-if="!row.editing"
            theme="primary"
            @click="handleEdit(rowIndex)"
          >
            编辑
          </t-link>
          <t-link
            v-else
            theme="success"
            @click="handleSave(rowIndex)"
          >
            保存
          </t-link>
          <t-popconfirm
            :content="t('pages.valueDict.item.deleteConfirm')"
            @confirm="handleDelete(rowIndex)"
          >
            <t-link theme="danger">{{ t('pages.valueDict.item.deleteItem') }}</t-link>
          </t-popconfirm>
        </t-space>
      </template>
    </t-table>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { AddIcon } from 'tdesign-icons-vue-next';
import type { ValueDictItemModel } from '@/api/model/valueDictModel';

const props = defineProps<{
  modelValue: ValueDictItemModel[];
  dictType?: string; // 字典类型：STR/INT/CHAR/FLOAT
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: ValueDictItemModel[]): void;
}>();

const { t } = useI18n();

const loading = ref(false);
const items = ref<Array<ValueDictItemModel & { editing?: boolean; tempId?: string }>>([]);
const isInternalUpdate = ref(false); // 标记是否为内部更新

// 初始化数据
watch(() => props.modelValue, (newVal) => {
  // 如果是内部触发的更新，跳过处理
  if (isInternalUpdate.value) {
    isInternalUpdate.value = false;
    return;
  }
  
  items.value = newVal.map((item, index) => ({
    ...item,
    editing: false,
    tempId: item.id ? `id-${item.id}` : `temp-${Date.now()}-${index}`,
  }));
}, { immediate: true });

// 同步数据到父组件的辅助方法（自动根据数组索引设置 sortOrder）
const syncToParent = () => {
  isInternalUpdate.value = true;
  const result = items.value.map(({ editing, tempId, ...item }, index) => ({
    ...item,
    sortOrder: index, // 根据数组索引自动设置排序
  }));
  emit('update:modelValue', result);
};

// 处理拖拽排序
const handleDragSort = ({ currentIndex, targetIndex }: { currentIndex: number; targetIndex: number }) => {
  const temp = items.value[currentIndex];
  items.value.splice(currentIndex, 1);
  items.value.splice(targetIndex, 0, temp);
  syncToParent();
};

// 验证枚举名称（只能以字母或下划线开头，后续可以包含字母、数字、下划线）
const validateEnumName = (name: string): boolean => {
  const pattern = /^[a-zA-Z_][a-zA-Z0-9_]*$/;
  return pattern.test(name);
};

// 验证原始值（根据字典类型）
const validateRawValue = (value: string, type?: string): boolean => {
  if (!value || !type) return true;
  
  switch (type) {
    case 'INT':
      return /^-?\d+$/.test(value); // 整数（可以有负号）
    case 'FLOAT':
      return /^-?\d+(\.\d+)?$/.test(value); // 浮点数
    case 'CHAR':
      return value.length === 1; // 单个字符
    case 'STR':
      return true; // 字符串无限制
    default:
      return true;
  }
};

// 获取原始值输入提示
const getRawValuePlaceholder = (type?: string): string => {
  switch (type) {
    case 'INT':
      return '如：1, -100';
    case 'FLOAT':
      return '如：1.0, -3.14';
    case 'CHAR':
      return '如：A, 1（单个字符）';
    case 'STR':
    default:
      return t('pages.valueDict.item.rawValuePlaceholder');
  }
};

// 获取验证错误提示
const getValidationErrorTips = (type?: string): string => {
  switch (type) {
    case 'INT':
      return '请输入整数，如：1, -100';
    case 'FLOAT':
      return '请输入浮点数，如：1.0, -3.14';
    case 'CHAR':
      return '只能输入单个字符';
    case 'STR':
    default:
      return '输入格式不正确';
  }
};

const columns = computed(() => [
  {
    colKey: 'name',
    title: t('pages.valueDict.item.name'),
    width: 200, // 增加宽度
  },
  {
    colKey: 'rawValue',
    title: t('pages.valueDict.item.rawValue'),
    width: 180, // 增加宽度
  },
  {
    colKey: 'description',
    title: t('pages.valueDict.item.description'),
    width: 250, // 固定宽度，不要太宽
    ellipsis: true,
  },
  {
    colKey: 'op',
    title: t('pages.valueDict.list.operation'),
    width: 150,
    fixed: 'right' as const,
  },
]);

const handleAdd = () => {
  items.value.push({
    name: '',
    rawValue: '',
    description: '',
    sortOrder: items.value.length,
    editing: true,
    tempId: `temp-${Date.now()}`,
  });
  syncToParent();
};

const handleEdit = (index: number) => {
  items.value[index].editing = true;
};

const handleSave = (index: number) => {
  const item = items.value[index];
  if (!item.name || !item.rawValue) {
    return;
  }
  
  // 验证枚举名称
  if (!validateEnumName(item.name)) {
    return;
  }
  
  // 验证原始值
  if (!validateRawValue(item.rawValue, props.dictType)) {
    return;
  }
  
  item.editing = false;
  syncToParent();
};

const handleDelete = (index: number) => {
  items.value.splice(index, 1);
  syncToParent();
};
</script>

<style scoped lang="less">
.dict-item-manager {
  width: 100%;
}
</style>

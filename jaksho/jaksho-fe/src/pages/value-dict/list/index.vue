<template>
  <div>
    <t-card class="list-card-container" :bordered="false">
      <t-row justify="space-between">
        <div class="left-operation-container">
          <t-button @click="handleCreate">{{ t('pages.valueDict.list.createButton') }}</t-button>
        </div>
        <div class="search-input">
          <t-input v-model="searchValue" :placeholder="t('pages.valueDict.list.searchPlaceholder')" clearable>
            <template #suffix-icon>
              <search-icon size="16px" />
            </template>
          </t-input>
        </div>
      </t-row>

      <t-table
        :data="listData"
        :columns="columns"
        row-key="id"
        :hover="true"
        :pagination="pagination"
        :loading="dataLoading"
        @page-change="handlePageChange"
      >
        <template #type="{ row }">
          <t-tag :theme="getTypeTheme(row.type)">{{ getTypeLabel(row.type) }}</t-tag>
        </template>

        <template #createTime="{ row }">
          {{ formatTime(row.createTime) }}
        </template>

        <template #op="{ row }">
          <t-space>
            <t-link theme="primary" @click="handleEdit(row)">编辑</t-link>
            <t-popconfirm
              :content="t('pages.valueDict.list.deleteConfirm')"
              @confirm="handleDelete(row)"
            >
              <t-link theme="danger">删除</t-link>
            </t-popconfirm>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <!-- 创建/编辑对话框 -->
    <t-dialog
      v-model:visible="dialogVisible"
      :header="isEditing ? t('pages.valueDict.create.editTitle') : t('pages.valueDict.create.title')"
      width="70%"
      :footer="false"
      destroy-on-close
    >
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
              />
              <template #tips>{{ t('pages.valueDict.create.nameHelp') }}</template>
            </t-form-item>
          </t-col>
          <t-col :span="6">
            <t-form-item :label="t('pages.valueDict.create.typeLabel')" name="type">
              <t-select v-model="formData.type" :placeholder="t('pages.valueDict.create.typeRequired')">
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

        <div class="dialog-footer">
          <t-space>
            <t-button theme="default" @click="onCancel">{{ t('pages.valueDict.create.cancelButton') }}</t-button>
            <t-button theme="primary" type="submit" :loading="submitLoading">
              {{ t('pages.valueDict.create.submitButton') }}
            </t-button>
          </t-space>
        </div>
      </t-form>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { SearchIcon } from 'tdesign-icons-vue-next';
import { MessagePlugin } from 'tdesign-vue-next';
import type { FormInstanceFunctions, FormRule, PaginationProps } from 'tdesign-vue-next';
import { getValueDictList, createValueDict, updateValueDict, deleteValueDict, getValueDictDetail } from '@/api/valueDict';
import type { ValueDictModel, ValueDictItemModel } from '@/api/model/valueDictModel';
import DictItemManager from '../components/DictItemManager.vue';

const { t } = useI18n();

// 列表数据
const listData = ref<ValueDictModel[]>([]);
const dataLoading = ref(false);
const searchValue = ref('');

// 分页
const pagination = ref<PaginationProps>({
  current: 1,
  pageSize: 10,
  total: 0,
});

// 对话框
const dialogVisible = ref(false);
const isEditing = ref(false);
const formRef = ref<FormInstanceFunctions>();
const submitLoading = ref(false);

interface FormData {
  id?: number;
  name: string;
  description: string;
  type: string;
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

// 表格列
const columns = computed(() => [
  { colKey: 'id', title: 'ID', width: 80 },
  { colKey: 'name', title: t('pages.valueDict.list.name'), ellipsis: true },
  { colKey: 'description', title: t('pages.valueDict.list.description'), ellipsis: true },
  { colKey: 'type', title: t('pages.valueDict.list.type'), width: 100 },
  { colKey: 'createTime', title: t('pages.valueDict.list.createTime'), width: 180 },
  { colKey: 'op', title: t('pages.valueDict.list.operation'), width: 150, fixed: 'right' },
]);

// 加载列表数据
const loadData = async () => {
  try {
    dataLoading.value = true;
    const params = {
      current: pagination.value.current!,
      pageSize: pagination.value.pageSize!,
      name: searchValue.value || undefined,
    };
    const res = await getValueDictList(params);
    listData.value = res.rows || res.records || [];
    pagination.value.total = res.total || 0;
  } catch (error) {
    console.error('Failed to load value dict list:', error);
    MessagePlugin.error('加载数据失败');
  } finally {
    dataLoading.value = false;
  }
};

// 监听搜索值变化
watch(searchValue, () => {
  pagination.value.current = 1;
  loadData();
});

// 分页变化
const handlePageChange = (pageInfo: PaginationProps) => {
  pagination.value.current = pageInfo.current;
  pagination.value.pageSize = pageInfo.pageSize;
  loadData();
};

// 创建
const handleCreate = () => {
  isEditing.value = false;
  formData.value = {
    name: '',
    description: '',
    type: 'STR',
    remark: '',
    items: [],
  };
  dialogVisible.value = true;
};

// 编辑
const handleEdit = async (row: ValueDictModel) => {
  try {
    dataLoading.value = true;
    // 先加载详情（包括 items）
    const detail = await getValueDictDetail(row.id);
    
    isEditing.value = true;
    formData.value = {
      id: detail.id,
      name: detail.name,
      description: detail.description || '',
      type: detail.type,
      remark: detail.remark || '',
      items: detail.items || [],
    };
    dialogVisible.value = true;
  } catch (error) {
    console.error('Failed to load value dict detail:', error);
    MessagePlugin.error('加载字典详情失败');
  } finally {
    dataLoading.value = false;
  }
};

// 提交
const onSubmit = async ({ validateResult }: { validateResult: boolean }) => {
  if (!validateResult) return;

  try {
    submitLoading.value = true;
    if (isEditing.value) {
      await updateValueDict(formData.value.id!, formData.value);
      MessagePlugin.success(t('pages.valueDict.message.updateSuccess'));
    } else {
      await createValueDict(formData.value);
      MessagePlugin.success(t('pages.valueDict.message.createSuccess'));
    }
    dialogVisible.value = false;
    loadData();
  } catch (error) {
    console.error('Failed to submit:', error);
    MessagePlugin.error('操作失败');
  } finally {
    submitLoading.value = false;
  }
};

// 取消
const onCancel = () => {
  dialogVisible.value = false;
};

// 删除
const handleDelete = async (row: ValueDictModel) => {
  try {
    await deleteValueDict(row.id);
    MessagePlugin.success(t('pages.valueDict.message.deleteSuccess'));
    loadData();
  } catch (error) {
    console.error('Failed to delete:', error);
    MessagePlugin.error('删除失败');
  }
};

// 辅助函数
const getTypeTheme = (type: string) => {
  const themeMap: Record<string, string> = {
    STR: 'primary',
    INT: 'success',
    CHAR: 'warning',
    FLOAT: 'danger',
  };
  return themeMap[type] || 'default';
};

const getTypeLabel = (type: string) => {
  return t(`pages.valueDict.create.type${type.charAt(0) + type.slice(1).toLowerCase()}`);
};

const formatTime = (time?: string) => {
  if (!time) return '-';
  return new Date(time).toLocaleString('zh-CN');
};

onMounted(() => {
  loadData();
});
</script>

<style scoped lang="less">
.list-card-container {
  padding: 20px;

  .left-operation-container {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .search-input {
    width: 360px;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
}
</style>

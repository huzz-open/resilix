<template>
  <div>
    <t-card class="list-card-container" :bordered="false">
      <t-row justify="space-between">
        <div class="left-operation-container">
          <t-button @click="goToCreate">{{ t('pages.valueDict.list.createButton') }}</t-button>
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
            <t-link theme="primary" @click="goToEdit(row.id)">编辑</t-link>
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
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { SearchIcon } from 'tdesign-icons-vue-next';
import { MessagePlugin } from 'tdesign-vue-next';
import type { PaginationProps } from 'tdesign-vue-next';
import { getValueDictList, deleteValueDict } from '@/api/valueDict';
import type { ValueDictModel } from '@/api/model/valueDictModel';

const { t } = useI18n();
const router = useRouter();

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


// 表格列
const columns = computed(() => [
  { colKey: 'id', title: 'ID', width: 80 },
  { colKey: 'name', title: t('pages.valueDict.list.name'), ellipsis: true },
  { colKey: 'description', title: t('pages.valueDict.list.description'), ellipsis: true },
  { colKey: 'type', title: t('pages.valueDict.list.type'), width: 100 },
  { colKey: 'createTime', title: t('pages.valueDict.list.createTime'), width: 180 },
  { colKey: 'op', title: t('pages.valueDict.list.operation'), width: 150, fixed: 'right' as const },
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

// 路由跳转到创建页面
const goToCreate = () => {
  router.push({ name: 'ValueDictCreate' });
};

// 路由跳转到编辑页面
const goToEdit = (id: number) => {
  router.push({ name: 'ValueDictEdit', params: { id } });
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
type TagTheme = 'default' | 'success' | 'primary' | 'warning' | 'danger';

const getTypeTheme = (type: string): TagTheme => {
  const themeMap: Record<string, TagTheme> = {
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

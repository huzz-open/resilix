<template>
  <div>
    <t-card class="list-card-container" :bordered="false">
      <t-row justify="space-between">
        <div class="left-operation-container">
          <t-button @click="handleCreate">{{ t('pages.bizField.list.create') }}</t-button>
          <p v-if="!!selectedRowKeys.length" class="selected-count">
            {{ t('pages.bizField.list.selectedCount', { count: selectedRowKeys.length }) }}
          </p>
        </div>
        <div class="search-input">
          <t-input v-model="searchValue" :placeholder="t('pages.bizField.list.searchPlaceholder')" clearable>
            <template #suffix-icon>
              <search-icon size="16px" />
            </template>
          </t-input>
        </div>
      </t-row>
      <t-table
        :data="listData"
        :columns="COLUMNS"
        row-key="bizField.id"
        vertical-align="top"
        :hover="true"
        :pagination="pagination"
        :selected-row-keys="selectedRowKeys"
        :loading="dataLoading"
        :header-affixed-top="headerAffixedTop"
        @page-change="rehandlePageChange"
        @change="rehandleChange"
        @select-change="(value: (string | number)[]) => rehandleSelectChange(value)"
      >
        <template #op="slotProps">
          <t-space>
            <t-link theme="primary" @click="goToDetail(slotProps.row?.bizField?.id ?? slotProps.row?.id)">{{ t('pages.bizField.list.edit') }}</t-link>
            <t-link theme="danger" @click="handleClickDelete(slotProps)">{{ t('pages.bizField.list.delete') }}</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <t-dialog
      v-model:visible="confirmVisible"
      :header="t('pages.bizField.list.confirmDelete')"
      :body="confirmBody"
      :on-cancel="onCancel"
      @confirm="onConfirmDelete"
    />
  </div>
</template>
<script setup lang="ts">
import { SearchIcon } from 'tdesign-icons-vue-next';
import type { PageInfo, PrimaryTableCol } from 'tdesign-vue-next';
import { MessagePlugin, Tooltip } from 'tdesign-vue-next';
import { computed, h, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import { deleteBizField, getBizFieldList } from '@/api/bizField';
import { prefix } from '@/config/global';
import { t } from '@/locales';
import { useSettingStore } from '@/store';

const router = useRouter();

defineOptions({
  name: 'BizFieldList',
});

const store = useSettingStore();

const COLUMNS: PrimaryTableCol[] = [
  { colKey: 'row-select', type: 'multiple', width: 64, fixed: 'left' },
  { title: t('pages.bizField.list.name'), align: 'left', width: 200, colKey: 'bizField.name' },
  { title: t('pages.bizField.list.description'), width: 300, ellipsis: true, colKey: 'bizField.description' },
  {
    title: t('pages.bizField.list.bizFieldTypeName'),
    width: 200,
    colKey: 'bizFieldType.name',
    ellipsis: true,
    cell(h, { row }) {
      const name = row?.bizFieldType?.name;
      const desc = row?.bizFieldType?.description;
      if (!name) return name;
      return h(Tooltip, { content: desc || '' }, { default: () => name });
    },
  },
  { title: t('pages.bizField.list.basicFieldType'), width: 140, colKey: 'bizFieldType.basicFieldType' },
  { title: t('pages.bizField.list.collectionType'), width: 140, colKey: 'bizFieldType.collectionType' },
  { title: t('pages.bizField.list.minimum'), width: 140, colKey: 'bizFieldType.minimum' },
  { title: t('pages.bizField.list.maximum'), width: 140, colKey: 'bizFieldType.maximum' },
  { title: t('pages.bizField.list.createTime'), width: 180, colKey: 'bizField.createTime' },
  { title: t('pages.bizField.list.updateTime'), width: 180, colKey: 'bizField.updateTime' },
  { title: t('pages.bizField.list.operation'), align: 'left', fixed: 'right', width: 260, colKey: 'op' },
];

const listData = ref<any[]>([]);
const pagination = ref({ pageSize: 20, total: 0, current: 1 });

const searchValue = ref('');
const dataLoading = ref(false);

// 跳转到详情/编辑页面
const goToDetail = (id?: number) => {
  if (id) {
    router.push({ name: 'BizFieldEdit', params: { id } });
  }
};

// 跳转到创建页面
const handleCreate = () => {
  router.push({ name: 'BizFieldCreate' });
};

const fetchData = async () => {
  dataLoading.value = true;
  try {
    const { rows, total } = await getBizFieldList({
      current: pagination.value.current,
      pageSize: pagination.value.pageSize,
    });
    listData.value = rows;
    pagination.value = { ...pagination.value, total };
  } catch (e) {
    console.log(e);
    await MessagePlugin.error(t('pages.bizField.list.fetchFailed'));
  } finally {
    dataLoading.value = false;
  }
};

const deleteIdx = ref(-1);
const confirmBody = computed(() => {
  if (deleteIdx.value > -1) {
    const { name } = listData.value[deleteIdx.value];
    return t('pages.bizField.list.confirmDeleteBody', { name });
  }
  return '';
});

onMounted(() => {
  fetchData();
});

const confirmVisible = ref(false);
const selectedRowKeys = ref<(string | number)[]>([]);

const resetIdx = () => {
  deleteIdx.value = -1;
};

const onConfirmDelete = async () => {
  try {
    const id = listData.value[deleteIdx.value]?.bizField?.id ?? listData.value[deleteIdx.value]?.id;
    if (id == null) return;
    await deleteBizField(id);
    await MessagePlugin.success(t('pages.bizField.list.deleteSuccess'));
    confirmVisible.value = false;
    resetIdx();
    await fetchData();
  } catch (e) {
    console.error(e);
  }
};

const onCancel = () => {
  resetIdx();
};
const rehandleSelectChange = (val: (string | number)[]) => {
  selectedRowKeys.value = val;
};

const rehandlePageChange = (pageInfo: PageInfo, newDataSource: any) => {
  pagination.value.current = pageInfo.current;
  pagination.value.pageSize = pageInfo.pageSize;
  console.log('分页变化', newDataSource);
  fetchData();
};

const rehandleChange = (changeParams: unknown, triggerAndData: unknown) => {
  console.log('统一Change', changeParams, triggerAndData);
};

const handleClickDelete = (row: { rowIndex: any }) => {
  deleteIdx.value = row.rowIndex;
  confirmVisible.value = true;
};

const headerAffixedTop = computed(
  () =>
    ({
      offsetTop: store.isUseTabsRouter ? 48 : 0,
      container: `.${prefix}-layout`,
    }) as any,
);
</script>
<style lang="less" scoped>
.list-card-container {
  padding: var(--td-comp-paddingTB-xxl) var(--td-comp-paddingLR-xxl);

  :deep(.t-card__body) {
    padding: 0;
  }
}

.left-operation-container {
  display: flex;
  align-items: center;
  margin-bottom: var(--td-comp-margin-xxl);

  .selected-count {
    display: inline-block;
    margin-left: var(--td-comp-margin-l);
    color: var(--td-text-color-secondary);
  }
}

.search-input {
  width: 360px;
}

.dialog-footer {
  margin-top: var(--td-comp-margin-l);
  text-align: right;
}
</style>

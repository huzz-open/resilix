<template>
  <div>
    <t-card class="list-card-container" :bordered="false">
      <t-row justify="space-between">
        <div class="left-operation-container">
          <t-button @click="handleCreate">{{ t('pages.bizDomain.list.create') }}</t-button>
          <p v-if="!!selectedRowKeys.length" class="selected-count">
            {{ t('pages.bizDomain.list.selectedCount', { count: selectedRowKeys.length }) }}
          </p>
        </div>
        <div class="search-input">
          <t-input v-model="searchValue" :placeholder="t('pages.bizDomain.list.searchPlaceholder')" clearable>
            <template #suffix-icon>
              <search-icon size="16px" />
            </template>
          </t-input>
        </div>
      </t-row>
      <t-table
        :data="listData"
        :columns="COLUMNS"
        :row-key="rowKey"
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
            <t-link theme="primary" @click="goToEdit(slotProps.row.id)"
              >{{ t('pages.bizDomain.list.edit') }}
            </t-link>
            <t-link theme="danger" @click="handleClickDelete(slotProps)">{{ t('pages.bizDomain.list.delete') }}</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <t-dialog
      v-model:visible="confirmVisible"
      :header="t('pages.bizDomain.list.confirmDelete')"
      :body="confirmBody"
      :on-cancel="onCancel"
      @confirm="onConfirmDelete"
    />
  </div>
</template>
<script setup lang="ts">
import { SearchIcon } from 'tdesign-icons-vue-next';
import type { PageInfo, PrimaryTableCol } from 'tdesign-vue-next';
import { MessagePlugin } from 'tdesign-vue-next';
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import { deleteBizDomain, getBizDomainList } from '@/api/bizDomain';
import { prefix } from '@/config/global';
import { t } from '@/locales';
import { useSettingStore } from '@/store';

const router = useRouter();

defineOptions({
  name: 'BizDomainList',
});

const store = useSettingStore();

const COLUMNS: PrimaryTableCol[] = [
  { colKey: 'row-select', type: 'multiple', width: 64, fixed: 'left' },
  {
    title: 'ID',
    align: 'left',
    width: 80,
    colKey: 'id',
    fixed: 'left',
  },
  {
    title: t('pages.bizDomain.list.name'),
    align: 'left',
    width: 200,
    colKey: 'name',
  },
  {
    title: t('pages.bizDomain.list.description'),
    width: 300,
    ellipsis: true,
    colKey: 'description',
  },
  {
    title: t('pages.bizDomain.list.createTime'),
    width: 180,
    colKey: 'createTime',
  },
  {
    title: t('pages.bizDomain.list.updateTime'),
    width: 180,
    colKey: 'updateTime',
  },
  {
    title: t('pages.bizDomain.list.operation'),
    align: 'left',
    fixed: 'right',
    width: 160,
    colKey: 'op',
  },
];

const listData = ref([]);
const pagination = ref({
  pageSize: 20,
  total: 0,
  current: 1,
});

const searchValue = ref('');
const dataLoading = ref(false);

const fetchData = async () => {
  dataLoading.value = true;
  try {
    const { rows, total } = await getBizDomainList({
      current: pagination.value.current,
      pageSize: pagination.value.pageSize,
    });
    // 设置表格数据
    listData.value = rows;
    // 设置分页总数
    pagination.value = {
      ...pagination.value,
      total,
    };
  } catch (e) {
    console.log(e);
    await MessagePlugin.error(t('pages.bizDomain.list.fetchFailed'));
  } finally {
    dataLoading.value = false;
  }
};

const deleteIdx = ref(-1);
const confirmBody = computed(() => {
  if (deleteIdx.value > -1) {
    const { name } = listData.value[deleteIdx.value];
    return `删除后，${name}的所有信息将被清空，且无法恢复`;
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
    const id = (listData.value[deleteIdx.value] as any)?.id;
    if (id == null) return;
    await deleteBizDomain(id);
    await MessagePlugin.success(t('pages.bizDomain.list.deleteSuccess'));
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

const rowKey = 'id';

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

// 跳转到编辑页面
const goToEdit = (id?: number) => {
  if (id) {
    router.push({ name: 'BizDomainEdit', params: { id } });
  } else {
    router.push({ name: 'BizDomainCreate' });
  }
};

const handleCreate = () => {
  goToEdit();
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
</style>

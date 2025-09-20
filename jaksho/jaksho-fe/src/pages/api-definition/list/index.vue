<template>
  <div>
    <t-card class="list-card-container" :bordered="false">
      <t-row justify="space-between">
        <div class="left-operation-container">
          <t-button theme="primary" @click="openDrawerForCreate">{{ t('pages.apiDefinition.list.create') }}</t-button>
          <p v-if="!!selectedRowKeys.length" class="selected-count">
            {{ t('pages.apiDefinition.list.selectedCount', { count: selectedRowKeys.length }) }}
          </p>
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
        @page-change="onPageChange"
        @select-change="(value: (string | number)[]) => (selectedRowKeys = value)"
      >
        <template #op="slotProps">
          <t-space>
            <t-link theme="primary" @click="openDrawerForDetail(slotProps.row)">{{
              t('pages.apiDefinition.list.detail')
            }}</t-link>
            <t-link theme="danger" @click="confirmDelete(slotProps.row)">{{
              t('pages.apiDefinition.list.delete')
            }}</t-link>
          </t-space>
        </template>
      </t-table>
    </t-card>

    <t-dialog
      v-model:visible="confirmVisible"
      :header="t('pages.apiDefinition.list.confirmDelete')"
      :body="confirmBody"
      @confirm="onConfirmDelete"
    />

    <!-- 创建：改用对话框；详情仍保留抽屉 -->
    <t-dialog
      v-model:visible="createDialogVisible"
      :header="t('pages.apiDefinition.drawer.titleCreate')"
      width="70%"
      :footer="false"
    >
      <api-definition-drawer
        v-if="createDialogVisible"
        mode="create"
        :value="null"
        @success="onCreateSuccess"
        @close="createDialogVisible = false"
      />
    </t-dialog>

    <t-drawer v-model:visible="detailDrawerVisible" placement="right" size="70%" :footer="false">
      <template #header>
        <span>{{ t('pages.apiDefinition.drawer.titleDetail') }}</span>
      </template>
      <api-definition-drawer
        v-if="detailDrawerVisible"
        mode="detail"
        :value="detailRecord"
        @success="onDrawerSuccess"
        @close="detailDrawerVisible = false"
      />
    </t-drawer>
  </div>
</template>
<script setup lang="ts">
import type { PageInfo, PrimaryTableCol } from 'tdesign-vue-next';
import { MessagePlugin } from 'tdesign-vue-next';
import { computed, onMounted, ref } from 'vue';

import { deleteApiDefinition, getApiDefinitionList } from '@/api/apiDefinition';
import type { ApiDefinitionModel } from '@/api/model/apiDefinitionModel';
import { prefix } from '@/config/global';
import { t } from '@/locales';
import ApiDefinitionDrawer from '@/pages/api-definition/components/ApiDefinitionDrawer.vue';
import { useSettingStore } from '@/store';

defineOptions({ name: 'ApiDefinitionList' });

const store = useSettingStore();

const COLUMNS: PrimaryTableCol[] = [
  { colKey: 'row-select', type: 'multiple', width: 64, fixed: 'left' },
  { title: t('pages.apiDefinition.list.columns.name'), align: 'left', width: 220, colKey: 'name' },
  { title: t('pages.apiDefinition.list.columns.path'), width: 260, colKey: 'path' },
  { title: t('pages.apiDefinition.list.columns.method'), width: 120, colKey: 'method' },
  { title: t('pages.apiDefinition.list.columns.description'), width: 340, ellipsis: true, colKey: 'description' },
  { title: t('pages.apiDefinition.list.columns.createTime'), width: 180, colKey: 'createTime' },
  { title: t('pages.apiDefinition.list.columns.updateTime'), width: 180, colKey: 'updateTime' },
  { title: t('pages.apiDefinition.list.columns.operation'), align: 'left', fixed: 'right', width: 160, colKey: 'op' },
];

const listData = ref<ApiDefinitionModel[]>([]);
const pagination = ref({ pageSize: 20, total: 0, current: 1 });
const selectedRowKeys = ref<(string | number)[]>([]);
const dataLoading = ref(false);

async function fetchData() {
  dataLoading.value = true;
  try {
    const { rows, total } = await getApiDefinitionList({
      current: pagination.value.current,
      pageSize: pagination.value.pageSize,
    });
    listData.value = rows;
    pagination.value = { ...pagination.value, total };
  } catch (e) {
    console.error(e);
    await MessagePlugin.error(t('pages.apiDefinition.list.loadFailed'));
  } finally {
    dataLoading.value = false;
  }
}

onMounted(fetchData);

const rowKey = 'id';

function onPageChange(pageInfo: PageInfo) {
  pagination.value.current = pageInfo.current;
  pagination.value.pageSize = pageInfo.pageSize;
  fetchData();
}

const headerAffixedTop = computed(
  () =>
    ({
      offsetTop: store.isUseTabsRouter ? 48 : 0,
      container: `.${prefix}-layout`,
    }) as any,
);

const confirmVisible = ref(false);
const pendingDeleteId = ref<number | null>(null);
const confirmBody = computed(() => t('pages.apiDefinition.list.confirmDeleteBody'));

function confirmDelete(row: ApiDefinitionModel) {
  pendingDeleteId.value = row.id;
  confirmVisible.value = true;
}

async function onConfirmDelete() {
  try {
    if (pendingDeleteId.value == null) return;
    await deleteApiDefinition(pendingDeleteId.value);
    await MessagePlugin.success(t('pages.apiDefinition.list.deleteSuccess'));
    confirmVisible.value = false;
    pendingDeleteId.value = null;
    await fetchData();
  } catch (e) {
    console.error(e);
  }
}

const createDialogVisible = ref(false);
const detailDrawerVisible = ref(false);
const detailRecord = ref<ApiDefinitionModel | null>(null);

function openDrawerForCreate() {
  createDialogVisible.value = true;
}
function openDrawerForDetail(row: ApiDefinitionModel) {
  detailRecord.value = row;
  detailDrawerVisible.value = true;
}

async function onCreateSuccess() {
  createDialogVisible.value = false;
  await fetchData();
}
async function onDrawerSuccess() {
  detailDrawerVisible.value = false;
  await fetchData();
}
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
</style>

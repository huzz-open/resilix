<template>
  <t-table :data="rowsModel" :columns="columns" :row-key="rowKey" bordered size="small" />

  <t-dialog
    v-model:visible="selector.visible"
    :header="t('pages.apiDefinition.drawer.selectField')"
    width="70%"
    :on-cancel="onSelectorCancel"
    @confirm="onSelectorConfirm"
  >
    <template #body>
      <t-space direction="vertical" style="width: 100%">
        <t-input
          v-model="selector.keyword"
          :placeholder="t('pages.apiDefinition.drawer.searchPlaceholder')"
          @change="onKeywordChange"
        />
        <t-table
          :data="listData"
          :columns="selectorColumns"
          row-key="bizFieldDomain.id"
          hover
          :pagination="pagination"
          :loading="loading"
          @page-change="onPageChange"
          @row-click="onSelectorRowClick"
        />
      </t-space>
    </template>
  </t-dialog>
</template>
<script setup lang="ts">
import type { PageInfo, PrimaryTableCol } from 'tdesign-vue-next';
import { Input, Link, Switch as TSwitch, Tooltip } from 'tdesign-vue-next';
import { ulid } from 'ulid';
import { computed, h, onMounted, reactive, ref, watch } from 'vue';

import JBizFieldDomainCard from '@/components/j-biz-field-domain-card/index.vue';
import { t } from '@/locales';
import { request } from '@/utils/request';

export interface KvpRow {
  ulid: string;
  key: string;
  value: string;
  isRequired: boolean;
  // 业务扩展：选中的标准字段ID与用于展示的元信息
  bizFieldDomainId?: number | string;
  __meta?: Record<string, any> | null;
}

const props = defineProps<{
  rows: KvpRow[];
  rowKey?: string;
  hideDeleteForLastEmpty?: boolean;
  valueTitleKey?: string; // 默认列标题 key，支持请求参数场景改为“默认值”
}>();
const emit = defineEmits<{ (e: 'update:rows', v: KvpRow[]): void }>();

const rowKey = computed(() => props.rowKey ?? 'ulid');
const rowsModel = computed<KvpRow[]>({
  get: () => props.rows || [],
  set: (v) => emit('update:rows', v),
});

const hideDeleteForLastEmpty = computed(() => Boolean(props.hideDeleteForLastEmpty));

const columns = computed<PrimaryTableCol[]>(() => [
  {
    title: t('pages.apiDefinition.drawer.key'),
    colKey: 'key',
    width: 260,
    cell: (_h: any, p: any) => {
      const row = p?.row as any;
      // 只读输入框，点击打开选择器
      const inputVNode = h(Input as any, {
        size: 'small',
        readonly: true,
        placeholder: t('pages.apiDefinition.drawer.selectFieldPlaceholder'),
        value: row?.key ?? '',
        onClick: () => openSelector(p.rowIndex),
      });

      if (!row?.__meta) return inputVNode;

      const contentVNode = h(JBizFieldDomainCard as any, { entity: buildEntityFromMeta(row.__meta), mode: 'panel' });

      return h(Tooltip as any, { placement: 'top-left' }, { default: () => inputVNode, content: () => contentVNode });
    },
  },
  {
    title: t(props.valueTitleKey || 'pages.apiDefinition.drawer.value'),
    colKey: 'value',
    width: 320,
    cell: (_h: any, p: any) =>
      h(Input as any, {
        size: 'small',
        placeholder: t('pages.apiDefinition.drawer.valuePlaceholder'),
        value: (p?.row as any)?.value,
        onChange: (v: string) => {
          (p.row as any).value = v;
          onCellChange(p.rowIndex);
        },
      }),
  },
  {
    title: t('pages.apiDefinition.drawer.required'),
    colKey: 'isRequired',
    width: 120,
    cell: (_h: any, p: any) =>
      h(TSwitch as any, {
        size: 'small',
        value: Boolean((p?.row as any)?.isRequired),
        onChange: (v: boolean) => {
          (p.row as any).isRequired = v;
          onCellChange(p.rowIndex);
        },
      }),
  },
  {
    title: t('pages.apiDefinition.list.columns.operation'),
    colKey: 'op',
    width: 120,
    cell: (_h: any, p: any) => {
      const idx = p.rowIndex as number;
      if (isSingleRow()) {
        return h(
          Link as any,
          { theme: 'primary', onClick: () => clearRow(idx) },
          { default: () => t('pages.apiDefinition.drawer.clear') },
        );
      }
      if (hideDeleteForLastEmpty.value && isLastEmptyRow(idx)) return null as any;
      return h(
        Link as any,
        { theme: 'danger', onClick: () => removeRow(idx) },
        { default: () => t('pages.apiDefinition.drawer.remove') },
      );
    },
  },
]);

function ensureTrailingEmptyRow() {
  const arr = rowsModel.value || [];
  const last = arr[arr.length - 1];
  if (!last || String(last.key || '').trim() !== '' || String(last.value || '').trim() !== '') {
    rowsModel.value = [...arr, { ulid: ulid(), key: '', value: '', isRequired: false }];
  }
}

function onCellChange(_rowIndex: number) {
  ensureTrailingEmptyRow();
}

function isLastEmptyRow(index: number): boolean {
  const arr = rowsModel.value;
  const lastIndex = arr.length - 1;
  if (index !== lastIndex) return false;
  const last = arr[lastIndex];
  return !!last && String(last.key || '').trim() === '' && String(last.value || '').trim() === '' && !last.isRequired;
}

function removeRow(idx: number) {
  const arr = rowsModel.value || [];
  rowsModel.value = [...arr.slice(0, idx), ...arr.slice(idx + 1)];
  ensureTrailingEmptyRow();
}

function isSingleRow(): boolean {
  const arr = rowsModel.value || [];
  return arr.length <= 1;
}

function clearRow(idx: number) {
  const arr = rowsModel.value || [];
  const row = arr[idx];
  if (!row) return;
  row.key = '';
  row.value = '';
  row.isRequired = false;
  // 触发更新（保持引用稳定也可依赖 Vue 响应式，但此处强制写回保证外层感知）
  rowsModel.value = [...arr];
  ensureTrailingEmptyRow();
}

onMounted(() => {
  ensureTrailingEmptyRow();
});

watch(
  () => rowsModel.value?.map((r) => [r.key, r.value, r.isRequired, r.ulid]) as unknown[],
  () => {
    ensureTrailingEmptyRow();
  },
  { deep: true },
);

// ==================== 选择器（Key 字段选择 BizFieldDomain） ====================
const selector = reactive({ visible: false, rowIndex: -1, keyword: '' });
const pagination = reactive({ pageSize: 10, total: 0, current: 1 });
const loading = ref(false);
const listData = ref<any[]>([]);

const selectorColumns: PrimaryTableCol[] = [
  { title: t('pages.apiDefinition.drawer.selector.name'), colKey: 'bizField.name', ellipsis: true },
  {
    title: t('pages.apiDefinition.drawer.selector.domain'),
    colKey: 'bizDomain.name',
    width: 160,
    cell: (_h: any, p: any) => {
      const row = p.row as any;
      const domainName = row?.bizDomain?.name as string | undefined;
      if (!domainName) return null as any;
      const domainDesc = (row?.bizDomain?.description as string | undefined) || t('pages.apiDefinition.drawer.none');
      return h(
        Tooltip as any,
        { content: domainDesc, placement: 'top' },
        { default: () => h('span', { class: 'domain-pill' }, domainName) },
      );
    },
  },
  {
    title: t('pages.apiDefinition.drawer.selector.basicFieldType'),
    colKey: 'bizFieldType.basicFieldType',
    ellipsis: true,
  },
  {
    title: t('pages.apiDefinition.drawer.selector.collectionType'),
    colKey: 'bizFieldType.collectionType',
    ellipsis: true,
  },
  { title: t('pages.apiDefinition.drawer.selector.minimum'), colKey: 'bizFieldType.minimum', width: 120 },
  { title: t('pages.apiDefinition.drawer.selector.maximum'), colKey: 'bizFieldType.maximum', width: 120 },
  { title: t('pages.apiDefinition.drawer.selector.description'), colKey: 'bizField.description', ellipsis: true },
];

function openSelector(rowIndex: number) {
  selector.visible = true;
  selector.rowIndex = rowIndex;
  pagination.current = 1;
  void fetchSelectorPage();
}

function onKeywordChange() {
  pagination.current = 1;
  void fetchSelectorPage();
}

function onPageChange(pageInfo: PageInfo) {
  pagination.current = pageInfo.current;
  if ((pageInfo as any).pageSize) pagination.pageSize = (pageInfo as any).pageSize as number;
  void fetchSelectorPage();
}

async function fetchSelectorPage() {
  loading.value = true;
  try {
    const rs = await request.post<{ rows: any[]; total: number }>({
      url: '/sr/biz-field-domain/page',
      data: { current: pagination.current, pageSize: pagination.pageSize, keyword: selector.keyword },
    });
    listData.value = rs.rows || [];
    pagination.total = rs.total || 0;
  } finally {
    loading.value = false;
  }
}

function onSelectorRowClick({ row }: any) {
  selector.visible = false;
  const idx = selector.rowIndex;
  const arr = rowsModel.value || [];
  const target = arr[idx];
  if (!target) return;
  // 提取基础信息用于展示及后续提交
  const name = row?.bizField?.name ?? row?.name ?? '';
  target.key = name;
  // 兜底ID：优先取 row.id；若有嵌套结构则取内层 id
  (target as any).bizFieldDomainId = row?.id ?? row?.bizFieldDomain?.id ?? row?.bizField?.id;
  (target as any).__meta = {
    bizField: row?.bizField,
    bizFieldType: row?.bizFieldType,
    bizDomain: row?.bizDomain,
    // 兼容旧依赖字段
    name,
    description: row?.bizField?.description ?? row?.description,
    basicFieldType: row?.bizFieldType?.basicFieldType,
    minimum: row?.bizFieldType?.minimum,
    maximum: row?.bizFieldType?.maximum,
  } as Record<string, any>;
  rowsModel.value = [...arr];
  ensureTrailingEmptyRow();
}

function buildEntityFromMeta(meta: any): any {
  if (!meta) return {};
  if (meta.bizField || meta.bizFieldType || meta.bizDomain) return meta;
  // 兼容旧结构
  return {
    bizField: { name: meta.name, description: meta.description },
    bizFieldType: { basicFieldType: meta.basicFieldType, minimum: meta.minimum, maximum: meta.maximum },
  } as any;
}

function onSelectorCancel() {
  selector.visible = false;
}

function onSelectorConfirm() {
  // 行点击即应用，这里仅关闭
  selector.visible = false;
}
</script>
<style scoped></style>
<style scoped>
.kvp-meta-tip {
  max-width: 420px;
  line-height: 1.6;
}

.kvp-tip-row {
  display: flex;
  gap: 8px;
  margin: 2px 0;
}

.kvp-tip-label {
  color: var(--td-text-color-placeholder);
  white-space: nowrap;
}

.kvp-tip-val {
  color: var(--td-text-color-primary);
}

.selector-name-cell .domain-pill,
.domain-pill {
  display: inline-block;
  padding: 1px 6px;
  border-radius: 8px;
  box-shadow: inset 0 1px 3px rgb(0 0 0 / 25%);
  background: linear-gradient(180deg, rgb(255 255 255 / 6%), rgb(255 255 255 / 2%));
  border: 1px solid var(--td-component-border);
  color: var(--td-text-color-primary);
  margin-right: 0;
}

.selector-name {
  color: var(--td-text-color-primary);
}
</style>

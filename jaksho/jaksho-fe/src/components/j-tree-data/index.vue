<template>
  <t-space direction="vertical" style="width: 100%">
    <t-space align="center">
      <t-button theme="primary" variant="outline" @click="openSelector('appendRoot')">插入一个根节点</t-button>
      <t-input-adornment prepend="filter:">
        <t-input v-model="filterText" @change="onFilterChange" />
      </t-input-adornment>
    </t-space>

    <t-tree
      ref="treeRef"
      :data="treeData"
      hover
      draggable
      :checkable="isCheckable"
      expand-all
      activable
      :label="getLabel"
      :filter="filterByText"
      line
      :scroll="{
        rowHeight: 34,
        bufferSize: 10,
        threshold: 10,
        type: 'virtual',
      }"
    >
      <template #operations="{ node }">
        <t-space :size="10">
          <slot name="ops-prefix" :row="node.data">
            <t-input :value="node.data.data.name" size="small" readonly />
          </slot>
          <t-button size="small" variant="base" @click="openSelector('appendChild', node)">添加子节点</t-button>
          <t-button size="small" variant="outline" @click="openSelector('insertBefore', node)">前插节点</t-button>
          <t-button size="small" variant="outline" @click="openSelector('insertAfter', node)">后插节点</t-button>
          <t-button size="small" variant="base" theme="danger" @click="remove(node)">删除</t-button>
        </t-space>
      </template>
    </t-tree>

    <t-dialog v-model:visible="selector.visible" :header="selectorTitle" width="960px" :on-cancel="onSelectorCancel" @confirm="onSelectorConfirm">
      <template #body>
        <t-space direction="vertical" style="width: 100%">
          <t-input v-model="selector.keyword" placeholder="搜索..." style="width: 260px" @change="onKeywordChange">
            <template #suffix-icon>
              <SearchIcon size="16px" />
            </template>
          </t-input>
          <t-table
            :row-key="rowKeyInternal"
            :data="listData"
            :columns="columns"
            :pagination="pagination"
            :loading="loading"
            v-model:selected-row-keys="selectedRowKeys"
            :hover="true"
            @page-change="onPageChange"
          />
        </t-space>
      </template>
    </t-dialog>
  </t-space>
</template>

<script lang="ts" setup>
import {computed, nextTick, reactive, ref} from 'vue';
import {SearchIcon} from 'tdesign-icons-vue-next';
import {ulid} from 'ulid';
import type {PageInfo, PrimaryTableCol} from 'tdesign-vue-next';

type FetchPageFn = (params: { current: number; pageSize: number; keyword?: string }) => Promise<{ rows: any[]; total: number }>;

interface TreeNodeData {
  value: string; // ulid
  label?: string;
  children?: TreeNodeData[];
  data?: Record<string, any>;
}

const props = defineProps<{
  fetchPage: FetchPageFn;
  // 表格列，用于选择器展示
  columns: PrimaryTableCol[];
  // 选择器行主键
  rowKey?: 'id';
  // 选择模式
  selection?: 'single' | 'multiple';
  // 自定义属性字段名与映射函数
  customField: string;
  getCustomValue: (row: any) => unknown;
  // 分页
  defaultPageSize?: number;
  pageSizeOptions?: number[];
}>();

const emits = defineEmits<{
  (e: 'change', list: Array<Record<string, any>>): void;
  (e: 'update:list', list: Array<Record<string, any>>): void;
}>();

const rowKeyInternal = computed(() => props.rowKey ?? 'id');

const treeRef = ref();
const treeData = ref<TreeNodeData[]>([]);
const isCheckable = ref(true);

// 过滤
const filterText = ref('');
const filterByText = ref<((node: any) => boolean) | null>(null);
const onFilterChange = () => {
  if (filterText.value) {
    filterByText.value = (node: any) => {
      const label = node?.data?.label || '';
      return String(label).includes(filterText.value);
    };
  } else {
    filterByText.value = null;
  }
};

// 选择器状态
const selector = reactive({
  visible: false,
  action: 'appendRoot' as 'appendRoot' | 'appendChild' | 'insertBefore' | 'insertAfter',
  targetValue: '' as string | '',
  keyword: '',
});

const pagination = reactive({
  pageSize: props.defaultPageSize ?? 10,
  total: 0,
  current: 1,
  pageSizeOptions: props.pageSizeOptions ?? [10, 20, 50],
});
const loading = ref(false);
const listData = ref<any[]>([]);
const selectedRowKeys = ref<Array<string | number>>([]);

const columns = computed(() => props.columns);
const displayColumns = computed<PrimaryTableCol[]>(() => (props.columns || []).filter((c: any) => c && c.colKey && c.colKey !== 'row-select' && c.colKey !== 'op'));

const selectorTitle = computed(() => {
  switch (selector.action) {
    case 'appendChild':
      return '选择要作为子节点的数据';
    case 'insertBefore':
      return '选择要前插的数据';
    case 'insertAfter':
      return '选择要后插的数据';
    default:
      return '选择要插入的根节点数据';
  }
});

function getLabel(_h: any, node: any) {
  // 用于过滤与激活，取首列或 customField 作为可读文本
  const first = displayColumns.value[0]?.colKey as string | undefined;
  const label = (first && node?.data?.[first]) ?? node?.data?.[props.customField] ?? '';
  node.data.label = String(label ?? '');
  return node.data.label;
}

function getCellText(node: any, col: PrimaryTableCol) {
  const key = String(col.colKey ?? '');
  if (!key) return '';
  const val = node?.data?.[key];
  return val === undefined || val === null ? '' : String(val);
}

function getCellStyle(col: PrimaryTableCol) {
  const style: Record<string, string> = {};
  const w = (col as any).width;
  if (w !== undefined) {
    style.width = typeof w === 'number' ? `${w}px` : String(w);
    style.flex = '0 0 auto';
  } else {
    style.flex = '1 1 0';
  }
  return style;
}

function openSelector(action: 'appendRoot' | 'appendChild' | 'insertBefore' | 'insertAfter', node?: any) {
  selector.action = action;
  selector.targetValue = node?.value ?? '';
  selector.visible = true;
  selectedRowKeys.value = [];
  pagination.current = 1;
  void fetchPage();
}

function onSelectorCancel() {
  selector.visible = false;
}

function onKeywordChange() {
  pagination.current = 1;
  void fetchPage();
}

function onPageChange(pageInfo: PageInfo) {
  pagination.current = pageInfo.current;
  if ((pageInfo as any).pageSize) pagination.pageSize = (pageInfo as any).pageSize as number;
  void fetchPage();
}

async function fetchPage() {
  loading.value = true;
  try {
    const rs = await props.fetchPage({ current: pagination.current, pageSize: pagination.pageSize, keyword: selector.keyword });
    listData.value = rs.rows || [];
    pagination.total = rs.total || 0;
  } finally {
    loading.value = false;
  }
}

function createNodeFromRow(row: any): TreeNodeData {
  const id = ulid();
  return {
    value: id,
    data: {
      // 保留原始行的全部字段，便于外部在树节点展示时直接取用
      ...(row || {}),
      ulid: id,
      // 若需要自定义字段名作为展示值，允许覆盖/补充
      [props.customField]: props.getCustomValue(row),
    },
  };
}

function insertNodesByAction(rows: any[]) {
  const nodes = rows.map((r) => createNodeFromRow(r));
  const tree = treeRef.value;
  if (!tree) return;

  if (selector.action === 'appendRoot') {
    nodes.forEach((n) => tree.appendTo('', n));
  }
  if (selector.action === 'appendChild') {
    nodes.forEach((n) => tree.appendTo(selector.targetValue, n));
  }
  if (selector.action === 'insertBefore') {
    // 多选保持顺序：按选择顺序逐个前插，后插会改变目标位置索引，使用当前顺序即可
    nodes.forEach((n) => tree.insertBefore(selector.targetValue, n));
  }
  if (selector.action === 'insertAfter') {
    // after 时按逆序插入确保最终顺序与选择顺序一致
    nodes
      .slice()
      .reverse()
      .forEach((n) => tree.insertAfter(selector.targetValue, n));
  }

  // 更新 label
  nextTick(() => {
    nodes.forEach((n) => setLabel(n.value));
  });
}

function setLabel(value: string) {
  const node = treeRef.value.getItem(value);
  const pathNodes = node.getPath();
  const labelIndex = pathNodes.map((n: any) => n.getIndex() + 1).join('.');
  const labelRight = node?.data?.[props.customField];
  node.data.label = `${labelIndex} | ${props.customField}: ${labelRight}`;
}

function onSelectorConfirm() {
  const key = rowKeyInternal.value;
  const selectedRows = listData.value.filter((x) => selectedRowKeys.value.includes(x[key]));
  insertNodesByAction(selectedRows);
  selector.visible = false;
  emitList();
}

function remove(node: any) {
  treeRef.value.remove(node.value);
  emitList();
}

function emitList() {
  const list = buildList();
  emits('update:list', list);
  emits('change', list);
}

function buildList() {
  const list: Array<Record<string, any>> = [];
  const tree = treeRef.value;
  if (!tree) return list;
  const roots: any[] = tree.getTreeData();

  const dfs = (nodes: any[], parentUlid: string | '') => {
    nodes.forEach((n: any, idx: number) => {
      const item = {
        ulid: n.value,
        parentUlid: parentUlid,
        sortOrder: idx,
        [props.customField]: n.data?.[props.customField],
      } as Record<string, any>;
      list.push(item);
      if (Array.isArray(n.children) && n.children.length > 0) {
        dfs(n.children, n.value);
      }
    });
  };
  dfs(roots, '');
  return list;
}

defineExpose({ getList: buildList });
</script>

<style scoped>
.t-tree {
  min-height: 180px;
  background-color: var(--td-bg-color-container);
  padding: 8px 12px;
}
</style>



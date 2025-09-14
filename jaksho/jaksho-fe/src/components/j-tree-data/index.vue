<template>
  <t-space direction="vertical" style="width: 100%">
    <t-space align="center">
      <t-button theme="primary" variant="outline" @click="openSelector(ACTIONS.AppendRoot)">
        {{ i18n.insertRoot }}
      </t-button>
      <t-input-adornment :prepend="i18n.filterLabel">
        <t-input v-model="filterText" @change="onFilterChange" />
      </t-input-adornment>
    </t-space>

    <!-- 简易表头：展示列标题，便于对齐理解 -->
    <div class="j-tree-data-header" :style="{ paddingLeft: `${headerLeftPadding}px` }">
      <t-space :size="12" align="center">
        <template v-for="(col, idx) in headerColumns" :key="String(col.colKey)">
          <span class="header-cell" :style="getHeaderColWidthStyle(idx)">{{ getColTitle(col) }}</span>
        </template>
      </t-space>
    </div>

    <t-tree
      ref="treeRef"
      :data="treeData"
      hover
      draggable
      :checkable="isCheckable"
      expand-all
      activable
      :filter="filterByText"
      :allow-drop="handleAllowDrop"
      line
      :scroll="{
        rowHeight: 34,
        bufferSize: 10,
        threshold: 10,
        type: 'virtual',
      }"
      @drag-end="onTreeDragEnd"
    >
      <template #label="{ node }">
        <div>
          <t-space :size="12">
            <template v-for="(col, idx) in displayColumns" :key="String(col.colKey)">
              <t-input
                v-if="!col.type && col.colKey !== 'row-select'"
                size="small"
                readonly
                :value="String(getCellValue(node.data, col))"
                :style="idx === 0 ? firstColStyle : {}"
                placeholder=""
              ></t-input>
            </template>
          </t-space>
        </div>
      </template>
      <template #operations="{ node }">
        <t-space :size="10">
          <t-tooltip :content="i18n.addChild" placement="top">
            <add-icon class="op-icon" size="16px" @click="openSelector(ACTIONS.AppendChild, node)" />
          </t-tooltip>
          <t-tooltip :content="i18n.insertBefore" placement="top">
            <arrow-up-icon class="op-icon" size="16px" @click="openSelector(ACTIONS.InsertBefore, node)" />
          </t-tooltip>
          <t-tooltip :content="i18n.insertAfter" placement="top">
            <arrow-down-icon class="op-icon" size="16px" @click="openSelector(ACTIONS.InsertAfter, node)" />
          </t-tooltip>
          <t-tooltip :content="i18n.remove" placement="top">
            <delete-icon class="op-icon danger" size="16px" @click="remove(node)" />
          </t-tooltip>
        </t-space>
      </template>
    </t-tree>

    <t-dialog
      v-model:visible="selector.visible"
      :header="selectorTitle"
      :width="dialogWidth"
      :cancel-btn="i18n.cancel"
      :confirm-btn="i18n.confirm"
      :on-cancel="onSelectorCancel"
      @confirm="onSelectorConfirm"
    >
      <template #body>
        <t-space direction="vertical" style="width: 100%">
          <t-input
            v-model="selector.keyword"
            :placeholder="i18n.searchPlaceholder"
            style="width: 260px"
            @change="onKeywordChange"
          >
            <template #suffix-icon>
              <search-icon size="16px" />
            </template>
          </t-input>
          <t-table
            v-model:selected-row-keys="selectedRowKeys"
            select-on-row-click
            :row-key="rowKeyInternal"
            :data="listData"
            :columns="selectorColumns"
            :pagination="pagination"
            :loading="loading"
            :hover="true"
            @page-change="onPageChange"
          />
        </t-space>
      </template>
    </t-dialog>
  </t-space>
</template>
<script lang="ts" setup>
import { AddIcon, ArrowDownIcon, ArrowUpIcon, DeleteIcon, SearchIcon } from 'tdesign-icons-vue-next';
import type { PageInfo, PrimaryTableCol } from 'tdesign-vue-next';
import { MessagePlugin } from 'tdesign-vue-next';
import { ulid } from 'ulid';
import { computed, nextTick, onMounted, reactive, ref } from 'vue';

import { t } from '@/locales';

type FetchPageFn = (params: {
  current: number;
  pageSize: number;
  keyword?: string;
}) => Promise<{ rows: any[]; total: number }>;

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
  // 树节点行内展示的列（默认与 columns 一致，可外部传入覆盖）
  showColumns?: PrimaryTableCol[];
  // 选择器行主键
  rowKey?: string;
  // 选择模式
  selection?: 'single' | 'multiple';
  // 自定义属性字段名与映射函数
  customField: string;
  getCustomValue: (row: any) => unknown;
  // 分页
  defaultPageSize?: number;
  pageSizeOptions?: number[];
  treeDepthThreshold?: number;
}>();
// 统一插入/放置动作常量与类型，避免字符串散落
const ACTIONS = {
  AppendRoot: 'appendRoot',
  AppendChild: 'appendChild',
  InsertBefore: 'insertBefore',
  InsertAfter: 'insertAfter',
} as const;
type Action = (typeof ACTIONS)[keyof typeof ACTIONS];

const treeLineIndentation =
  Number.parseFloat(getComputedStyle(document.documentElement).getPropertyValue('--td-comp-margin-xxl')) || 24;
const rowKeyInternal = computed(() => props.rowKey ?? 'id');
// 支持 a.b.c 点路径的安全取值
function getByPath(source: any, path: string): any {
  if (!path) return undefined;
  if (source == null) return undefined;
  if (path.includes('.')) {
    return path.split('.').reduce((acc: any, k: string) => (acc == null ? acc : acc[k]), source);
  }
  return (source as any)[path];
}
// ========= i18n（统一到全局 locales） =========
const i18n = computed(() => ({
  insertRoot: t('components.jTreeData.insertRoot'),
  filterLabel: t('components.jTreeData.filterLabel'),
  addChild: t('components.jTreeData.addChild'),
  insertBefore: t('components.jTreeData.insertBefore'),
  insertAfter: t('components.jTreeData.insertAfter'),
  remove: t('components.jTreeData.remove'),
  searchPlaceholder: t('components.jTreeData.searchPlaceholder'),
  dialogTitleRoot: t('components.jTreeData.dialogTitleRoot'),
  dialogTitleChild: t('components.jTreeData.dialogTitleChild'),
  dialogTitleBefore: t('components.jTreeData.dialogTitleBefore'),
  dialogTitleAfter: t('components.jTreeData.dialogTitleAfter'),
  cancel: t('components.jTreeData.cancel'),
  confirm: t('components.jTreeData.confirm'),
  dropDenied: t('components.jTreeData.dropDenied'),
}));

// 用于同层级去重的值标准化：将比较值统一转为字符串，降低类型不一致造成的不匹配
function normalizeComparable(val: unknown): string {
  try {
    // 优先保持字符串/数字等原始值的直观表示
    if (val == null) return '';
    if (typeof val === 'string') return val;
    if (typeof val === 'number' || typeof val === 'boolean') return String(val);
    // 避免对象引用不一致导致的比较错误
    return JSON.stringify(val);
  } catch {
    return String(val as any);
  }
}

const extraWidth = ref<number>(0);
const SELECTION_COL_WIDTH = 52;

function computeMaxDepth(nodes: any[], currentDepth: number): number {
  let maxDepth = currentDepth;
  nodes.forEach((n: any) => {
    if (Array.isArray(n.children) && n.children.length > 0) {
      const childMax = computeMaxDepth(n.children, currentDepth + 1);
      if (childMax > maxDepth) maxDepth = childMax;
    }
  });
  return maxDepth;
}

function recalcExtraWidth(_?: string) {
  const tree = treeRef.value;
  if (!tree || !tree.getTreeData) {
    extraWidth.value = 0;
    void nextTick().then(() => {
      measureHeaderOffset();
      measureHeaderColWidths();
    });
    return;
  }
  const treeDepthThreshold = props.treeDepthThreshold ?? 3;
  const roots = tree.getTreeData();
  const maxDepth = computeMaxDepth(roots, 1);
  extraWidth.value = maxDepth < treeDepthThreshold ? 0 : (maxDepth - treeDepthThreshold) * treeLineIndentation;
  void nextTick().then(() => {
    measureHeaderOffset();
    measureHeaderColWidths();
  });
}

const firstColStyle = computed(() => {
  const extra = `${extraWidth.value}px`;
  return {
    marginRight: `calc(var(--td-comp-margin-xxl) * var(--level) * -1)`,
    paddingRight: extra,
    width: `calc(100% + ${extra})`,
  } as Record<string, string>;
});

const treeRef = ref();
const headerLeftPadding = ref(0);
const headerColWidths = ref<number[]>([]);
const treeData = ref<TreeNodeData[]>([]);
const isCheckable = ref(true);

// 过滤
const filterText = ref('');
const filterByText = ref<((node: any) => boolean) | null>(null);
const onFilterChange = () => {
  if (filterText.value) {
    filterByText.value = (node: any) => {
      return String(JSON.stringify(node?.data)).includes(filterText.value);
    };
  } else {
    filterByText.value = null;
  }
};

// 选择器状态
const selector = reactive({
  visible: false,
  action: ACTIONS.AppendRoot as Action,
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
// 移除 rowSelection，改为按文档通过列 disabled 控制禁用

const columns = computed(() => props.columns);
// 选择器表格列：按官方 API，添加或复用选择列（single/multiple），通过 checkProps 禁用行
const selectorColumns = computed(() => {
  // 强制依赖以便在集合变化时重算列定义（避免对象引用复用）
  const _ver = existingLevelValuesVersion?.value ?? existingLevelValues.value.size;
  void _ver;

  const cols = (props.columns || []).map((c: any) => ({ ...c }));
  const hasSelection = cols.some((c: any) => c && (c.type === 'single' || c.type === 'multiple'));
  const ensureCheckProps = (orig: any) => (options: any) => {
    const row = options?.row ?? options;
    const comparable = normalizeComparable(props.getCustomValue(row));
    const disabledByLevel = existingLevelValues.value.has(comparable);
    if (typeof orig === 'function') {
      const rs = orig(options) || {};
      return { ...rs, disabled: Boolean(rs.disabled) || disabledByLevel } as Record<string, any>;
    }
    return { disabled: disabledByLevel } as Record<string, any>;
  };

  if (hasSelection) {
    // 复用已有的选择列，并合并/覆盖其 checkProps 以实现禁用
    return cols.map((c: any) => {
      if (c && (c.type === 'single' || c.type === 'multiple')) {
        const next: any = { ...c };
        next.checkProps = ensureCheckProps(c.checkProps);
        if (typeof next.width === 'undefined') next.width = SELECTION_COL_WIDTH;
        return next;
      }
      return c;
    });
  }

  // 注入一个选择列
  const selectionCol: any = {
    colKey: '__row_select__',
    type: props.selection === 'single' ? 'single' : 'multiple',
    width: SELECTION_COL_WIDTH,
    checkProps: ensureCheckProps(undefined),
  };
  return [selectionCol, ...cols] as any[];
});
// 根据 columns 计算弹窗宽度：
// 1) 优先使用列的 width/widthPx；2) 否则按默认列最小宽度估算；3) 加上动作列与内边距余量；4) 限制最小/最大宽度
const dialogWidth = computed(() => {
  const cols = columns.value || [];
  const defaultColMin = 140; // 每列的保守最小宽度估算
  const opsWidth = 160; // 选择框、分页、余量等
  const padding = 80; // 弹窗内边距/表格滚动条富余
  let total = 0;
  cols.forEach((c: any) => {
    const cw =
      (typeof c.width === 'number' ? c.width : undefined) ||
      (typeof c.width === 'string' && c.width.endsWith('px') ? Number.parseInt(c.width, 10) : undefined);
    total += cw && !Number.isNaN(cw) ? cw : defaultColMin;
  });
  const width = total + opsWidth + padding;
  const min = 720;
  const max = 1440;
  const bounded = Math.max(min, Math.min(width, max));
  return `${bounded}px`;
});
const showColumnsResolved = computed<PrimaryTableCol[]>(() => {
  // 优先获取 showColumns，如果没有则使用 columns
  return props.showColumns && props.showColumns.length > 0 ? props.showColumns : props.columns;
});

// 用于渲染的一致列数组（去掉 row-select），保证 header 与行渲染顺序/数量一致
const displayColumns = computed<PrimaryTableCol[]>(() => {
  const cols = showColumnsResolved.value || [];
  return cols.filter((c: any) => c && c.colKey !== 'row-select');
});

// 头部列：过滤掉选择列（type 为 single/multiple 或 colKey === 'row-select'）
const headerColumns = computed<PrimaryTableCol[]>(() => {
  const cols = displayColumns.value || [];
  return cols.filter((c: any) => {
    if (!c) return false;
    if (c.colKey === 'row-select') return false;
    if ((c as any).type === 'single' || (c as any).type === 'multiple') return false;
    return true;
  });
});

function getCellValue(rowData: any, col: PrimaryTableCol) {
  const key = (col?.colKey as string) || '';
  if (!key) return '';
  const source = rowData ?? {};
  // 支持 a.b.c 的安全取值
  return key.split('.').reduce((acc: any, k: string) => (acc == null ? acc : acc[k]), source?.data) ?? '';
}

function getColTitle(col: PrimaryTableCol): string {
  const tVal = (col as any)?.title;
  if (typeof tVal === 'string') return tVal;
  // 回退到 colKey 显示
  return String((col?.colKey as string) || '');
}

// 与 header 对齐：按列索引返回与行渲染一致的宽度
function getHeaderColWidthStyle(index: number): Record<string, string> {
  // 优先使用首行实际渲染宽度，确保像素级对齐
  const measured = headerColWidths.value?.[index];
  if (measured && measured > 0) {
    return { width: `${measured}px` };
  }
  // 若列对象提供 width，优先使用，否则使用默认最小宽度
  const col = (displayColumns.value || [])[index] as any;
  const defaultMin = 140;
  const colWidth =
    (typeof col?.width === 'number' ? col.width : undefined) ||
    (typeof col?.width === 'string' && col.width.endsWith('px') ? Number.parseInt(col.width, 10) : undefined) ||
    defaultMin;
  return { minWidth: `${colWidth}px` };
}

const selectorTitle = computed(() => {
  switch (selector.action) {
    case ACTIONS.AppendChild:
      return i18n.value.dialogTitleChild;
    case ACTIONS.InsertBefore:
      return i18n.value.dialogTitleBefore;
    case ACTIONS.InsertAfter:
      return i18n.value.dialogTitleAfter;
    default:
      return i18n.value.dialogTitleRoot;
  }
});

function openSelector(action: Action, node?: any) {
  selector.action = action;
  selector.targetValue = node?.value ?? '';
  selector.visible = true;
  selectedRowKeys.value = [];
  pagination.current = 1;
  // 预计算：当前插入目标层级中已有的数据值集合
  computeExistingLevelValues();
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
    const rs = await props.fetchPage({
      current: pagination.current,
      pageSize: pagination.pageSize,
      keyword: selector.keyword,
    });
    listData.value = rs.rows || [];
    pagination.total = rs.total || 0;
    // 基于当前页数据，更新禁用并预勾选的 keys（同层级已存在）
    updateDisabledSelectionsForCurrentPage();
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

  if (selector.action === ACTIONS.AppendRoot) {
    nodes.forEach((n) => tree.appendTo('', n));
  }
  if (selector.action === ACTIONS.AppendChild) {
    nodes.forEach((n) => tree.appendTo(selector.targetValue, n));
  }
  if (selector.action === ACTIONS.InsertBefore) {
    // 多选保持顺序：按选择顺序逐个前插，后插会改变目标位置索引，使用当前顺序即可
    nodes.forEach((n) => tree.insertBefore(selector.targetValue, n));
  }
  if (selector.action === ACTIONS.InsertAfter) {
    // after 时按逆序插入确保最终顺序与选择顺序一致
    nodes
      .slice()
      .reverse()
      .forEach((n) => tree.insertAfter(selector.targetValue, n));
  }
  recalcExtraWidth('insert');
}

function onSelectorConfirm() {
  const key = rowKeyInternal.value;
  // 仅插入“新勾选”的字段：过滤掉同层级已存在（被禁用并预勾选）的项
  const selectedRows = listData.value.filter((x) => {
    const k = getByPath(x, key as string);
    if (!selectedRowKeys.value.includes(k)) return false;
    const v = normalizeComparable(props.getCustomValue(x));
    return !existingLevelValues.value.has(v);
  });
  insertNodesByAction(selectedRows);
  selector.visible = false;
}

function remove(node: any) {
  treeRef.value.remove(node.value);
  recalcExtraWidth('remove');
}

function buildTreeDTOList() {
  const list: Array<Record<string, any>> = [];
  const tree = treeRef.value;
  if (!tree) return list;
  const roots: any[] = tree.getTreeData();

  const dfs = (nodes: any[], parentUlid: string) => {
    nodes.forEach((n: any, idx: number) => {
      const item = {
        ulid: n.value,
        parentUlid,
        sortOrder: idx,
        [props.customField]: n.data?.[props.customField],
      } as Record<string, any>;
      list.push(item);
      if (Array.isArray(n.children) && n.children.length > 0) {
        dfs(n.children, n.value);
      }
    });
  };
  dfs(roots, null);
  return list;
}

defineExpose({ getTreeDTOList: buildTreeDTOList });

function onTreeDragEnd() {
  recalcExtraWidth('dragend');
}

// ============== 限制：同层级去重（插入 + 拖拽） ==============
const existingLevelValues = ref<Set<any>>(new Set());
const existingLevelValuesVersion = ref(0);

function computeExistingLevelValues() {
  const tree = treeRef.value;
  if (!tree || !tree.getTreeData) {
    existingLevelValues.value = new Set();
    return;
  }
  const roots: any[] = tree.getTreeData();
  const levelNodes = getTargetLevelNodesForAction(roots, selector.action, selector.targetValue);
  const vals = new Set<any>();
  levelNodes.forEach((n: any) => {
    const v = n?.data?.[props.customField];
    if (v !== undefined) vals.add(normalizeComparable(v));
  });
  existingLevelValues.value = vals;
  existingLevelValuesVersion.value++;
}

function getTargetLevelNodesForAction(roots: any[], action: Action, targetValue: string) {
  if (action === ACTIONS.AppendRoot) return roots || [];
  const { node, parent } = findNodeWithParent(roots, targetValue);
  if (action === ACTIONS.AppendChild) return (node?.children as any[]) || [];
  // insertBefore/insertAfter：同父节点的 children（根节点则为 roots）
  if (action === ACTIONS.InsertBefore || action === ACTIONS.InsertAfter) {
    if (parent) return (parent.children as any[]) || [];
    return roots || [];
  }
  return [] as any[];
}

function findNodeWithParent(nodes: any[], target: string, parent?: any): { node: any | null; parent: any | null } {
  for (const n of nodes || []) {
    if (String(n?.value) === String(target)) return { node: n, parent: parent ?? null };
    if (Array.isArray(n?.children) && n.children.length > 0) {
      const rs = findNodeWithParent(n.children, target, n);
      if (rs.node) return rs;
    }
  }
  return { node: null, parent: null };
}

function updateDisabledSelectionsForCurrentPage() {
  const key = rowKeyInternal.value;
  // 当前页中已存在于层级的数据项：禁用并预勾选
  const toDisableKeys = new Set<any>();
  (listData.value || []).forEach((row: any) => {
    const v = normalizeComparable(props.getCustomValue(row));
    if (existingLevelValues.value.has(v)) {
      toDisableKeys.add(getByPath(row, key as string));
    }
  });
  // 预勾选：把禁用项加到 selectedRowKeys（保持用户已勾选项不丢失）
  const next = new Set<any>(selectedRowKeys.value as any[]);
  toDisableKeys.forEach((k) => next.add(k));
  selectedRowKeys.value = Array.from(next);
}

function handleAllowDrop(context: any) {
  try {
    const tree = treeRef.value;
    if (!tree || !tree.getTreeData) return true;
    const roots: any[] = tree.getTreeData();
    const dragValueId = context?.dragNode?.value;
    const dropValueId = context?.dropNode?.value;
    const dropPosition = context?.dropPosition; // -1: before, 0: inner, 1: after

    const dragNode = dragValueId ? findNodeWithParent(roots, dragValueId).node : null;
    const dropNode = dropValueId ? findNodeWithParent(roots, dropValueId).node : null;
    if (!dragNode || !dropNode) return true;

    // 目标层级节点集合：根据 dropPosition 决定同级集合
    let levelNodes;
    if (dropPosition === 0) {
      // 放入 dropNode 的 children
      levelNodes = (dropNode.children as any[]) || [];
    } else {
      // 放在 dropNode 前/后 → 与 dropNode 同父的 children（根则为 roots）
      const rel = findNodeWithParent(roots, dropNode.value);
      levelNodes = rel.parent ? (rel.parent.children as any[]) || [] : roots;
    }

    const dragValue = dragNode?.data?.[props.customField];
    // 排除拖动节点自身后，检查是否存在相同数据
    const conflict = (levelNodes || [])
      .filter((n: any) => String(n?.value) !== String(dragNode.value))
      .some((n: any) => normalizeComparable(n?.data?.[props.customField]) === normalizeComparable(dragValue));
    if (conflict) {
      tipDenyOnce(`${dragNode.value}->${dropNode.value}:${dropPosition}`, i18n.value.dropDenied);
      return false;
    }
    return true;
  } catch {
    return true;
  }
}

// 拖拽禁止提示：节流避免频繁弹出
const denyTipState = reactive({ lastKey: '', lastAt: 0 });

function tipDenyOnce(key: string, message: string) {
  const now = Date.now();
  const hitDifferentTarget = denyTipState.lastKey !== key;
  const hitTimeout = now - denyTipState.lastAt > 800;
  if (hitDifferentTarget || hitTimeout) {
    denyTipState.lastKey = key;
    denyTipState.lastAt = now;
    MessagePlugin.warning(message);
  }
}

onMounted(() => {
  recalcExtraWidth('');
  measureHeaderOffset();
  measureHeaderColWidths();
  window.addEventListener('resize', () => {
    measureHeaderOffset();
    measureHeaderColWidths();
  });
});

function measureHeaderOffset() {
  try {
    const treeEl = (treeRef.value as any)?.$el as HTMLElement;
    if (!treeEl) {
      headerLeftPadding.value = 0;
      return;
    }
    // 方案A：优先用官方设计变量估算（更可控，可避免首行未渲染/虚拟滚动测量失败）
    const readCssNumber = (name: string) => {
      const v = getComputedStyle(document.documentElement).getPropertyValue(name);
      const n = Number.parseFloat(v);
      return Number.isFinite(n) ? n : 0;
    };
    const paddingLeft = Number.parseFloat(getComputedStyle(treeEl).paddingLeft || '0') || 0;
    // 估算图标/复选框区域：基于间距变量兜底（不同主题/密度存在差异）
    const marginXXL = readCssNumber('--td-comp-margin-xxl');
    const marginXL = readCssNumber('--td-comp-margin-xl');
    const marginL = readCssNumber('--td-comp-margin-l');
    const marginM = readCssNumber('--td-comp-margin-m');
    // 取一个合理的代表宽度：优先使用 xxl/xl，否则退化到 l/m
    const unit = marginXXL || marginXL || marginL || marginM || 16;
    // 当可勾选时，预留一个复选框（含左右间距）的宽度；再预留展开图标区域
    const checkWidth = isCheckable.value ? unit : 0;
    const expandIconWidth = unit;
    const fromVars = Math.floor(paddingLeft + checkWidth + expandIconWidth);

    // 方案B：DOM 实测（兜底）：第一行 label 左边界与树容器左边界之差
    const firstLabelEl = treeEl.querySelector('.t-tree__label') as HTMLElement;
    const treeRect = treeEl.getBoundingClientRect();
    const labelRect = firstLabelEl?.getBoundingClientRect();
    const fromDom = labelRect ? Math.max(0, Math.floor(labelRect.left - treeRect.left)) : 0;

    // 取两者的较大值，避免由于变量估算偏小导致错位
    headerLeftPadding.value = Math.max(fromVars, fromDom);
  } catch {
    headerLeftPadding.value = 0;
  }
}

function measureHeaderColWidths() {
  try {
    const treeEl = (treeRef.value as any)?.$el as HTMLElement;
    if (!treeEl) {
      headerColWidths.value = [];
      return;
    }
    const firstLabelEl = treeEl.querySelector('.t-tree__label') as HTMLElement;
    if (!firstLabelEl) {
      headerColWidths.value = [];
      return;
    }
    const nodes = Array.from(firstLabelEl.querySelectorAll('.t-input, .t-input-number')) as HTMLElement[];
    headerColWidths.value = nodes.map((el) => Math.max(0, Math.floor(el.getBoundingClientRect().width)));
  } catch {
    headerColWidths.value = [];
  }
}
</script>
<style scoped>
.t-tree {
  min-height: 180px;
  background-color: var(--td-bg-color-container);
  padding: 8px 12px;
}

.op-icon {
  cursor: pointer;
  color: var(--td-text-color-secondary);
}

.op-icon:hover {
  color: var(--td-brand-color);
}

.op-icon.danger:hover {
  color: var(--td-error-color);
}

.j-tree-data-header {
  padding: 6px 12px 0 12px;
  color: var(--td-text-color-secondary);
}

.header-cell {
  display: inline-block;
  min-width: 120px;
}

.header-sep {
  color: var(--td-text-color-placeholder);
}
</style>

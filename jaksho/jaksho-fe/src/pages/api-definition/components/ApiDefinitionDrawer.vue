<template>
  <t-space direction="vertical" style="width: 100%">
    <t-form ref="formRef" :data="form" label-align="top" @submit="onSubmit">
      <t-row :gutter="16">
        <t-col :span="6">
          <t-form-item
            :label="t('pages.apiDefinition.drawer.name')"
            name="name"
            :rules="[{ required: true, message: t('pages.apiDefinition.drawer.validate.nameRequired') }]"
            ><!-- eslint-disable-line max-len -->
            <t-input v-model="form.name" :maxlength="100" show-word-limit />
          </t-form-item>
        </t-col>
        <t-col :span="6">
          <t-form-item
            :label="t('pages.apiDefinition.drawer.method')"
            name="method"
            :rules="[{ required: true, message: t('pages.apiDefinition.drawer.validate.methodRequired') }]"
            ><!-- eslint-disable-line max-len -->
            <t-select v-model="form.method">
              <t-option v-for="m in METHODS" :key="m" :value="m" :label="m" />
            </t-select>
          </t-form-item>
        </t-col>
        <t-col :span="12">
          <t-form-item
            :label="t('pages.apiDefinition.drawer.path')"
            name="path"
            :rules="[{ required: true, message: t('pages.apiDefinition.drawer.validate.pathRequired') }]"
            ><!-- eslint-disable-line max-len -->
            <t-input v-model="form.path" :placeholder="t('pages.apiDefinition.drawer.pathPlaceholder')" />
          </t-form-item>
        </t-col>
      </t-row>

      <t-form-item :label="t('pages.apiDefinition.drawer.description')" name="description">
        <t-textarea v-model="form.description" :maxlength="255" :height="80" show-word-limit />
      </t-form-item>

      <!-- 类别切换：路径参数 / 请求参数 / 请求体 -->
      <t-tabs v-model="activeCategory">
        <t-tab-panel :label="t('pages.apiDefinition.drawer.pathParams')" value="path">
          <t-form-item :label="t('pages.apiDefinition.drawer.pathParams')">
            <j-kvp-table v-model:rows="pathRows" hide-delete-for-last-empty :open-by-click="false" />
          </t-form-item>
        </t-tab-panel>
        <t-tab-panel :label="t('pages.apiDefinition.drawer.tabs.params')" value="params">
          <t-form-item :label="t('pages.apiDefinition.drawer.queryParams')">
            <j-kvp-table
              v-model:rows="queryRows"
              hide-delete-for-last-empty
              :selector-filters="{ notBasicFieldTypes: ['OBJECT', 'FILE'], collectionType: 'NONE' }"
            />
          </t-form-item>
        </t-tab-panel>

        <t-tab-panel :label="t('pages.apiDefinition.drawer.tabs.body')" value="body">
          <t-form-item label="" name="bodyType">
            <t-radio-group v-model="form.bodyType" @change="onBodyTypeChange">
              <t-radio v-for="b in BODY_TYPES" :key="b" :value="b">{{ b }}</t-radio>
            </t-radio-group>
          </t-form-item>

          <template v-if="form.bodyType === 'NONE'">
            <t-alert theme="info" :message="t('pages.apiDefinition.drawer.noBody')" />
          </template>

          <template v-if="form.bodyType === 'FORM_DATA' || form.bodyType === 'FORM_URLENCODED'">
            <j-kvp-table
              v-model:rows="kvpRows"
              hide-delete-for-last-empty
              :selector-filters="{ notBasicFieldTypes: ['OBJECT', 'FILE'], collectionType: 'NONE' }"
            />
          </template>

          <template v-if="form.bodyType === 'RAW_TEXT'">
            <t-textarea
              v-model="rawText"
              :height="240"
              :placeholder="t('pages.apiDefinition.drawer.rawTextPlaceholder')"
            />
          </template>

          <!-- RAW_JSON 面板：受控树数据，通过 v-model:treeDtoList 回显与同步 -->
          <div v-show="form.bodyType === 'RAW_JSON'">
            <t-space direction="vertical" style="width: 100%">
              <j-tree-data
                ref="jTreeRef"
                v-model:tree-dto-list="rawJsonTreeDTO"
                :fetch-page="fetchBizFieldDomainPage"
                :columns="jsonFieldColumns"
                row-key="bizFieldDomain.id"
                selection="multiple"
                custom-field="bizFieldDomainId"
                :get-custom-value="(row: any) => row.bizFieldDomain.id"
                :default-page-size="10"
              />
            </t-space>
          </div>

          <template v-if="form.bodyType === 'BINARY'">
            <t-alert theme="info" :message="t('pages.apiDefinition.drawer.binaryTip')" />
          </template>
        </t-tab-panel>
        <t-tab-panel label="业务码" value="bizCode">
          <div v-if="mode === 'detail' && props.value?.id" class="biz-code-panel">
            <biz-code-selector :api-id="props.value.id" @change="onBizCodesChange" />
          </div>
          <t-alert v-else theme="info" message="请先创建接口后再关联业务码" style="margin-top: 16px;" />
        </t-tab-panel>
      </t-tabs>

      <!-- 路径字段选择对话框：在路径输入包含"{"时弹出 -->
      <t-dialog
        v-model:visible="pathSelector.visible"
        :header="t('pages.apiDefinition.drawer.selectPathField')"
        width="70%"
        :on-cancel="onPathSelectorCancel"
      >
        <template #body>
          <t-space direction="vertical" style="width: 100%">
            <t-input
              v-model="pathSelector.keyword"
              :placeholder="t('pages.apiDefinition.drawer.searchPlaceholder')"
              @change="onPathKeywordChange"
            />
            <t-table
              :data="pathListData"
              :columns="pathSelectorColumns"
              row-key="bizFieldDomain.id"
              hover
              :row-class-name="pathRowClassName"
              :pagination="pathPagination"
              :loading="pathLoading"
              @page-change="onPathPageChange"
              @row-click="onPathSelectorRowClick"
            />
          </t-space>
        </template>
      </t-dialog>

      <div class="footer">
        <t-button theme="primary" type="submit" :loading="submitting">
          {{ mode === 'create' ? '创建' : '保存' }}
        </t-button>
      </div>
    </t-form>
  </t-space>
</template>
<script setup lang="ts">
import type { FormInstanceFunctions, PrimaryTableCol } from 'tdesign-vue-next';
import { MessagePlugin } from 'tdesign-vue-next';
import { ulid } from 'ulid';
import { nextTick, onMounted, reactive, ref, watch } from 'vue';

import { createApiDefinition, getApiDefinitionDetail } from '@/api/apiDefinition';
import type {
  ApiDefinitionFieldDTO,
  ApiDefinitionModel,
  BodyType,
  CreateApiDefinitionRequest,
  FieldType,
  HttpMethod,
} from '@/api/model/apiDefinitionModel';
import BizCodeSelector from '@/components/biz-code-selector/index.vue';
import JKvpTable from '@/components/j-kvp-table/index.vue';
import JTreeData from '@/components/j-tree-data/index.vue';
import { t } from '@/locales';
import { request } from '@/utils/request';

// 详情模式：仅做初始回填（当前后端无详情接口，暂按传入行填充基本信息）
const props = defineProps<{ mode: 'create' | 'detail'; value: ApiDefinitionModel | null }>();
const emit = defineEmits<{ (e: 'success'): void; (e: 'close'): void }>();
const METHODS: HttpMethod[] = ['GET', 'POST', 'PUT', 'DELETE', 'HEAD', 'TRACE', 'OPTIONS', 'PATCH'];
const BODY_TYPES: BodyType[] = ['NONE', 'FORM_DATA', 'FORM_URLENCODED', 'RAW_JSON', 'RAW_TEXT', 'BINARY'];

const formRef = ref<FormInstanceFunctions>();
// 类别页签：路径参数 / 请求参数 / 请求体 / 业务码
const activeCategory = ref<'path' | 'params' | 'body' | 'bizCode'>('params');
const form = ref<CreateApiDefinitionRequest>({
  name: '',
  path: '',
  method: 'GET',
  bodyType: 'NONE',
  description: '',
  remark: '',
  apiDefinitionFieldDTOList: [],
});

// KVP 列表（form-data/x-www-form-urlencoded）
interface KVPRow {
  ulid: string;
  key: string;
  value: string;
  isRequired: boolean;
}

const kvpRows = ref<KVPRow[]>([]);
// 表格列已在 kvp-table 内部定义，这里不再需要

// 行行为改由 kvp-table 统一处理

// Path / Query 列表
const pathRows = ref<KVPRow[]>([]);
// Query 列表：始终保留一行空白行
const queryRows = ref<KVPRow[]>([]);
// 列由 kvp-table 定义

// 行行为改由 kvp-table 统一处理

// RAW 文本
const rawText = ref('');
// RAW_JSON 受控树 DTO 列表
const rawJsonTreeDTO = ref<any[]>([]);
// 提取路径参数名称
function extractPathParamNames(source: string | null | undefined): string[] {
  return Array.from(String(source || '').matchAll(/\{(.*?)\}/g))
    .map((m) => (m?.[1] || '').trim())
    .filter(Boolean);
}

// RAW JSON 使用 j-tree-data 选择 BizFieldDomain 构建树
const jTreeRef = ref<InstanceType<typeof JTreeData> | null>(null);
const jsonFieldColumns: PrimaryTableCol[] = [
  { colKey: 'row-select', type: 'multiple' },
  { title: t('pages.apiDefinition.drawer.selector.name'), colKey: 'bizField.name', ellipsis: true },
  { title: t('pages.apiDefinition.drawer.selector.basicFieldType'), colKey: 'bizFieldType.basicFieldType', ellipsis: true },
  { title: t('pages.apiDefinition.drawer.selector.collectionType'), colKey: 'bizFieldType.collectionType', ellipsis: true },
  { title: t('pages.apiDefinition.drawer.selector.minimum'), colKey: 'bizFieldType.minimum', ellipsis: true },
  { title: t('pages.apiDefinition.drawer.selector.maximum'), colKey: 'bizFieldType.maximum', ellipsis: true },
];

async function fetchBizFieldDomainPage(params: { current: number; pageSize: number; keyword?: string }) {
  return request.post<{ rows: any[]; total: number }>({
    url: '/sr/biz-field-domain/page',
    data: { current: params.current, pageSize: params.pageSize, keyword: params.keyword },
  });
}

function onBodyTypeChange() {
  // 切换体裁清空相关数据
  kvpRows.value = [];
  rawText.value = '';
  // 若切到表格类体裁，预置一行空白，避免初始无法编辑
  if (form.value.bodyType === 'FORM_DATA' || form.value.bodyType === 'FORM_URLENCODED') {
    kvpRows.value = [{ ulid: ulid(), key: '', value: '', isRequired: false }];
  }
}

function buildFieldsByBodyType(): ApiDefinitionFieldDTO[] {
  const list: ApiDefinitionFieldDTO[] = [];
  // 路径参数：优先使用“路径参数”页签内容；若为空则回退到路径解析
  const pathRowsFiltered = (pathRows.value || []).filter((r) => String(r.key || '').trim() !== '');
  if (pathRowsFiltered.length > 0) {
    pathRowsFiltered.forEach((row, idx) => {
      list.push({
        fieldType: 'PATH',
        ulid: row.ulid,
        parentUlid: null,
        sortOrder: idx,
        isRequired: row.isRequired,
        description: row.key,
        // 由选择器注入
        bizFieldDomainId: (row as any).bizFieldDomainId,
      });
    });
  } else {
    const names = extractPathParamNames(form.value.path);
    names.forEach((p, idx) => {
      list.push({
        fieldType: 'PATH',
        ulid: ulid(),
        parentUlid: null,
        sortOrder: idx,
        isRequired: true,
        description: p,
      });
    });
  }

  // Query 参数：过滤掉空白行
  queryRows.value.forEach((row, idx) => {
    const hasContent = String(row.key || '').trim() !== '' || String(row.value || '').trim() !== '';
    if (!hasContent) return;
    list.push({
      fieldType: 'QUERY',
      ulid: row.ulid,
      parentUlid: null,
      sortOrder: idx,
      description: row.key,
      isRequired: row.isRequired,
      // 由选择器注入
      bizFieldDomainId: (row as any).bizFieldDomainId,
    });
  });

  if (form.value.bodyType === 'FORM_DATA' || form.value.bodyType === 'FORM_URLENCODED') {
    const fieldType: FieldType = form.value.bodyType === 'FORM_DATA' ? 'FORM_DATA' : 'FORM_URLENCODED';
    kvpRows.value.forEach((row, idx) => {
      const hasContent = String(row.key || '').trim() !== '' || String(row.value || '').trim() !== '';
      if (!hasContent) return;
      list.push({
        fieldType,
        ulid: row.ulid,
        parentUlid: null,
        sortOrder: idx,
        description: row.key,
        isRequired: row.isRequired,
        // 由选择器注入到 kvp 行
        bizFieldDomainId: (row as any).bizFieldDomainId,
      });
    });
  }
  if (form.value.bodyType === 'RAW_TEXT') {
    list.push({ fieldType: 'RAW_TEXT', ulid: ulid(), parentUlid: null, sortOrder: 0, description: rawText.value });
  }
  if (form.value.bodyType === 'RAW_JSON') {
    const tree = jTreeRef.value?.getTreeDTOList?.() || [];
    (tree as any[]).forEach((x, idx) => {
      list.push({
        fieldType: 'RAW_JSON',
        ulid: x.ulid,
        parentUlid: x.parentUlid ?? null,
        sortOrder: x.sortOrder ?? idx,
        // bizFieldDomainId 来自选择器
        bizFieldDomainId: x.bizFieldDomainId,
      });
    });
  }
  return list;
}

const submitting = ref(false);
// 提前声明以避免 watch(immediate) 时未初始化
const braceInsertIndex = ref<number>(-1);
const pathAutoPopupEnabled = ref(true);

// 业务码变化处理
const onBizCodesChange = (bizCodes: any[]) => {
  console.log('业务码已更新:', bizCodes);
  // 业务码变化的回调，可以在这里做额外处理
};

async function onSubmit() {
  submitting.value = true;
  try {
    const payload: CreateApiDefinitionRequest = { ...form.value };
    payload.apiDefinitionFieldDTOList = buildFieldsByBodyType();
    await createApiDefinition(payload);
    await MessagePlugin.success(t('pages.apiDefinition.drawer.saveSuccess'));
    emit('success');
  } catch (e) {
    console.error(e);
  } finally {
    submitting.value = false;
  }
}

watch(
  () => props.value,
  (val) => {
    if (props.mode === 'detail' && val) {
      // 统一从详情接口获取完整数据
      void (async () => {
        try {
          pathAutoPopupEnabled.value = false;
          const detail = await getApiDefinitionDetail((val as any).id);
          const data = (detail as any)?.data || detail;
          form.value.name = data?.name || '';
          form.value.path = data?.path || '';
          form.value.method = (data?.method as HttpMethod) || 'GET';
          form.value.bodyType = (data?.bodyType as BodyType) || 'NONE';
          form.value.description = data?.description || '';

          const rows: any[] = (data?.apiDefinitionFields || []) as any[];
          // 统一提取 apiDefinitionField + 元数据
          const normalized = rows.map((r) => ({
            api: r?.apiDefinitionField || {},
            bizField: r?.bizField || {},
            bizFieldType: r?.bizFieldType || {},
            bizDomain: r?.bizDomain || {},
          }));
          const sorted = normalized.sort((a, b) => (a.api?.sortOrder ?? 0) - (b.api?.sortOrder ?? 0));

          // PATH → 路径参数
          const pathFields = sorted.filter((x) => x.api?.fieldType === 'PATH');
          pathRows.value = pathFields.map((f) => ({
            ulid: f.api.ulid,
            key: f.bizField?.name ?? f.api?.description ?? '',
            value: '',
            isRequired: !!f.api.isRequired,
            bizFieldDomainId: f.api.bizFieldDomainId,
            __meta: {
              bizField: f.bizField,
              bizFieldType: f.bizFieldType,
              bizDomain: f.bizDomain,
              name: f.bizField?.name,
              description: f.bizField?.description,
              basicFieldType: f.bizFieldType?.basicFieldType,
              minimum: f.bizFieldType?.minimum,
              maximum: f.bizFieldType?.maximum,
            },
          }));

          // QUERY → 请求参数
          const queryFields = sorted.filter((x) => x.api?.fieldType === 'QUERY');
          queryRows.value = queryFields.map((f) => ({
            ulid: f.api.ulid,
            key: f.bizField?.name ?? f.api?.description ?? '',
            value: '',
            isRequired: !!f.api.isRequired,
            bizFieldDomainId: f.api.bizFieldDomainId,
            __meta: {
              bizField: f.bizField,
              bizFieldType: f.bizFieldType,
              bizDomain: f.bizDomain,
              name: f.bizField?.name,
              description: f.bizField?.description,
              basicFieldType: f.bizFieldType?.basicFieldType,
              minimum: f.bizFieldType?.minimum,
              maximum: f.bizFieldType?.maximum,
            },
          }));

          // FORM_DATA / FORM_URLENCODED → 键值对表
          const formDataFields = sorted.filter((x) => x.api?.fieldType === 'FORM_DATA');
          const urlEncodedFields = sorted.filter((x) => x.api?.fieldType === 'FORM_URLENCODED');
          const toKvp = (f: any) => ({
            ulid: f.api.ulid,
            key: f.bizField?.name ?? f.api?.description ?? '',
            value: '',
            isRequired: !!f.api.isRequired,
            bizFieldDomainId: f.api.bizFieldDomainId,
            __meta: {
              bizField: f.bizField,
              bizFieldType: f.bizFieldType,
              bizDomain: f.bizDomain,
              name: f.bizField?.name,
              description: f.bizField?.description,
              basicFieldType: f.bizFieldType?.basicFieldType,
              minimum: f.bizFieldType?.minimum,
              maximum: f.bizFieldType?.maximum,
            },
          });
          if ((data?.bodyType as BodyType) === 'FORM_DATA' || (data?.bodyType as BodyType) === 'FORM_URLENCODED') {
            kvpRows.value = ((data?.bodyType as BodyType) === 'FORM_DATA' ? formDataFields : urlEncodedFields).map(
              toKvp,
            );
          } else {
            kvpRows.value = [];
          }

          // RAW_JSON → 树 DTO（包含用于列展示的扁平字段）
          const rawJson = sorted.filter((x) => x.api?.fieldType === 'RAW_JSON');
          rawJsonTreeDTO.value = rawJson.map((f) => ({
            ulid: f.api.ulid,
            parentUlid: f.api.parentUlid || null,
            sortOrder: f.api.sortOrder ?? 0,
            bizFieldDomainId: f.api.bizFieldDomainId,
            name: f.bizField?.name ?? '',
            basicFieldType: f.bizFieldType?.basicFieldType ?? '',
            collectionType: f.bizFieldType?.collectionType ?? '',
            minimum: f.bizFieldType?.minimum ?? null,
            maximum: f.bizFieldType?.maximum ?? null,
          }));
          form.value.bodyType = (data?.bodyType as BodyType) || 'RAW_JSON';
          activeCategory.value = 'body';
        } finally {
          void nextTick(() => (pathAutoPopupEnabled.value = true));
        }
      })();
    } else {
      form.value = {
        name: '',
        path: '',
        method: 'GET',
        bodyType: 'NONE',
        description: '',
        remark: '',
        apiDefinitionFieldDTOList: [],
      };
      // 初始化：参数与表单类均预置一行空白，保障可编辑
      pathRows.value = [];
      queryRows.value = [{ ulid: ulid(), key: '', value: '', isRequired: false }];
      kvpRows.value = [];
      rawText.value = '';
    }
    // 末尾空白行由通用表格组件 kvp-table 自行维护
  },
  { immediate: true },
);

onMounted(() => {
  // 末尾空白行由通用表格组件 kvp-table 自行维护
});

// ================= 路径输入：输入 "{" 时弹出字段选择器 =================
// 已在上方提前声明，避免声明顺序问题
watch(
  () => form.value.path,
  (newVal, oldVal) => {
    if (!pathAutoPopupEnabled.value) return;
    const count = (s: string) => (s.match(/\{/g) || []).length;
    try {
      const prev = String(oldVal || '');
      const curr = String(newVal || '');
      if (count(curr) > count(prev)) {
        braceInsertIndex.value = String(newVal || '').lastIndexOf('{');
        openPathSelector();
        return;
      }
      // 处理删除：当路径中的 {name} 被移除时，同步移除“路径参数”列表对应项并提示
      const prevSet = new Set<string>(extractPathParamNames(prev));
      const currSet = new Set<string>(extractPathParamNames(curr));
      const removed: string[] = [];
      prevSet.forEach((p) => {
        if (!currSet.has(p)) removed.push(p);
      });
      if (removed.length > 0) {
        pathRows.value = (pathRows.value || []).filter((r) => !removed.includes(String(r.key || '').trim()));
        const msg =
          removed.length === 1
            ? t('pages.apiDefinition.drawer.pathParamRemoved', { name: removed[0] })
            : t('pages.apiDefinition.drawer.pathParamRemovedMultiple', { names: removed.join(', ') });
        void MessagePlugin.info(msg);
      }
    } catch {
      // ignore
    }
  },
);

// ================= 当删除“路径参数”行时，同步移除路径中的 {name} =================
const prevPathRowKeys = ref<string[]>([]);
watch(
  () => (pathRows.value || []).map((r) => String(r?.key || '').trim()),
  (currKeys) => {
    try {
      const prevSet = new Set<string>((prevPathRowKeys.value || []).filter(Boolean));
      const currSet = new Set<string>((currKeys || []).filter(Boolean));
      const removed: string[] = [];
      prevSet.forEach((k) => {
        if (!currSet.has(k)) removed.push(k);
      });
      if (removed.length > 0) {
        // 避免触发路径监听里的自动弹窗/二次同步
        pathAutoPopupEnabled.value = false;
        try {
          let p = String(form.value.path || '');
          removed.forEach((name) => {
            const token = `{${name}}`;
            // 全量移除所有出现的 {name}
            while (p.includes(token)) p = p.replace(token, '');
          });
          // 规范化多余的斜杠（可选）：将重复斜杠折叠，保留协议中的双斜杠不处理（这里是相对路径，直接折叠）
          p = p.replace(/\/+/, '/');
          form.value.path = p;
        } finally {
          void nextTick(() => (pathAutoPopupEnabled.value = true));
        }
      }
    } finally {
      prevPathRowKeys.value = currKeys.slice();
    }
  },
  { immediate: true, deep: true },
);

const pathSelector = reactive({ visible: false, keyword: '' });
const pathSelectedRowKeys = ref<Array<string | number>>([]);
const pathPagination = reactive({ pageSize: 10, total: 0, current: 1 });
const pathLoading = ref(false);
const pathListData = ref<any[]>([]);

const pathSelectorColumns: PrimaryTableCol[] = [
  { title: t('pages.apiDefinition.drawer.selector.name'), colKey: 'bizField.name', ellipsis: true },
  { title: t('pages.apiDefinition.drawer.selector.domain'), colKey: 'bizDomain.name', width: 160 },
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

const pathSelectorTableColumns: PrimaryTableCol[] = [
  { colKey: 'row-select', type: 'multiple', width: 52, checkProps: ({ row }: any) => ({ disabled: isPathRowDisabled(row) }) },
  ...pathSelectorColumns,
];

function getRowBizFieldDomainId(row: any): string | number | undefined {
  return row?.bizFieldDomain?.id ?? row?.bizField?.id ?? row?.id;
}

function pathRowClassName({ row }: any) {
  const existing = new Set((pathRows.value || []).map((r: any) => r?.bizFieldDomainId).filter(Boolean));
  const id = getRowBizFieldDomainId(row);
  return existing.has(id) ? 'is-disabled' : '';
}

function isPathRowDisabled(row: any): boolean {
  const id = getRowBizFieldDomainId(row);
  if (id == null) return false;
  const existing = new Set((pathRows.value || []).map((r: any) => r?.bizFieldDomainId).filter(Boolean));
  return existing.has(id);
}

function openPathSelector() {
  pathSelector.visible = true;
  pathPagination.current = 1;
  pathSelectedRowKeys.value = [];
  void fetchPathSelectorPage();
}

function onPathSelectorCancel() {
  const idx = braceInsertIndex.value;
  if (idx >= 0) {
    const p = String(form.value.path || '');
    form.value.path = p.slice(0, idx) + p.slice(idx + 1);
  }
  braceInsertIndex.value = -1;
  pathSelector.visible = false;
}

function onPathKeywordChange() {
  pathPagination.current = 1;
  void fetchPathSelectorPage();
}

function onPathPageChange(pageInfo: any) {
  pathPagination.current = pageInfo.current;
  if (pageInfo.pageSize) pathPagination.pageSize = pageInfo.pageSize as number;
  void fetchPathSelectorPage();
}

async function fetchPathSelectorPage() {
  pathLoading.value = true;
  try {
    const rs = await request.post<{ rows: any[]; total: number }>({
      url: '/sr/biz-field-domain/page',
      data: {
        current: pathPagination.current,
        pageSize: pathPagination.pageSize,
        keyword: pathSelector.keyword,
        // 后端过滤：路径参数不允许 OBJECT/FILE，集合类型仅 NONE
        notBasicFieldTypes: ['OBJECT', 'FILE'],
        collectionType: 'NONE',
      },
    });
    pathListData.value = rs.rows || [];
    pathPagination.total = rs.total || 0;
    updatePathPreselectedForCurrentPage();
  } finally {
    pathLoading.value = false;
  }
}

function updatePathPreselectedForCurrentPage() {
  try {
    const toSelect = new Set<any>(pathSelectedRowKeys.value as any[]);
    (pathListData.value || []).forEach((row: any) => {
      if (isPathRowDisabled(row)) {
        const id = getRowBizFieldDomainId(row);
        if (id != null) toSelect.add(id);
      }
    });
    pathSelectedRowKeys.value = Array.from(toSelect);
  } catch {
    // ignore
  }
}

function onPathSelectorConfirm() {
  const selectedRows = (pathListData.value || []).filter((r: any) =>
    pathSelectedRowKeys.value.includes((r?.bizFieldDomain?.id ?? r?.bizField?.id ?? r?.id) as any),
  );
  const existingIds = new Set<string>((pathRows.value || [])
    .map((x: any) => (x?.bizFieldDomainId == null ? null : String(x.bizFieldDomainId)))
    .filter(Boolean) as string[]);
  selectedRows.forEach((row: any) => {
    const id = String(getRowBizFieldDomainId(row));
    if (existingIds.has(id)) return;
    applyPathField(row);
    existingIds.add(id);
  });
}

function applyPathField(row: any) {
  const name = row?.bizField?.name ?? row?.name ?? '';
  const idx = braceInsertIndex.value;
  const p = String(form.value.path || '');
  if (idx >= 0) {
    form.value.path = `${p.slice(0, idx)}{${name}}${p.slice(idx + 1)}`;
  } else {
    form.value.path = `${p}{${name}}`;
  }
  braceInsertIndex.value = -1;

  // 规范化“路径参数”列表：移除中间的空行，仅保留一个末尾空行
  const isEmptyRow = (r: any) =>
    String(r?.key || '').trim() === '' && String(r?.value || '').trim() === '' && !r?.isRequired;
  const existing = (pathRows.value || []).filter((r) => !isEmptyRow(r));
  const exists = existing.some((x) => String(x.key).trim() === String(name).trim());
  // 记录 bizFieldDomainId，便于提交携带
  const bizFieldDomainId = row?.id ?? row?.bizFieldDomain?.id ?? row?.bizField?.id;
  let next = exists
    ? existing
    : [...existing, { ulid: ulid(), key: name, value: '', isRequired: true, bizFieldDomainId } as any];
  // 确保仅有一个尾部空行
  next = [...next, { ulid: ulid(), key: '', value: '', isRequired: false }];
  pathRows.value = next;

  activeCategory.value = 'path';
  pathSelector.visible = false;
}

function onPathSelectorRowClick(params: any) {
  const row = params?.row;
  const id = getRowBizFieldDomainId(row);
  const existing = new Set((pathRows.value || []).map((r: any) => r?.bizFieldDomainId).filter(Boolean));
  if (existing.has(id)) {
    void MessagePlugin.info(t('pages.apiDefinition.drawer.alreadySelected'));
    return;
  }
  applyPathField(row);
}
</script>
<style scoped>
.footer {
  display: flex;
  justify-content: flex-end;
}

.biz-code-panel {
  padding: 16px 0;
}

.inline-label {
  color: var(--td-text-color-primary);
  font-size: 14px;
  white-space: nowrap;
}

/* Tabs 与面板内容之间增加上下间距，缓解紧贴分隔线的视觉压力 */
:deep(.t-tabs__content) {
  padding: 10px 0;
}

/* no extra overrides */
</style>

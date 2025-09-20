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

      <!-- 类别切换：请求参数 / 请求体 -->
      <t-tabs v-model="activeCategory">
        <t-tab-panel :label="t('pages.apiDefinition.drawer.tabs.params')" value="params">
          <t-form-item :label="t('pages.apiDefinition.drawer.queryParams')">
            <j-kvp-table v-model:rows="queryRows" hide-delete-for-last-empty />
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
            <j-kvp-table v-model:rows="kvpRows" hide-delete-for-last-empty />
          </template>

          <template v-if="form.bodyType === 'RAW_TEXT'">
            <t-textarea
              v-model="rawText"
              :height="240"
              :placeholder="t('pages.apiDefinition.drawer.rawTextPlaceholder')"
            />
          </template>

          <template v-if="form.bodyType === 'RAW_JSON'">
            <t-space direction="vertical" style="width: 100%">
              <j-tree-data
                ref="jTreeRef"
                :fetch-page="fetchBizFieldDomainPage"
                :columns="jsonFieldColumns"
                row-key="bizFieldDomain.id"
                selection="multiple"
                custom-field="bizFieldDomainId"
                :get-custom-value="(row: any) => row.bizFieldDomain.id"
                :default-page-size="10"
              />
            </t-space>
          </template>

          <template v-if="form.bodyType === 'BINARY'">
            <t-alert theme="info" :message="t('pages.apiDefinition.drawer.binaryTip')" />
          </template>
        </t-tab-panel>
      </t-tabs>

      <div class="footer">
        <t-space>
          <t-button theme="default" @click="$emit('close')">{{ t('pages.apiDefinition.drawer.close') }}</t-button>
          <t-button v-if="mode === 'create'" theme="primary" type="submit" :loading="submitting"
            >{{ t('pages.apiDefinition.drawer.save') }}
          </t-button>
        </t-space>
      </div>
    </t-form>
  </t-space>
</template>
<script setup lang="ts">
import type { FormInstanceFunctions, PrimaryTableCol } from 'tdesign-vue-next';
import { MessagePlugin } from 'tdesign-vue-next';
import { ulid } from 'ulid';
import { onMounted, ref, watch } from 'vue';

import { createApiDefinition } from '@/api/apiDefinition';
import type {
  ApiDefinitionFieldDTO,
  ApiDefinitionModel,
  BodyType,
  CreateApiDefinitionRequest,
  FieldType,
  HttpMethod,
} from '@/api/model/apiDefinitionModel';
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
// 类别页签：请求参数 / 请求体
const activeCategory = ref<'params' | 'body'>('params');
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

// Query 列表：始终保留一行空白行
const queryRows = ref<KVPRow[]>([]);
// 列由 kvp-table 定义

// 行行为改由 kvp-table 统一处理

// RAW 文本
const rawText = ref('');

// RAW JSON 使用 j-tree-data 选择 BizFieldDomain 构建树
const jTreeRef = ref<InstanceType<typeof JTreeData> | null>(null);
const jsonFieldColumns: PrimaryTableCol[] = [
  { colKey: 'row-select', type: 'multiple' },
  { title: '字段名', colKey: 'bizField.name', ellipsis: true },
  { title: '类型', colKey: 'bizFieldType.basicFieldType', ellipsis: true },
  { title: '集合', colKey: 'bizFieldType.collectionType', ellipsis: true },
  { title: '最小', colKey: 'bizFieldType.minimum', ellipsis: true },
  { title: '最大', colKey: 'bizFieldType.maximum', ellipsis: true },
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
  // 为避免 ESLint 报告未转义的 '}'，将正则中的 '}' 转义
  const pathParams = Array.from(form.value.path.matchAll(/\{(.*?)\}/g))
    .map((m) => m[1])
    .filter(Boolean);
  pathParams.forEach((p, idx) => {
    list.push({ fieldType: 'PATH', ulid: ulid(), parentUlid: null, sortOrder: idx, isRequired: true, description: p });
  });

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
      form.value.name = val.name;
      form.value.path = val.path;
      form.value.method = val.method as HttpMethod;
      form.value.bodyType = val.bodyType as BodyType;
      form.value.description = val.description || '';
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
</script>
<style scoped>
.footer {
  display: flex;
  justify-content: flex-end;
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

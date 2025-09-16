<template>
  <t-card :bordered="false">
    <t-space direction="vertical" style="width: 100%">
      <j-tree-data
        ref="jTreeRef"
        :fetch-page="fetchPage"
        :columns="columns"
        row-key="id"
        selection="multiple"
        custom-field="bizFieldDomainId"
        :get-custom-value="(row) => row.id"
        :default-page-size="10"
        :page-size-options="[1, 20, 520]"
        @change="onListChange"
      />
    </t-space>
  </t-card>
</template>
<script lang="ts" setup>
import type { PrimaryTableCol } from 'tdesign-vue-next';
import { ref } from 'vue';

import JTreeData from '@/components/j-tree-data/index.vue';
import { request } from '@/utils/request';

defineOptions({ name: 'JTreeDataDemo' });

const jTreeRef = ref<InstanceType<typeof JTreeData> | null>(null);
const listPreview = ref('');

const columns: PrimaryTableCol[] = [
  { colKey: 'row-select', type: 'multiple' },
  { title: '名称', colKey: 'name', ellipsis: true },
  { title: '集合类型', colKey: 'collectionType', ellipsis: true },
  { title: '描述', colKey: 'description', ellipsis: true },
  { title: '最小值', colKey: 'minimum', ellipsis: true },
  { title: '最大值', colKey: 'maximum', ellipsis: true },
];

// 演示分页数据接口：优先复用现有字段类型分页接口，如需更换可在这里替换
async function fetchPage(params: { current: number; pageSize: number; keyword?: string }) {
  // 这里使用已有的字段类型分页接口做演示；若你有其它分页接口，替换 url 即可
  return request.post<{ rows: any[]; total: number }>({
    url: '/sr/biz-field-type/page',
    data: { current: params.current, pageSize: params.pageSize, keyword: params.keyword },
  });
}

function onListChange(list: any[]) {
  listPreview.value = JSON.stringify(list, null, 2);
}
</script>
<style scoped></style>


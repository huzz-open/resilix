<template>
  <t-card :bordered="false">
    <t-space direction="vertical" style="width: 100%">
      <t-alert theme="info" message="选择任意分页数据插入树中，最终以List返回" />
      <j-tree-data
        ref="jTreeRef"
        :fetch-page="fetchPage"
        :columns="columns"
        row-key="id"
        selection="multiple"
        custom-field="bizFieldDomainId"
        :get-custom-value="(row)=>row.id"
        :default-page-size="10"
        :page-size-options="[1,20,520]"
        @change="onListChange"
      />
      <t-space>
        <t-button theme="primary" @click="showList">输出List</t-button>
      </t-space>
      <t-textarea v-model="listPreview" autosize placeholder="这里会展示 build 出来的 List" />
    </t-space>
  </t-card>

</template>

<script lang="ts" setup>
import { ref } from 'vue';
import JTreeData from '@/components/j-tree-data/index.vue';
import type { PrimaryTableCol } from 'tdesign-vue-next';
import { request } from '@/utils/request';

defineOptions({ name: 'JTreeDataDemo' });

const jTreeRef = ref<InstanceType<typeof JTreeData> | null>(null);
const listPreview = ref('');

const columns: PrimaryTableCol[] = [
  { colKey: 'row-select', type: 'multiple', width: 64, fixed: 'left' },
  { title: 'ID', colKey: 'id', width: 120 },
  { title: '名称', colKey: 'name', width: 240 },
  { title: '描述', colKey: 'description', ellipsis: true },
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

function showList() {
  if (!jTreeRef.value) return;
  const list = jTreeRef.value.getList();
  listPreview.value = JSON.stringify(list, null, 2);
}
</script>

<style scoped>
</style>



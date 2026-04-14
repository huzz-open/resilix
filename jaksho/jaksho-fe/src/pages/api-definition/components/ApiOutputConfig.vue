<template>
  <div class="api-output-config">
    <t-alert theme="info" message="选择响应字段来定义响应体结构，推荐使用字段属性为【可作为输出】的字段" closable />
    
    <!-- 双区域布局 -->
    <div class="config-container">
      <!-- 左侧：树形结构区域 -->
      <div class="tree-area">
        <j-tree-data
          ref="jTreeRef"
          v-model:tree-dto-list="responseTreeDTO"
          :fetch-page="fetchBizFieldDomainPage"
          :columns="responseFieldColumns"
          row-key="bizFieldDomain.id"
          selection="multiple"
          custom-field="bizFieldDomainId"
          :get-custom-value="(row: any) => row.bizFieldDomain.id"
          :default-page-size="10"
        />
      </div>
      
      <!-- 右侧：SLOT映射配置区域 -->
      <div v-if="slotFields.length > 0" class="slot-mapping-area">
        <div class="mapping-header">
          <t-icon name="info-circle" />
          <span>{{ t('components.jTreeData.slotMapping.title') }}</span>
          <t-tag v-if="unmappedCount > 0" theme="warning">{{ unmappedCount }} {{ t('components.jTreeData.slotMapping.unmapped') }}</t-tag>
          <t-tag v-else theme="success">全部已配置</t-tag>
        </div>
        
        <div class="mapping-list">
          <div
            v-for="slot in slotFields"
            :key="slot.ulid"
            class="mapping-item"
            :class="{ 'unmapped': !slot.mappedFieldDomainId }"
          >
            <div class="slot-info">
              <t-tag theme="primary" size="small">{{ t('components.jTreeData.slotMapping.slotTag') }}</t-tag>
              <span class="slot-name">{{ slot.fieldName }}</span>
              <span class="slot-path">{{ slot.fieldPath }}</span>
            </div>
            
            <div class="mapping-config">
              <t-button
                size="small"
                variant="outline"
                @click="openMappingSelector(slot)"
              >
                {{ slot.mappedFieldDomainId ? t('components.jTreeData.slotMapping.changeMapping') : t('components.jTreeData.slotMapping.selectMapping') }}
              </t-button>
              
              <div v-if="slot.mappedFieldDomainId" class="mapped-info">
                <t-icon name="check-circle" style="color: var(--td-success-color)" />
                <span>{{ slot.mappedFieldName }}</span>
                <t-button
                  theme="danger"
                  variant="text"
                  size="small"
                  @click="clearMapping(slot)"
                >
                  {{ t('components.jTreeData.slotMapping.clearMapping') }}
                </t-button>
              </div>
              
              <div v-else class="unmapped-warning">
                <t-icon name="error-circle" style="color: var(--td-warning-color)" />
                <span>{{ t('components.jTreeData.slotMapping.unmappedWarning') }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <t-collapse style="margin-top: 16px;" :default-value="[]">
      <t-collapse-panel header="使用提示" value="tips">
        <ul style="margin: 0; padding-left: 20px; font-size: 13px; color: var(--td-text-color-secondary);">
          <li>在字段管理中创建字段时，可设置"字段属性"为"可作为输出"，这样的字段会自动标记为响应字段</li>
          <li>如果需要创建同名但不同类型的字段（如 data 字段既有 STRING 类型又有 SLOT 类型），可以使用"添加领域字段"功能，在"插槽领域"下创建 SLOT 类型版本</li>
          <li>SLOT 类型字段可作为占位符使用，便于在不同接口中灵活映射到不同的实际字段</li>
          <li>响应字段支持 OBJECT 类型，可以定义嵌套的响应结构</li>
        </ul>
      </t-collapse-panel>
    </t-collapse>
    
    <!-- 映射字段选择对话框 -->
    <t-dialog
      v-model:visible="mappingSelectorVisible"
      :header="mappingDialogTitle"
      width="900px"
      :cancel-btn="t('components.jTreeData.cancel')"
      :confirm-btn="t('components.jTreeData.confirm')"
      @confirm="confirmMapping"
    >
      <t-space direction="vertical" style="width: 100%">
        <t-input
          v-model="mappingKeyword"
          :placeholder="t('components.jTreeData.searchPlaceholder')"
          style="width: 260px"
          @change="onMappingKeywordChange"
        >
          <template #suffix-icon>
            <search-icon size="16px" />
          </template>
        </t-input>
        
        <t-table
          v-model:selected-row-keys="selectedMappingKeys"
          select-on-row-click
          :data="mappingCandidates"
          :columns="mappingColumns"
          :pagination="mappingPagination"
          :loading="mappingLoading"
          :hover="true"
          row-key="bizFieldDomain.id"
          @page-change="onMappingPageChange"
        >
          <template #empty>
            <div style="padding: 20px;">暂无可选字段</div>
          </template>
        </t-table>
      </t-space>
    </t-dialog>
  </div>
</template>

<script setup lang="ts">
import { SearchIcon } from 'tdesign-icons-vue-next';
import type { PageInfo, PrimaryTableCol } from 'tdesign-vue-next';
import { MessagePlugin } from 'tdesign-vue-next';
import { computed, reactive, ref, watch } from 'vue';

import { getBizFieldDomainList } from '@/api/bizFieldDomain';
import JTreeData from '@/components/j-tree-data/index.vue';
import { t } from '@/locales';

interface Props {
  modelValue?: any[];
  workspaceId?: number;
}

interface Emits {
  (e: 'update:modelValue', value: any[]): void;
}

interface SlotFieldInfo {
  ulid: string;
  fieldName: string;
  fieldPath: string;
  basicFieldType: string;
  mappedFieldDomainId: number | null;
  mappedFieldName: string | null;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const jTreeRef = ref<InstanceType<typeof JTreeData>>();
const responseTreeDTO = ref<any[]>([]);

// 响应字段列配置
const responseFieldColumns: PrimaryTableCol[] = [
  { title: t('pages.apiDefinition.drawer.fieldName'), colKey: 'bizField.name', width: 200 },
  { title: t('pages.apiDefinition.drawer.fieldType'), colKey: 'bizFieldType.name', width: 200 },
  { title: t('pages.apiDefinition.drawer.selector.basicFieldType'), colKey: 'bizFieldType.basicFieldType', width: 120 },
  { title: t('pages.apiDefinition.drawer.fieldDescription'), colKey: 'bizField.description', ellipsis: true },
];

// 获取字段领域分页数据（获取所有字段，组件内会根据 fieldAttributes 进行位运算筛选）
async function fetchBizFieldDomainPage(params: any) {
  const res = await getBizFieldDomainList({
    ...params,
    workspaceId: props.workspaceId,
  });
  
  // 前端过滤：只显示可作为输出的字段（fieldAttributes & 2 > 0）
  if (res && res.rows) {
    res.rows = res.rows.filter((row: any) => {
      const attrs = row?.bizFieldDomain?.fieldAttributes || 3; // 默认为3（输入输出均可）
      return (attrs & 2) > 0; // 检查是否包含输出位
    });
  }
  
  return res;
}

// ==================== SLOT 字段检测 ====================

// 递归遍历树结构，提取所有 SLOT 字段
const slotFields = computed<SlotFieldInfo[]>(() => {
  const slots: SlotFieldInfo[] = [];
  
  const traverse = (nodes: any[], parentPath: string = '') => {
    if (!Array.isArray(nodes)) return;
    
    nodes.forEach((node) => {
      const fieldName = node.bizField?.name || '';
      const basicFieldType = node.bizFieldType?.basicFieldType;
      const currentPath = parentPath ? `${parentPath}.${fieldName}` : fieldName;
      
      // 如果是 SLOT 类型
      if (basicFieldType === 'SLOT') {
        slots.push({
          ulid: node.ulid,
          fieldName,
          fieldPath: currentPath,
          basicFieldType,
          mappedFieldDomainId: node.slotMappedFieldDomainId || null,
          mappedFieldName: node.slotMappedFieldName || null,
        });
      }
      
      // 递归遍历子节点（不限于 OBJECT 类型，所有有 children 的都遍历）
      if (node.children && Array.isArray(node.children) && node.children.length > 0) {
        traverse(node.children, currentPath);
      }
    });
  };
  
  traverse(responseTreeDTO.value);
  return slots;
});

// 计算未映射的数量
const unmappedCount = computed(() => {
  return slotFields.value.filter((s) => !s.mappedFieldDomainId).length;
});

// ==================== 映射选择器 ====================

const currentSlot = ref<SlotFieldInfo | null>(null);
const mappingSelectorVisible = ref(false);
const selectedMappingKeys = ref<number[]>([]);
const mappingCandidates = ref<any[]>([]);
const mappingLoading = ref(false);
const mappingKeyword = ref('');

const mappingPagination = reactive({
  pageSize: 10,
  total: 0,
  current: 1,
  pageSizeOptions: [10, 20, 50],
});

const mappingDialogTitle = computed(() => {
  if (!currentSlot.value) return '';
  return t('components.jTreeData.slotMapping.dialogTitle').replace('{name}', currentSlot.value.fieldName);
});

const mappingColumns: PrimaryTableCol[] = [
  { colKey: 'row-select', type: 'single', width: 50 },
  { title: t('components.jTreeData.slotMapping.fieldName'), colKey: 'bizField.name', width: 200 },
  { title: t('components.jTreeData.slotMapping.fieldType'), colKey: 'bizFieldType.name', width: 200 },
  { title: t('components.jTreeData.slotMapping.basicFieldType'), colKey: 'bizFieldType.basicFieldType', width: 120 },
  { title: t('components.jTreeData.slotMapping.fieldDescription'), colKey: 'bizField.description', ellipsis: true },
];

function openMappingSelector(slot: SlotFieldInfo) {
  currentSlot.value = slot;
  selectedMappingKeys.value = slot.mappedFieldDomainId ? [slot.mappedFieldDomainId] : [];
  mappingSelectorVisible.value = true;
  mappingKeyword.value = '';
  mappingPagination.current = 1;
  loadMappingCandidates();
}

function onMappingKeywordChange() {
  mappingPagination.current = 1;
  loadMappingCandidates();
}

function onMappingPageChange(pageInfo: PageInfo) {
  mappingPagination.current = pageInfo.current;
  if ((pageInfo as any).pageSize) {
    mappingPagination.pageSize = (pageInfo as any).pageSize as number;
  }
  loadMappingCandidates();
}

// 加载可映射的字段列表（排除 SLOT 类型）
async function loadMappingCandidates() {
  mappingLoading.value = true;
  try {
    const res = await getBizFieldDomainList({
      current: mappingPagination.current,
      pageSize: mappingPagination.pageSize,
      keyword: mappingKeyword.value,
    });
    
    // 过滤：1）可作为输出 2）非 SLOT 类型
    if (res && res.rows) {
      mappingCandidates.value = res.rows.filter((row: any) => {
        const attrs = row?.bizFieldDomain?.fieldAttributes || 3;
        const isOutput = (attrs & 2) > 0;
        const isNotSlot = row?.bizFieldType?.basicFieldType !== 'SLOT';
        return isOutput && isNotSlot;
      });
      mappingPagination.total = res.total || 0;
    } else {
      mappingCandidates.value = [];
      mappingPagination.total = 0;
    }
  } catch (error) {
    console.error('加载映射字段列表失败:', error);
    mappingCandidates.value = [];
    mappingPagination.total = 0;
  } finally {
    mappingLoading.value = false;
  }
}

// 确认映射
function confirmMapping() {
  if (selectedMappingKeys.value.length === 0 || !currentSlot.value) {
    MessagePlugin.warning(t('components.jTreeData.slotMapping.selectPrompt'));
    return;
  }
  
  const targetFieldDomainId = selectedMappingKeys.value[0];
  const targetField = mappingCandidates.value.find(
    (f) => f.bizFieldDomain.id === targetFieldDomainId,
  );
  
  if (!targetField) {
    MessagePlugin.warning('未找到选中的字段');
    return;
  }
  
  // 更新树数据中对应节点的映射信息
  updateSlotMapping(currentSlot.value.ulid, {
    slotMappedFieldDomainId: targetFieldDomainId,
    slotMappedFieldName: targetField?.bizField?.name || '',
  });
  
  mappingSelectorVisible.value = false;
  MessagePlugin.success(t('components.jTreeData.slotMapping.mappedSuccess'));
}

// 更新节点的映射信息
function updateSlotMapping(ulid: string, mapping: any) {
  const updateNode = (nodes: any[]): boolean => {
    if (!Array.isArray(nodes)) return false;
    
    for (const node of nodes) {
      if (node.ulid === ulid) {
        Object.assign(node, mapping);
        return true;
      }
      if (node.children && Array.isArray(node.children) && node.children.length > 0) {
        if (updateNode(node.children)) return true;
      }
    }
    return false;
  };
  
  updateNode(responseTreeDTO.value);
  // 触发更新
  responseTreeDTO.value = [...responseTreeDTO.value];
}

// 清除映射
function clearMapping(slot: SlotFieldInfo) {
  updateSlotMapping(slot.ulid, {
    slotMappedFieldDomainId: null,
    slotMappedFieldName: null,
  });
  MessagePlugin.success(t('components.jTreeData.slotMapping.clearSuccess'));
}

// ==================== 验证 ====================

// 验证所有 SLOT 字段是否已映射
function validateSlotMappings(): { valid: boolean; message?: string } {
  const unmapped = slotFields.value.filter((s) => !s.mappedFieldDomainId);
  
  if (unmapped.length === 0) {
    return { valid: true };
  }
  
  const unmappedNames = unmapped.map((s) => s.fieldName).join('、');
  return {
    valid: false,
    message: `以下插槽字段尚未配置映射：${unmappedNames}`,
  };
}

// 暴露给父组件的方法
defineExpose({
  validateSlotMappings,
});

// ==================== 数据同步 ====================

// 监听 tree 数据变化，同步到父组件
watch(
  () => responseTreeDTO.value,
  (newVal) => {
    emit('update:modelValue', newVal);
  },
  { deep: true },
);

// 监听父组件传入的值，回显到树组件
watch(
  () => props.modelValue,
  (newVal) => {
    console.log('ApiOutputConfig 接收到的 modelValue:', newVal);
    if (newVal && JSON.stringify(newVal) !== JSON.stringify(responseTreeDTO.value)) {
      responseTreeDTO.value = newVal;
      console.log('ApiOutputConfig 已更新 responseTreeDTO:', responseTreeDTO.value);
    }
  },
  { immediate: true, deep: true },
);
</script>

<style scoped lang="less">
.api-output-config {
  padding: 16px 0;
  
  .config-container {
    display: flex;
    gap: 16px;
    margin-top: 16px;
    
    .tree-area {
      flex: 6;
      min-width: 0;
    }
    
    .slot-mapping-area {
      flex: 4;
      min-width: 300px;
      border: 1px solid var(--td-border-level-1-color);
      border-radius: var(--td-radius-default);
      background: var(--td-bg-color-container);
      padding: 16px;
      max-height: 600px;
      overflow-y: auto;
      
      .mapping-header {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 16px;
        font-weight: 500;
        color: var(--td-text-color-primary);
      }
      
      .mapping-list {
        display: flex;
        flex-direction: column;
        gap: 12px;
      }
      
      .mapping-item {
        padding: 12px;
        border: 1px solid var(--td-border-level-2-color);
        border-radius: var(--td-radius-small);
        background: var(--td-bg-color-container-hover);
        
        &.unmapped {
          border-color: var(--td-warning-color);
          background: var(--td-warning-color-1);
        }
        
        .slot-info {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 8px;
          
          .slot-name {
            font-weight: 500;
          }
          
          .slot-path {
            font-size: 12px;
            color: var(--td-text-color-placeholder);
          }
        }
        
        .mapping-config {
          display: flex;
          align-items: center;
          gap: 8px;
          flex-wrap: wrap;
          
          .mapped-info {
            display: flex;
            align-items: center;
            gap: 4px;
            flex: 1;
            min-width: 0;
            
            span {
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
          }
          
          .unmapped-warning {
            display: flex;
            align-items: center;
            gap: 4px;
            color: var(--td-warning-color);
            font-size: 13px;
          }
        }
      }
    }
  }
}
</style>

<template>
  <div class="dashboard-container">
    <t-row :gutter="16">
      <!-- 统计卡片 -->
      <t-col :xs="12" :sm="6" :md="6" :lg="6" :xl="6">
        <t-card :bordered="false" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon field-type-icon">
              <t-icon name="view-module" size="32px" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.fieldTypeCount }}</div>
              <div class="stat-label">字段类型</div>
            </div>
          </div>
        </t-card>
      </t-col>
      
      <t-col :xs="12" :sm="6" :md="6" :lg="6" :xl="6">
        <t-card :bordered="false" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon field-icon">
              <t-icon name="layers" size="32px" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.fieldCount }}</div>
              <div class="stat-label">字段</div>
            </div>
          </div>
        </t-card>
      </t-col>
      
      <t-col :xs="12" :sm="6" :md="6" :lg="6" :xl="6">
        <t-card :bordered="false" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon api-icon">
              <t-icon name="api" size="32px" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.apiCount }}</div>
              <div class="stat-label">接口</div>
            </div>
          </div>
        </t-card>
      </t-col>
      
      <t-col :xs="12" :sm="6" :md="6" :lg="6" :xl="6">
        <t-card :bordered="false" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon service-icon">
              <t-icon name="server" size="32px" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.serviceCount }}</div>
              <div class="stat-label">服务</div>
            </div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <t-row :gutter="16" style="margin-top: 16px;">
      <!-- 快速入口 -->
      <t-col :span="12">
        <t-card :title="'快速入口'" :bordered="false">
          <div class="quick-links">
            <t-button theme="primary" variant="outline" @click="navigateTo('BizFieldTypeList')">
              <template #icon><t-icon name="view-module" /></template>
              创建字段类型
            </t-button>
            <t-button theme="primary" variant="outline" @click="navigateTo('BizFieldList')">
              <template #icon><t-icon name="layers" /></template>
              创建字段
            </t-button>
            <t-button theme="primary" variant="outline" @click="navigateTo('ApiDefinitionList')">
              <template #icon><t-icon name="api" /></template>
              创建接口
            </t-button>
            <t-button theme="primary" variant="outline" @click="navigateTo('ServiceList')">
              <template #icon><t-icon name="server" /></template>
              创建服务
            </t-button>
          </div>
        </t-card>
      </t-col>

      <!-- 使用指南 -->
      <t-col :span="12">
        <t-card :title="'使用指南'" :bordered="false">
          <div class="guide-steps">
            <div class="guide-step">
              <div class="step-number">1</div>
              <div class="step-content">
                <div class="step-title">定义字段类型</div>
                <div class="step-desc">创建可复用的字段类型模板，如：用户名、手机号等</div>
              </div>
            </div>
            <div class="guide-step">
              <div class="step-number">2</div>
              <div class="step-content">
                <div class="step-title">创建字段</div>
                <div class="step-desc">使用字段类型定义具体字段，如：user_id、username</div>
              </div>
            </div>
            <div class="guide-step">
              <div class="step-number">3</div>
              <div class="step-content">
                <div class="step-title">设计接口</div>
                <div class="step-desc">使用字段组装成 API 接口的请求和响应</div>
              </div>
            </div>
          </div>
        </t-card>
      </t-col>
    </t-row>

    <t-row :gutter="16" style="margin-top: 16px;">
      <!-- 扩展功能 -->
      <t-col :span="24">
        <t-card :title="'扩展功能'" :bordered="false">
          <t-row :gutter="16">
            <t-col :span="6">
              <div class="feature-item" @click="navigateTo('ValueDictList')">
                <t-icon name="book" size="24px" />
                <div class="feature-title">值字典</div>
                <div class="feature-desc">定义枚举类型，增强字段类型</div>
              </div>
            </t-col>
            <t-col :span="6">
              <div class="feature-item" @click="navigateTo('BizDomainList')">
                <t-icon name="dashboard" size="24px" />
                <div class="feature-title">业务领域</div>
                <div class="feature-desc">定义业务分类，组织大量字段</div>
              </div>
            </t-col>
            <t-col :span="6">
              <div class="feature-item" @click="navigateTo('ServiceList')">
                <t-icon name="server" size="24px" />
                <div class="feature-title">服务</div>
                <div class="feature-desc">定义微服务和服务码</div>
              </div>
            </t-col>
            <t-col :span="6">
              <div class="feature-item" @click="navigateTo('BizCodeList')">
                <t-icon name="code" size="24px" />
                <div class="feature-title">业务码</div>
                <div class="feature-desc">定义规范化的业务错误码</div>
              </div>
            </t-col>
          </t-row>
        </t-card>
      </t-col>
    </t-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive } from 'vue';
import { useRouter } from 'vue-router';

defineOptions({
  name: 'DashboardBase',
});

const router = useRouter();

// 统计数据
const statistics = reactive({
  fieldTypeCount: 0,
  fieldCount: 0,
  apiCount: 0,
  serviceCount: 0,
});

// 模拟加载统计数据
const loadStatistics = () => {
  // 这里可以调用实际的 API
  setTimeout(() => {
    statistics.fieldTypeCount = 12;
    statistics.fieldCount = 45;
    statistics.apiCount = 28;
    statistics.serviceCount = 5;
  }, 500);
};

const navigateTo = (name: string) => {
  router.push({ name });
};

onMounted(() => {
  loadStatistics();
});
</script>

<style scoped lang="less">
.dashboard-container {
  padding: 16px;
}

.stat-card {
  height: 100%;
  
  :deep(.t-card__body) {
    padding: 20px;
  }
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.field-type-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.field-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.api-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.service-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: var(--td-text-color-primary);
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: var(--td-text-color-secondary);
  margin-top: 4px;
}

.quick-links {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.guide-steps {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.guide-step {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.step-number {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--td-brand-color);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  flex-shrink: 0;
}

.step-content {
  flex: 1;
}

.step-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--td-text-color-primary);
  margin-bottom: 4px;
}

.step-desc {
  font-size: 14px;
  color: var(--td-text-color-secondary);
}

.feature-item {
  padding: 20px;
  border: 1px solid var(--td-component-border);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  text-align: center;
  
  &:hover {
    border-color: var(--td-brand-color);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }
}

.feature-title {
  font-size: 16px;
  font-weight: 500;
  color: var(--td-text-color-primary);
  margin: 12px 0 8px;
}

.feature-desc {
  font-size: 14px;
  color: var(--td-text-color-secondary);
  line-height: 1.5;
}
</style>

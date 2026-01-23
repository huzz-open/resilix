import {AppIcon} from 'tdesign-icons-vue-next';
import {shallowRef} from 'vue';

import Layout from '@/layouts/index.vue';

export default [
  // 标准资源父菜单
  {
    path: '/standard-resource',
    component: Layout,
    redirect: '/standard-resource/field-type',
    name: 'standard-resource',
    meta: {
      title: {
        zh_CN: '标准资源',
        en_US: 'Standard Resource',
      },
      icon: shallowRef(AppIcon),
      orderNo: 1,
    },
    children: [
      // 核心资源：字段类型
      {
        path: 'field-type',
        name: 'BizFieldTypeList',
        component: () => import('@/pages/biz-field-type/list/index.vue'),
        meta: {
          title: {
            zh_CN: '字段类型',
            en_US: 'Field Type',
          },
          orderNo: 0,
        },
      },
      {
        path: 'field-type/create',
        name: 'BizFieldTypeCreate',
        component: () => import('@/pages/biz-field-type/edit/index.vue'),
        meta: {
          title: {
            zh_CN: '新建字段类型',
            en_US: 'Create Field Type',
          },
          keepAlive: false,
          hidden: true,
        },
      },
      {
        path: 'field-type/edit/:id',
        name: 'BizFieldTypeEdit',
        component: () => import('@/pages/biz-field-type/edit/index.vue'),
        meta: {
          title: {
            zh_CN: '编辑字段类型',
            en_US: 'Edit Field Type',
          },
          keepAlive: false,
          hidden: true,
        },
      },

      // 核心资源：字段
      {
        path: 'field',
        name: 'BizFieldList',
        component: () => import('@/pages/biz-field/list/index.vue'),
        meta: {
          title: {
            zh_CN: '字段',
            en_US: 'Field',
          },
          orderNo: 1,
        },
      },
      {
        path: 'field/create',
        name: 'BizFieldCreate',
        component: () => import('@/pages/biz-field/edit/index.vue'),
        meta: {
          title: {
            zh_CN: '新建字段',
            en_US: 'Create Field',
          },
          keepAlive: false,
          hidden: true,
        },
      },
      {
        path: 'field/edit/:id',
        name: 'BizFieldEdit',
        component: () => import('@/pages/biz-field/edit/index.vue'),
        meta: {
          title: {
            zh_CN: '编辑字段',
            en_US: 'Edit Field',
          },
          keepAlive: false,
          hidden: true,
        },
      },

      // 核心资源：接口
      {
        path: 'api',
        name: 'ApiDefinitionList',
        component: () => import('@/pages/api-definition/list/index.vue'),
        meta: {
          title: {
            zh_CN: '接口',
            en_US: 'API',
          },
          orderNo: 2,
        },
      },
      {
        path: 'api/create',
        name: 'ApiDefinitionCreate',
        component: () => import('@/pages/api-definition/edit/index.vue'),
        meta: {
          title: {
            zh_CN: '新建接口',
            en_US: 'Create API',
          },
          keepAlive: false,
          hidden: true,
        },
      },
      {
        path: 'api/edit/:id',
        name: 'ApiDefinitionEdit',
        component: () => import('@/pages/api-definition/edit/index.vue'),
        meta: {
          title: {
            zh_CN: '编辑接口',
            en_US: 'Edit API',
          },
          keepAlive: false,
          hidden: true,
        },
      },

      // 扩展资源：值字典
      {
        path: 'value-dict',
        name: 'ValueDictList',
        component: () => import('@/pages/value-dict/list/index.vue'),
        meta: {
          title: {
            zh_CN: '值字典',
            en_US: 'Value Dictionary',
          },
          orderNo: 3,
        },
      },
      {
        path: 'value-dict/create',
        name: 'ValueDictCreate',
        component: () => import('@/pages/value-dict/edit/index.vue'),
        meta: {
          title: {
            zh_CN: '新建字典',
            en_US: 'Create Dictionary',
          },
          keepAlive: false,
          hidden: true,
        },
      },
      {
        path: 'value-dict/edit/:id',
        name: 'ValueDictEdit',
        component: () => import('@/pages/value-dict/edit/index.vue'),
        meta: {
          title: {
            zh_CN: '编辑字典',
            en_US: 'Edit Dictionary',
          },
          keepAlive: false,
          hidden: true,
        },
      },

      // 扩展资源：业务领域
      {
        path: 'biz-domain',
        name: 'BizDomainList',
        component: () => import('@/pages/biz-domain/list/index.vue'),
        meta: {
          title: {
            zh_CN: '业务领域',
            en_US: 'Business Domain',
          },
          orderNo: 4,
        },
      },
      {
        path: 'biz-domain/create',
        name: 'BizDomainCreate',
        component: () => import('@/pages/biz-domain/edit/index.vue'),
        meta: {
          title: {
            zh_CN: '新建业务领域',
            en_US: 'Create Business Domain',
          },
          keepAlive: false,
          hidden: true,
        },
      },
      {
        path: 'biz-domain/edit/:id',
        name: 'BizDomainEdit',
        component: () => import('@/pages/biz-domain/edit/index.vue'),
        meta: {
          title: {
            zh_CN: '编辑业务领域',
            en_US: 'Edit Business Domain',
          },
          keepAlive: false,
          hidden: true,
        },
      },

      // 扩展资源：服务
      {
        path: 'service',
        name: 'ServiceList',
        component: () => import('@/pages/service/list/index.vue'),
        meta: {
          title: {
            zh_CN: '服务',
            en_US: 'Service',
          },
          orderNo: 5,
        },
      },
      {
        path: 'service/create',
        name: 'ServiceCreate',
        component: () => import('@/pages/service/edit/index.vue'),
        meta: {
          title: {
            zh_CN: '新建服务',
            en_US: 'Create Service',
          },
          keepAlive: false,
          hidden: true,
        },
      },
      {
        path: 'service/edit/:id',
        name: 'ServiceEdit',
        component: () => import('@/pages/service/edit/index.vue'),
        meta: {
          title: {
            zh_CN: '编辑服务',
            en_US: 'Edit Service',
          },
          keepAlive: false,
          hidden: true,
        },
      },

      // 扩展资源：业务码
      {
        path: 'biz-code',
        name: 'BizCodeList',
        component: () => import('@/pages/biz-code/list/index.vue'),
        meta: {
          title: {
            zh_CN: '业务码',
            en_US: 'Business Code',
          },
          orderNo: 6,
        },
      },
      {
        path: 'biz-code/create',
        name: 'BizCodeCreate',
        component: () => import('@/pages/biz-code/edit/index.vue'),
        meta: {
          title: {
            zh_CN: '新建业务码',
            en_US: 'Create Business Code',
          },
          keepAlive: false,
          hidden: true,
        },
      },
      {
        path: 'biz-code/edit/:id',
        name: 'BizCodeEdit',
        component: () => import('@/pages/biz-code/edit/index.vue'),
        meta: {
          title: {
            zh_CN: '编辑业务码',
            en_US: 'Edit Business Code',
          },
          keepAlive: false,
          hidden: true,
        },
      },
    ],
  },
];

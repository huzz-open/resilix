import { DashboardIcon } from 'tdesign-icons-vue-next';
import { shallowRef } from 'vue';

import Layout from '@/layouts/index.vue';

export default [
  {
    path: '/api-definition',
    component: Layout,
    redirect: '/api-definition/list',
    name: 'api-definition',
    meta: {
      title: {
        zh_CN: '接口设计',
        en_US: 'API Definition',
      },
      icon: shallowRef(DashboardIcon),
      orderNo: 4,
    },
    children: [
      {
        path: 'biz-code',
        name: 'BizCodeList',
        component: () => import('@/pages/biz-code/list/index.vue'),
        meta: {
          title: {
            zh_CN: '业务码管理',
            en_US: 'Business Code',
          },
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
      {
        path: 'list',
        name: 'ApiDefinitionList',
        component: () => import('@/pages/api-definition/list/index.vue'),
        meta: {
          title: {
            zh_CN: '接口列表',
            en_US: 'API List',
          },
        },
      },
      {
        path: 'create',
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
        path: 'edit/:id',
        name: 'ApiDefinitionEdit',
        component: () => import('@/pages/api-definition/edit/index.vue'),
        meta: {
          title: {
            zh_CN: '接口详情',
            en_US: 'API Detail',
          },
          keepAlive: false,
          hidden: true,
        },
      },
    ],
  },
];

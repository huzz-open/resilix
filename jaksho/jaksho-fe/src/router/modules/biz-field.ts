import { DashboardIcon } from 'tdesign-icons-vue-next';
import { shallowRef } from 'vue';

import Layout from '@/layouts/index.vue';

export default [
  {
    path: '/biz-field',
    component: Layout,
    redirect: '/biz-field/list',
    name: 'biz-field',
    meta: {
      title: {
        zh_CN: '字段管理',
        en_US: 'Field Management',
      },
      icon: shallowRef(DashboardIcon),
      orderNo: 3,
    },
    children: [
      {
        path: 'list',
        name: 'BizFieldList',
        component: () => import('@/pages/biz-field/list/index.vue'),
        meta: {
          title: {
            zh_CN: '字段列表',
            en_US: 'Field List',
          },
        },
      },
      {
        path: 'create',
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
        path: 'edit/:id',
        name: 'BizFieldEdit',
        component: () => import('@/pages/biz-field/edit/index.vue'),
        meta: {
          title: {
            zh_CN: '字段详情',
            en_US: 'Field Detail',
          },
          keepAlive: false,
          hidden: true,
        },
      },
    ],
  },
];

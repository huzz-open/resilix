import { DashboardIcon } from 'tdesign-icons-vue-next';
import { shallowRef } from 'vue';

import Layout from '@/layouts/index.vue';

export default [
  {
    path: '/biz-field-type',
    component: Layout,
    redirect: '/biz-field-type/list',
    name: 'biz-field-type',
    meta: {
      title: {
        zh_CN: '字段类型管理',
        en_US: 'Field Type Management',
      },
      icon: shallowRef(DashboardIcon),
      orderNo: 2,
    },
    children: [
      {
        path: 'list',
        name: 'BizFieldTypeList',
        component: () => import('@/pages/biz-field-type/list/index.vue'),
        meta: {
          title: {
            zh_CN: '字段类型列表',
            en_US: 'Field Type List',
          },
        },
      },
      {
        path: 'create',
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
        path: 'edit/:id',
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
    ],
  },
];

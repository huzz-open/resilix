import { DashboardIcon } from 'tdesign-icons-vue-next';
import { shallowRef } from 'vue';

import Layout from '@/layouts/index.vue';

export default [
  {
    path: '/biz-domain',
    component: Layout,
    redirect: '/biz-domain/list',
    name: 'biz-domain',
    meta: {
      title: {
        zh_CN: '业务领域管理',
        en_US: 'Business Domain Management',
      },
      icon: shallowRef(DashboardIcon),
      orderNo: 1,
    },
    children: [
      {
        path: 'list',
        name: 'BizDomainList',
        component: () => import('@/pages/biz-domain/list/index.vue'),
        meta: {
          title: {
            zh_CN: '业务领域列表',
            en_US: 'Business Domain List',
          },
        },
      },
      {
        path: 'create',
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
        path: 'edit/:id',
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
    ],
  },
];

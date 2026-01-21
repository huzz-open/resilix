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
    ],
  },
];

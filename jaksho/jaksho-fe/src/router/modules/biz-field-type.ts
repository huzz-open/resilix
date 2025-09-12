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
    ],
  },
];



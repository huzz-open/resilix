import { BookIcon } from 'tdesign-icons-vue-next';
import { shallowRef } from 'vue';

import Layout from '@/layouts/index.vue';

export default [
  {
    path: '/value-dict',
    component: Layout,
    redirect: '/value-dict/list',
    name: 'value-dict',
    meta: {
      title: {
        zh_CN: '值字典管理',
        en_US: 'Value Dictionary Management',
      },
      icon: shallowRef(BookIcon),
      orderNo: 5,
    },
    children: [
      {
        path: 'list',
        name: 'ValueDictList',
        component: () => import('@/pages/value-dict/list/index.vue'),
        meta: {
          title: {
            zh_CN: '字典列表',
            en_US: 'Dictionary List',
          },
        },
      },
    ],
  },
];

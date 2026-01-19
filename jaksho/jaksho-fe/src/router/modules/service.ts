import { ServerIcon } from 'tdesign-icons-vue-next'
import { shallowRef } from 'vue'

import Layout from '@/layouts/index.vue'

export default [
  {
    path: '/service',
    component: Layout,
    redirect: '/service/list',
    name: 'service',
    meta: {
      title: {
        zh_CN: '服务管理',
        en_US: 'Service Management',
      },
      icon: shallowRef(ServerIcon),
      orderNo: 0,
    },
    children: [
      {
        path: 'list',
        name: 'ServiceList',
        component: () => import('@/pages/service/list/index.vue'),
        meta: {
          title: {
            zh_CN: '服务列表',
            en_US: 'Service List',
          },
        },
      },
    ],
  },
]


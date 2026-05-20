import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      component: () => import('@/views/login/index.vue'),
      meta: { public: true },
    },
    {
      path: '/',
      component: () => import('@/layouts/default.vue'),
      children: [
        { path: '', redirect: '/dashboard' },
        { path: 'dashboard', component: () => import('@/views/dashboard/index.vue') },
        { path: 'graph', component: () => import('@/views/graph/list.vue') },
        { path: 'graph/:id/edit', component: () => import('@/views/graph/editor.vue') },
      ],
    },
  ],
})

router.beforeEach((to) => {
  const user = useUserStore()
  if (!to.meta.public && !user.token) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
})

export default router

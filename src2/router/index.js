import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    children: [
      { path: '/login', name: 'login', component: () => import('@/components/Login.vue') },
      { path: '/signup', name: 'signup', component: () => import('@/components/SignUp.vue') },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

export default router

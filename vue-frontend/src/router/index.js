import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import Profile from '@/views/Profile.vue'
import Java from '@/views/TermineJava.vue'
import Python from '@/views/KiPython.vue'
import Login from '@/views/Login.vue' // Add import
import AdminDashboard from '@/views/AdminDashboard.vue' // Add import
// Use dynamic import for store to avoid circular issues if any, or standard import
import { useAuthStore } from '@/stores/auth'

const routes = [
  { path: '/', name: 'home', component: Home },
  { path: '/profil', name: 'profil', component: Profile },
  { path: '/java', name: 'java', component: Java },
  { path: '/python', name: 'python', component: Python },
  { path: '/login', name: 'login', component: Login },
  { path: '/portfolio', name: 'portfolio', component: () => import('@/views/Portfolio.vue') },
  {
    path: '/admin',
    name: 'admin',
    component: AdminDashboard,
    meta: { requiresAuth: true }
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})

export default router
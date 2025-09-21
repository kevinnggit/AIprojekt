import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import Profile from '@/views/Profile.vue'
import Java from '@/views/TermineJava.vue'
import Python from '@/views/KiPython.vue'

const routes = [
  { path: '/', name: 'home', component: Home },
  { path: '/profil', name: 'profil', component: Profile },
  { path: '/java', name: 'java', component: Java },
  { path: '/python', name: 'python', component: Python },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
/**
 * router/index.js – Routenkonfiguration der Anwendung
 *
 * Definiert alle verfügbaren Routen und einen globalen Navigations-Guard,
 * der geschützte Bereiche (z. B. /admin) auf Authentifizierung prüft.
 */
import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import Profile from '@/views/Profile.vue'
import Java from '@/views/TermineJava.vue'
import Python from '@/views/KiPython.vue'
import Login from '@/views/Login.vue' // Add import
import AdminDashboard from '@/views/AdminDashboard.vue' // Add import
// Use dynamic import for store to avoid circular issues if any, or standard import
import { useAuthStore } from '@/stores/auth'

// Alle Anwendungsrouten; Portfolio wird lazy-loaded, um den initialen Bundle klein zu halten
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
    meta: { requiresAuth: true } // Diese Route erfordert eine aktive Authentifizierung
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

/**
 * Globaler Navigations-Guard
 * Leitet nicht authentifizierte Nutzer auf die Login-Seite um,
 * sofern die Zielroute das Metafeld requiresAuth trägt.
 * @param {RouteLocationNormalized} to - Zielroute
 * @param {RouteLocationNormalized} from - Ausgangsroute
 * @param {NavigationGuardNext} next - Callback zur Steuerung der Navigation
 */
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    // Zugriff verweigert – Weiterleitung zur Anmeldeseite
    next('/login')
  } else {
    next()
  }
})

export default router

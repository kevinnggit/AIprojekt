/**
 * main.js – Einstiegspunkt der Vue-Anwendung
 *
 * Initialisiert die Vue-Instanz, bindet Pinia als State-Management
 * und den Router ein, bevor die App in das DOM montiert wird.
 */
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router/index.js'

// Vue-Instanz auf Basis der Wurzelkomponente erstellen
const app = createApp(App)

// Pinia als globalen Store registrieren
app.use(createPinia())

// Router einbinden
app.use(router)

// App an das #app-Element im DOM montieren
app.mount('#app')

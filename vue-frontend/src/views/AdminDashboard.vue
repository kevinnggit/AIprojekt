<template>
  <div class="admin-dashboard">
    <div class="header">
      <h1>Admin Dashboard</h1>
      <div class="user-info">
        <span>Logged in as Admin</span>
        <button @click="authStore.logout()" class="logout-btn">Logout</button>
      </div>
    </div>

    <!-- TABS -->
    <div class="tabs">
        <button :class="{ active: currentTab === 'termine' }" @click="currentTab = 'termine'">📅 Termine</button>
        <button :class="{ active: currentTab === 'config' }" @click="currentTab = 'config'">⚙️ Einstellungen</button>
        <button :class="{ active: currentTab === 'users' }" @click="currentTab = 'users'">👥 Benutzer</button>
        <button :class="{ active: currentTab === 'portfolio' }" @click="currentTab = 'portfolio'">🎨 Portfolio</button>
    </div>

    <div v-if="error" class="error-msg">{{ error }}</div>

    <!-- TAB: TERMINE -->
    <div v-if="currentTab === 'termine'" class="content">
      <h2>Termin Übersicht (Live)</h2>
      <button @click="loadAppointments" class="refresh-btn">🔄 Refresh</button>
      
      <div v-if="loading" class="loading">Lade Termine...</div>
      
      <table v-else class="appointment-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Thema</th>
            <th>Zeitpunkt</th>
            <th>Status</th>
            <th>Aktionen</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="app in appointments" :key="app.id">
            <td>{{ app.id }}</td>
            <td>
                {{ app.name }}<br>
                <small>{{ app.email }}</small>
            </td>
            <td>{{ app.topic }}</td>
            <td>
                {{ formatDate(app.startTime) }} - <br>
                {{ formatTime(app.endTime) }}
            </td>
            <td>
                <span :class="['badge', app.status]">{{ app.status }}</span>
            </td>
            <td class="actions">
                <button 
                  v-if="app.status === 'PENDING'" 
                  @click="confirm(app.id)" 
                  class="btn-confirm"
                  :disabled="processing.has(app.id)"
                  :style="{ opacity: processing.has(app.id) ? 0.5 : 1 }"
                >
                  {{ processing.has(app.id) ? '...' : '✅' }}
                </button>
                <button 
                  @click="deleteTermin(app.id)" 
                  class="btn-delete"
                  :disabled="processing.has(app.id)"
                  :style="{ opacity: processing.has(app.id) ? 0.5 : 1 }"
                >
                  {{ processing.has(app.id) ? '...' : '🗑️' }}
                </button>
            </td>
          </tr>
          <tr v-if="appointments.length === 0">
            <td colspan="6" style="text-align: center;">Keine Termine vorhanden.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- TAB: CONFIG -->
    <div v-if="currentTab === 'config'" class="content">
        <h2>System Einstellungen</h2>
        <div class="card config-card">
            <div class="form-group">
                <label>Buchungsfenster (Monate im Voraus):</label>
                <input v-model="configForm.booking_window_months" type="number" min="1" max="24" />
                <small>Wie weit in die Zukunft können Termine gebucht werden?</small>
            </div>
            <button @click="saveConfig('booking_window_months', configForm.booking_window_months, 'Max months in advance')" class="btn-save">
                Speichern
            </button>
        </div>
    </div>

    <!-- TAB: USERS -->
    <div v-if="currentTab === 'users'" class="content">
        <h2>Benutzer Verwaltung</h2>
        
        <!-- Create User Form -->
        <div class="card user-form-card">
            <h3>Neuen Admin anlegen</h3>
            <div class="form-row">
                <input v-model="newUser.username" placeholder="Username" />
                <input v-model="newUser.password" type="password" placeholder="Passwort" />
                <button @click="createUser" class="btn-create">Admin erstellen</button>
            </div>
        </div>

        <h3>Vorhandene Benutzer</h3>
        <ul class="user-list">
            <li v-for="user in users" :key="user.id">
                <span class="username">{{ user.username }}</span>
                <span class="role">{{ user.role }}</span>
            </li>
        </ul>
    </div>

    <!-- TAB: PORTFOLIO -->
    <div v-if="currentTab === 'portfolio'" class="content">
        <h2>Portfolio Management</h2>
        
        <!-- Create Item -->
        <div class="card">
            <h3>Neues Projekt hinzufügen</h3>
            <div class="form-group">
                <label>Titel</label>
                <input v-model="newPortfolioItem.title" placeholder="Project Title" style="width: 100%; max-width: 400px;" />
            </div>
            <div class="form-group">
                <label>Beschreibung</label>
                <textarea v-model="newPortfolioItem.description" placeholder="Small description..." style="width: 100%; max-width: 400px; padding: 10px; background: #121212; border: 1px solid #444; color: white; border-radius: 4px;"></textarea>
            </div>
            <div class="form-group">
                <label>Bild URL</label>
                <input v-model="newPortfolioItem.imageUrl" placeholder="https://..." style="width: 100%; max-width: 400px;" />
            </div>
            <div class="form-group">
                <label>Projekt URL</label>
                <input v-model="newPortfolioItem.projectUrl" placeholder="https://..." style="width: 100%; max-width: 400px;" />
            </div>
            <div class="form-group">
                <label>Tags</label>
                <input v-model="newPortfolioItem.tags" placeholder="Java, Vue, AI" style="width: 100%; max-width: 400px;" />
            </div>
            <button @click="createPortfolioItem" class="btn-create">Projekt veröffentlichen</button>
        </div>

        <h3>Aktive Projekte</h3>
        <ul class="user-list">
            <li v-for="item in portfolioItems" :key="item.id">
                <span>{{ item.title }} ({{ item.tags }})</span>
                <button @click="deletePortfolioItem(item.id)" class="btn-delete">Löschen</button>
            </li>
        </ul>
    </div>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { api } from '@/services/api'

const authStore = useAuthStore()
const currentTab = ref('termine')
const error = ref('')
const loading = ref(false)
const processing = reactive(new Set()) 

// DATA
const appointments = ref([])
const users = ref([])
const configForm = reactive({
    booking_window_months: 3
})
const newUser = reactive({
    username: '',
    password: ''
})
const portfolioItems = ref([])
const newPortfolioItem = reactive({
    title: '',
    description: '',
    imageUrl: '',
    projectUrl: '',
    tags: ''
})

let pollInterval = null

// --- LOAD DATA ---

const loadAppointments = async () => {
    // Silent loading for poll
    if (!pollInterval && currentTab.value === 'termine') loading.value = true
    try {
        appointments.value = await api.termine.getAll()
    } catch (e) {
        if (!pollInterval) error.value = "Fehler: " + e.message
    } finally {
        loading.value = false
    }
}

const loadConfig = async () => {
    try {
        const configs = await api.admin.getConfig()
        // Find specific config
        const windowConfig = configs.find(c => c.configKey === 'booking_window_months')
        if (windowConfig) {
            configForm.booking_window_months = parseInt(windowConfig.configValue)
        }
    } catch (e) {
        console.error(e)
    }
}

const loadUsers = async () => {
    try {
        users.value = await api.admin.getUsers()
    } catch (e) {
        error.value = "User Load Error: " + e.message
    }
}

const loadPortfolio = async () => {
    try {
        portfolioItems.value = await api.portfolio.getAll()
    } catch (e) {
        error.value = "Portfolio Load Error: " + e.message
    }
}

// --- ACTIONS ---

const confirm = async (id) => {
    if (processing.has(id)) return;
    processing.add(id);
    try {
        await api.termine.confirm(id)
        await loadAppointments() 
    } catch (e) {
        alert("Fehler: " + e.message)
    } finally {
        processing.delete(id);
    }
}

const deleteTermin = async (id) => {
    if (processing.has(id)) return;
    if (!confirmDialog("Wirklich löschen?")) return;
    processing.add(id);
    try {
        await api.termine.delete(id)
        await loadAppointments()
    } catch (e) {
        alert("Fehler: " + e.message)
    } finally {
        processing.delete(id);
    }
}

const saveConfig = async (key, value, description) => {
    try {
        await api.admin.updateConfig(key, String(value), description)
        alert("Gespeichert!")
    } catch (e) {
        alert("Fehler: " + e.message)
    }
}

const createUser = async () => {
    if(!newUser.username || !newUser.password) return alert("Bitte alles ausfüllen");
    try {
        await api.admin.createUser(newUser.username, newUser.password, 'ADMIN') // Always create Admin here
        alert("Admin erstellt!")
        newUser.username = ''
        newUser.password = ''
        loadUsers()
    } catch (e) {
        alert("Fehler: " + e.message)
    }
}

const createPortfolioItem = async () => {
    try {
        await api.portfolio.create(newPortfolioItem)
        alert("Projekt erstellt!")
        // Reset
        Object.assign(newPortfolioItem, { title: '', description: '', imageUrl: '', projectUrl: '', tags: '' })
        loadPortfolio()
    } catch (e) {
        alert("Fehler: " + e.message)
    }
}

const deletePortfolioItem = async (id) => {
    if(!confirmDialog("Projekt löschen?")) return;
    try {
        await api.portfolio.delete(id)
        loadPortfolio()
    } catch (e) {
        alert("Fehler: " + e.message)
    }
}

const confirmDialog = (msg) => window.confirm(msg)
const formatDate = (dateStr) => new Date(dateStr).toLocaleString('de-DE', { dateStyle: 'medium', timeStyle: 'short' })
const formatTime = (dateStr) => new Date(dateStr).toLocaleTimeString('de-DE', { hour: '2-digit', minute: '2-digit' })

// --- LIFECYCLE ---

// 🔄 PERFORMANCE OPTIMIERUNG
// "Smart Polling": Wir pollen nur, wenn der 'termine' Tab aktiv ist.
// Sobald der User den Tab wechselt, stoppen wir das Interval.
watch(currentTab, (newTab) => {
    error.value = ''
    if (newTab === 'termine') {
        loadAppointments()
        pollInterval = setInterval(loadAppointments, 5000)
    } else {
        if (pollInterval) clearInterval(pollInterval)
        pollInterval = null
        
        if (newTab === 'config') loadConfig()
        if (newTab === 'users') loadUsers()
        if (newTab === 'portfolio') loadPortfolio()
    }
})

onMounted(() => {
    // Initial load
    loadAppointments()
    pollInterval = setInterval(loadAppointments, 5000)
})

onUnmounted(() => {
    if (pollInterval) clearInterval(pollInterval)
})
</script>

<style scoped>
.admin-dashboard { 
    padding: 20px; 
    color: #e0e0e0; 
    font-family: 'Inter', sans-serif;
    background-color: #121212; 
    min-height: calc(100vh - 80px); 
}
.header { display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #444; padding-bottom: 20px; margin-bottom: 20px; }
.logout-btn { background: #ff4444; color: white; border: none; padding: 8px 16px; cursor: pointer; border-radius: 4px; font-weight: bold; }

/* TABS */
.tabs { margin-bottom: 20px; display: flex; gap: 10px; border-bottom: 1px solid #333; padding-bottom: 10px; }
.tabs button {
    background: transparent; border: 1px solid transparent; color: #888; padding: 10px 20px; cursor: pointer; font-size: 1rem; border-radius: 4px 4px 0 0;
}
.tabs button:hover { color: #fff; background: #1e1e1e; }
.tabs button.active { background: #252525; color: #42b983; border-bottom: 2px solid #42b983; font-weight: bold; }

.content { animation: fadeIn 0.3s ease; }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }

/* TABLES & CARDS */
.appointment-table { width: 100%; border-collapse: collapse; background: #1e1e1e; border-radius: 8px; overflow: hidden; }
.appointment-table th, .appointment-table td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #333; }
.appointment-table th { background: #252525; font-weight: 600; color: #fff; text-transform: uppercase; }

.badge { padding: 4px 8px; border-radius: 12px; font-size: 0.75em; font-weight: bold; }
.badge.PENDING { background: #ffbb33; color: #000; }
.badge.CONFIRMED { background: #00C851; color: #fff; }

.card { background: #1e1e1e; padding: 20px; border-radius: 8px; margin-bottom: 20px; border: 1px solid #333; }
.form-group label { display: block; margin-bottom: 10px; color: #ccc; }
.form-group input, .form-group textarea { padding: 10px; background: #121212; border: 1px solid #444; color: white; width: 100%; max-width: 400px; border-radius: 4px; }
.btn-save, .btn-create { background: #42b983; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; margin-top: 10px; }
.btn-save:hover { background: #3aa876; }

.form-row { display: flex; gap: 10px; flex-wrap: wrap; }
.user-list { list-style: none; padding: 0; }
.user-list li { padding: 10px; border-bottom: 1px solid #333; display: flex; justify-content: space-between; align-items: center; }
.user-list li:last-child { border-bottom: none; }
.role { background: #333; padding: 2px 8px; border-radius: 4px; font-size: 0.8em; margin-left: 10px;}

.actions button { margin-right: 5px; border: none; cursor: pointer; padding: 6px 10px; border-radius: 4px; }
.btn-confirm { background: #00C851; color: white; }
.btn-delete { background: #ff4444; color: white; }
.actions button:disabled { opacity: 0.5; cursor: not-allowed; }
</style>

<template>
  <section>
    <h2>Java Termine Management</h2>
    <p>Verwaltung von Terminen über das Java Spring Boot Backend (Port 8081).</p>

    <!-- Fehlermeldung -->
    <div v-if="error" class="error-msg">
      {{ error }}
      <button @click="error = null">x</button>
    </div>

    <!-- Neuer Termin Formular -->
    <div class="card form-card">
      <h3>Neuen Termin buchen</h3>
      <form @submit.prevent="createTermin" class="termin-form">
        <div class="form-group">
          <label>Name:</label>
          <input v-model="form.name" required placeholder="Ihr Name" />
        </div>
        <div class="form-group">
          <label>Email:</label>
          <input v-model="form.email" type="email" required placeholder="email@example.com" />
        </div>
        <div class="form-group">
          <label>Thema:</label>
          <input v-model="form.topic" required placeholder="Worum geht es?" />
        </div>
        <div class="form-group span-2">
          <label>Zeitpunkt auswählen:</label>
          <CalendarView 
            :bookedAppointments="termine"
            :maxMonths="maxMonths"
            @slot-selected="onSlotSelected"
          />
          <div v-if="form.startTime" class="selected-time">
            Gewählt: {{ new Date(form.startTime).toLocaleString('de-DE') }}
          </div>
        </div>
        
        <button type="submit" :disabled="loading" class="primary-btn">
          {{ loading ? 'Speichere...' : 'Termin buchen' }}
        </button>
      </form>
    </div>

    <!-- Termin Liste -->
    <div class="list-container">
      <h3>Aktuelle Termine</h3>
      <div v-if="loading && termine.length === 0" class="loading">Lade Termine...</div>
      
      <div v-else-if="termine.length === 0" class="empty-state">
        Keine Termine vorhanden.
      </div>

      <div v-else class="termine-grid">
        <div v-for="t in termine" :key="t.id" class="termin-card">
          <div class="termin-header">
            <span class="topic">{{ t.topic }}</span>
            <span class="status" :class="t.status.toLowerCase()">{{ t.status }}</span>
          </div>
          <div class="termin-body">
            <p><strong>Von:</strong> {{ t.name }}</p>
            <p><strong>Start:</strong> {{ formatDate(t.startTime) }}</p>
            <p><strong>Ende:</strong> {{ formatDate(t.endTime) }}</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { api } from '../services/api';
import CalendarView from '../components/CalendarView.vue';

const termine = ref([]);
const loading = ref(false);
const error = ref(null);

const form = ref({
  name: '',
  email: '',
  topic: '',
  startTime: ''
});

const formatDate = (ts) => {
  if (!ts) return '-';
  return new Date(ts).toLocaleString('de-DE');
};

const loadTermine = async () => {
  loading.value = true;
  try {
    const config = await api.termine.getConfig();
    if(config.booking_window_months) maxMonths.value = config.booking_window_months;

    termine.value = await api.termine.getAll();
  } catch (e) {
    error.value = "Fehler beim Laden: " + e.message;
  } finally {
    loading.value = false;
  }
};

const maxMonths = ref(3); // Default

const onSlotSelected = (isoString) => {
  form.value.startTime = isoString;
};

const createTermin = async () => {
  if (!form.value.startTime) {
    error.value = "Bitte einen Termin im Kalender auswählen.";
    return;
  }
  loading.value = true;
  error.value = null;
  try {
    const payload = {
      ...form.value,
      startTime: form.value.startTime.length === 16 ? form.value.startTime + ':00' : form.value.startTime
    };
    
    await api.termine.create(payload);
    
    // Reset Form & Reload
    form.value = { name: '', email: '', topic: '', startTime: '' };
    await loadTermine();
  } catch (e) {
    error.value = "Fehler beim Erstellen: " + e.message;
  } finally {
    loading.value = false;
  }
};

onMounted(loadTermine);
</script>

<style scoped>
section {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

h2 { color: #2c3e50; margin-bottom: 10px; }

.error-msg {
  background: #ffdde1;
  color: #c0392b;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
}

.card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
  margin-bottom: 30px;
}

.termin-form {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-size: 0.9em;
  margin-bottom: 5px;
  color: #666;
}

.form-group input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.primary-btn {
  grid-column: span 2;
  background: #42b983;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  margin-top: 10px;
}

.primary-btn:disabled { background: #a0dbc2; }

.termine-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.termin-card {
  background: white;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 15px;
  transition: transform 0.2s;
}

.termin-card:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.1); }

.termin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
  margin-bottom: 10px;
}

.topic { font-weight: bold; color: #2c3e50; }

.status {
  font-size: 0.8em;
  padding: 2px 8px;
  border-radius: 10px;
  background: #eee;
}

.status.pending { background: #fff3cd; color: #856404; }
.status.confirmed { background: #d4edda; color: #155724; }

.termin-body p { margin: 5px 0; font-size: 0.9em; color: #555; }
</style>
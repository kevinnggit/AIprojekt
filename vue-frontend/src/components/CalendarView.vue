<template>
  <div class="calendar-container">
    <div class="calendar-controls">
      <button @click="prevWeek" :disabled="isPastWeek">← Vorherige Woche</button>
      <span>{{ dateRangeString }}</span>
      <button @click="nextWeek" :disabled="isFutureLimit">Nächste Woche →</button>
    </div>

    <div class="calendar-grid">
      <!-- Header Row (Days) -->
      <div class="grid-header-cell empty"></div>
      <div v-for="day in weekDays" :key="day.dateStr" class="grid-header-cell">
        <div class="day-name">{{ day.name }}</div>
        <div class="day-date">{{ day.displayDate }}</div>
      </div>

      <!-- Time Rows -->
      <div v-for="hour in hours" :key="hour" class="grid-row">
        <div class="time-cell">{{ hour }}:00</div>
        <div v-for="day in weekDays" :key="day.dateStr + hour" class="slot-cell">
          <button
            class="slot-btn"
            :class="getSlotClass(day, hour)"
            @click="selectSlot(day, hour)"
            :disabled="!isSlotAvailable(day, hour)"
          >
            {{ getSlotLabel(day, hour) }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';

const props = defineProps({
  bookedAppointments: {
    type: Array,
    default: () => []
  },
  maxMonths: {
    type: Number,
    default: 3
  }
});

const emit = defineEmits(['slot-selected']);

const currentStartDate = ref(new Date()); // Start of currently visible week
const hours = [10, 11, 12, 13, 14]; // 10:00 - 15:00

// Helper to get Monday of current week
const getMonday = (d) => {
  d = new Date(d);
  const day = d.getDay();
  const diff = d.getDate() - day + (day === 0 ? -6 : 1); // adjust when day is sunday
  d.setDate(diff);
  d.setHours(0, 0, 0, 0);
  return d;
};

// Initialize to this week's monday
currentStartDate.value = getMonday(new Date());

const isPastWeek = computed(() => {
  const today = getMonday(new Date());
  return currentStartDate.value <= today;
});

const isFutureLimit = computed(() => {
    const limit = new Date();
    // Use prop here
    limit.setMonth(limit.getMonth() + props.maxMonths);
    return currentStartDate.value > limit;
});

// 📅 Berechnet dynamsich die Wochentage (Mo-Fr) basierend auf dem aktuellen Startdatum
const weekDays = computed(() => {
  const days = [];
  const start = new Date(currentStartDate.value);
  const dayNames = ['Mo', 'Di', 'Mi', 'Do', 'Fr'];
  
  for (let i = 0; i < 5; i++) {
    const d = new Date(start);
    d.setDate(start.getDate() + i);
    days.push({
      name: dayNames[i],
      displayDate: d.toLocaleDateString('de-DE', { day: '2-digit', month: '2-digit' }),
      dateStr: d.toISOString().split('T')[0], // YYYY-MM-DD
      obj: d
    });
  }
  return days;
});

const dateRangeString = computed(() => {
    if(!weekDays.value.length) return '';
    const first = weekDays.value[0].displayDate;
    const last = weekDays.value[4].displayDate;
    return `${first} - ${last}`;
});

const prevWeek = () => {
  const d = new Date(currentStartDate.value);
  d.setDate(d.getDate() - 7);
  // Don't go to past if prohibited, but for navigation we allow going back to "today's week" even if today is wednesday.
  // Actually, past week view is fine, just slots disabled.
    currentStartDate.value = d;
};

const nextWeek = () => {
  const d = new Date(currentStartDate.value);
  d.setDate(d.getDate() + 7);
  currentStartDate.value = d;
};

// Check if a specific slot is booked
const isBooked = (day, hour) => {
    const year = day.dateStr.substring(0,4);
    const month = day.dateStr.substring(5,7);
    const date = day.dateStr.substring(8,10);
    // Construct ISO string start part: "YYYY-MM-DDTHH:00:00"
    // Note: Backend might send "2026-06-15T12:00:00"
    // We need to match precise string or Date object.
    
    // Simple string match
    const checkStr = `${day.dateStr}T${hour.toString().padStart(2, '0')}:00:00`;
    
    return props.bookedAppointments.some(app => 
        app.startTime === checkStr || app.startTime.startsWith(`${day.dateStr}T${hour.toString().padStart(2, '0')}:`)
    );
};

const isPast = (day, hour) => {
    const slotTime = new Date(day.obj);
    slotTime.setHours(hour, 0, 0, 0);
    return slotTime < new Date();
};

// 🧠 Hauptlogik für das Frontend:
// Ein Slot ist verfügbar, wenn er NICHT in der Vergangenheit liegt UND NICHT gebucht ist.
// Wir disablen den Button basierend hierauf.
const isSlotAvailable = (day, hour) => {
    if (isPast(day, hour)) return false;
    if (isBooked(day, hour)) return false;
    return true;
};

const getSlotClass = (day, hour) => {
    if (isBooked(day, hour)) return 'booked';
    if (isPast(day, hour)) return 'past';
    return 'available';
};

const getSlotLabel = (day, hour) => {
    if (isBooked(day, hour)) return 'Belegt';
    if (isPast(day, hour)) return '-';
    return 'Frei';
};

const selectSlot = (day, hour) => {
    // Return format: "YYYY-MM-DDTHH:mm" for input type datetime-local compatibility
    // Or just ISO
    const str = `${day.dateStr}T${hour.toString().padStart(2, '0')}:00`;
    emit('slot-selected', str);
};
</script>

<style scoped>
.calendar-container {
    background: #f9f9f9;
    padding: 15px;
    border-radius: 8px;
    border: 1px solid #ddd;
}

.calendar-controls {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
}

.calendar-controls button {
    padding: 5px 10px;
    cursor: pointer;
}

.calendar-grid {
    display: grid;
    grid-template-columns: 60px repeat(5, 1fr);
    gap: 5px;
}

.grid-header-cell {
    text-align: center;
    font-weight: bold;
    padding: 5px;
    background: #eee;
    border-radius: 4px;
}

.grid-row {
    display: contents; /* Allows children to participate in parent grid */
}

.time-cell {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 0.9em;
    color: #555;
    font-weight: bold;
}

.slot-cell {
    height: 40px;
}

.slot-btn {
    width: 100%;
    height: 100%;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 0.8em;
    font-weight: 600;
    transition: all 0.2s;
}

.slot-btn.available {
    background: #d4edda;
    color: #155724;
    border: 1px solid #c3e6cb;
}
.slot-btn.available:hover {
    background: #c3e6cb;
    transform: scale(1.02);
}

.slot-btn.booked {
    background: #f8d7da;
    color: #721c24;
    cursor: not-allowed;
    border: 1px solid #f5c6cb;
}

.slot-btn.past {
    background: #e2e3e5;
    color: #6c757d;
    cursor: not-allowed;
    border: 1px solid #d6d8db;
}
</style>

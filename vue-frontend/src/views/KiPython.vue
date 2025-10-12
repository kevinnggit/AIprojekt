<template>
  <section>
    <h2>KI Python Projekte</h2>
    <p>Hier werden KI und Python-bezogene Projekte angezeigt.</p>
    
    <!-- KI Inferenz Interface -->
    <div class="ki-interface">
      <h3>KI Inferenz Test</h3>
      <div class="input-group">
        <textarea 
          v-model="inputText" 
          placeholder="Gib einen Text ein für die KI-Analyse..."
          rows="3"
        ></textarea>
        <button 
          @click="runInference" 
          :disabled="loading || !inputText.trim()"
          class="infer-btn"
        >
          {{ loading ? 'Analysiere...' : 'KI-Analyse starten' }}
        </button>
      </div>
      
      <!-- Ergebnis -->
      <div v-if="result" class="result">
        <h4>KI-Antwort:</h4>
        <div class="response">{{ result.result.content }}</div>
        <div class="meta">
          <small>Modell: {{ result.model }} | Latenz: {{ result.latency_ms }}ms</small>
        </div>
      </div>
      
      <!-- Fehler -->
      <div v-if="error" class="error">
        <h4>Fehler:</h4>
        <p>{{ error }}</p>
      </div>
    </div>
  </section>
</template>

<script>
import { ref } from 'vue'

export default {
  name: 'KiPythonView',
  setup() {
    const inputText = ref('')
    const loading = ref(false)
    const result = ref(null)
    const error = ref(null)
    
    const API_BASE = 'http://localhost:8000'
    
    const runInference = async () => {
      loading.value = true
      error.value = null
      result.value = null
      
      try {
        const response = await fetch(`${API_BASE}/api/ki/infer`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ text: inputText.value })
        })
        
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}: ${await response.text()}`)
        }
        
        result.value = await response.json()
      } catch (err) {
        error.value = err.message
      } finally {
        loading.value = false
      }
    }
    
    return {
      inputText,
      loading,
      result,
      error,
      runInference
    }
  }
};
</script>

<style scoped>
section {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

h2 {
  color: #333;
  margin-bottom: 20px;
}

.ki-interface {
  background: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
  margin-top: 20px;
}

.ki-interface h3 {
  color: #333;
  margin-bottom: 15px;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
}

textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-family: inherit;
  resize: vertical;
}

.infer-btn {
  padding: 10px 20px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
}

.infer-btn:hover:not(:disabled) {
  background: #0056b3;
}

.infer-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.result {
  background: #e8f5e8;
  padding: 15px;
  border-radius: 4px;
  margin-top: 15px;
}

.result h4 {
  margin: 0 0 10px 0;
  color: #2d5a2d;
}

.response {
  background: white;
  padding: 10px;
  border-radius: 4px;
  border-left: 3px solid #28a745;
  margin-bottom: 10px;
}

.meta {
  color: #666;
  font-size: 0.9em;
}

.error {
  background: #f8d7da;
  color: #721c24;
  padding: 15px;
  border-radius: 4px;
  margin-top: 15px;
}

.error h4 {
  margin: 0 0 10px 0;
}
</style>
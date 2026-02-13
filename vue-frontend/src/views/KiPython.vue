<template>
  <section>
    <h2>KI Python Projekte</h2>
    <p>Hier werden KI-Funktionen über das Python Backend (Port 8000) gesteuert.</p>
    
    <!-- Provider Selector -->
    <div class="provider-select">
        <label>🧠 KI-Gehirn wählen:</label>
        <select v-model="provider">
            <option value="openai">OpenAI (GPT-3.5)</option>
            <option value="deepseek">DeepSeek (V3)</option>
            <option value="ollama">Ollama (Llama 2 Local)</option>
        </select>
    </div>

    <!-- 1. KI Inferenz -->
    <div class="ki-interface">
      <h3>1. KI Text-Analyse (Inferenz)</h3>
      <div class="input-group">
        <textarea 
          v-model="inputText" 
          placeholder="Gib einen Text ein für die KI-Analyse..."
          rows="3"
        ></textarea>
        <button 
          @click="runInference" 
          :disabled="loadingInfer || !inputText.trim()"
          class="infer-btn"
        >
          {{ loadingInfer ? 'Analysiere...' : 'Text analysieren' }}
        </button>
      </div>
      
      <!-- Ergebnis Inferenz -->
      <div v-if="resultInfer" class="result">
        <h4>Antwort:</h4>
        <div class="response">{{ resultInfer.result.content }}</div>
        <div class="meta">
          <small>Modell: {{ resultInfer.model }} | Provider: {{ resultInfer.provider }} | Latenz: {{ resultInfer.latency_ms }}ms</small>
        </div>
      </div>
    </div>

    <!-- 2. Projekt-Ideen Generator -->
    <div class="ki-interface">
      <h3>2. Projekt-Ideen Generator</h3>
      <div class="ideas-form">
        <input v-model="ideaTopic" placeholder="Thema (z.B. Weltraum)" class="topic-input" />
        <select v-model="ideaCount" class="count-select">
          <option :value="3">3 Ideen</option>
          <option :value="5">5 Ideen</option>
        </select>
        <button 
          @click="generateIdeas" 
          :disabled="loadingIdeas || !ideaTopic.trim()"
          class="infer-btn secondary"
        >
          {{ loadingIdeas ? 'Generiere...' : 'Ideen finden' }}
        </button>
      </div>

      <!-- Ergebnis Ideen -->
      <div v-if="resultIdeas" class="result ideas-result">
        <h4>Vorschläge für "{{ resultIdeas.topic }}":</h4>
        <ul>
          <li v-for="(idea, idx) in resultIdeas.ideas" :key="idx">{{ idea }}</li>
        </ul>
        <div class="meta">
          <small>Modell: {{ resultIdeas.model_used }}</small>
        </div>
      </div>
    </div>

    <!-- Globaler Fehler -->
    <div v-if="error" class="error">
      <h4>Fehler:</h4>
      <p>{{ error }}</p>
      <button @click="error = null" class="close-err">Schließen</button>
    </div>

  </section>
</template>

<script setup>
import { ref } from 'vue';
import { api } from '../services/api';

const provider = ref('openai');

// State Inferenz
const inputText = ref('');
const loadingInfer = ref(false);
const resultInfer = ref(null);

// State Ideen
const ideaTopic = ref('');
const ideaCount = ref(3);
const loadingIdeas = ref(false);
const resultIdeas = ref(null);

// Global Error
const error = ref(null);

const runInference = async () => {
  loadingInfer.value = true;
  error.value = null;
  resultInfer.value = null;
  try {
    // 🔌 HANDSHAKE: Wir schicken den gewählten Provider ('openai' oder 'deepseek')
    // an das Backend. Die Factory dort entscheidet dann.
    resultInfer.value = await api.ai.infer(inputText.value, provider.value);
  } catch (err) {
    error.value = err.message;
  } finally {
    loadingInfer.value = false;
  }
};

const generateIdeas = async () => {
  loadingIdeas.value = true;
  error.value = null;
  resultIdeas.value = null;
  try {
    resultIdeas.value = await api.ai.generateIdeas(ideaTopic.value, ideaCount.value, provider.value);
  } catch (err) {
    error.value = err.message;
  } finally {
    loadingIdeas.value = false;
  }
};
</script>

<style scoped>
section {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

h2 { color: #333; margin-bottom: 20px; }

.provider-select {
    background: #e9ecef;
    padding: 15px;
    border-radius: 8px;
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    gap: 15px;
    border: 1px solid #dee2e6;
}
.provider-select label { font-weight: bold; color: #495057; }
.provider-select select {
    padding: 8px;
    border-radius: 4px;
    border: 1px solid #ced4da;
    font-size: 1rem;
    min-width: 200px;
}

.ki-interface {
  background: #fdfdfd;
  padding: 25px;
  border-radius: 8px;
  margin-top: 30px;
  border: 1px solid #eee;
  box-shadow: 0 2px 8px rgba(0,0,0,0.03);
}

.ki-interface h3 {
  color: #2c3e50;
  margin-bottom: 15px;
  border-bottom: 2px solid #007bff;
  display: inline-block;
  padding-bottom: 5px;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-family: inherit;
  resize: vertical;
  min-height: 80px;
}

/* Ideen Formular Styles */
.ideas-form {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.topic-input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.count-select {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
}

.infer-btn {
  padding: 10px 25px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: background 0.2s;
}

.infer-btn.secondary { background: #6c757d; }
.infer-btn:hover:not(:disabled) { filter: brightness(0.9); }
.infer-btn:disabled { background: #ccc; cursor: not-allowed; }

/* Result Styles */
.result {
  background: #f0f7ff;
  padding: 20px;
  border-radius: 6px;
  margin-top: 20px;
  border: 1px solid #cce5ff;
}

.ideas-result { background: #fff8e1; border-color: #ffeeba; }

.result h4 { margin: 0 0 10px 0; color: #004085; }
.ideas-result h4 { color: #856404; }

.response {
  background: white;
  padding: 15px;
  border-radius: 4px;
  border-left: 4px solid #007bff;
  margin-bottom: 10px;
  line-height: 1.5;
}

ul { padding-left: 20px; margin: 10px 0; }
li { margin-bottom: 5px; color: #333; }

.meta { color: #888; font-size: 0.85em; text-align: right; }

.error {
  background: #f8d7da;
  color: #721c24;
  padding: 15px;
  border-radius: 4px;
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.close-err {
  background: none;
  border: 1px solid #f5c6cb;
  color: #721c24;
  padding: 5px 10px;
  border-radius: 4px;
  cursor: pointer;
}
</style>
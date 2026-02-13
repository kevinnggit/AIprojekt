<template>
  <div class="portfolio-page min-h-screen pt-20 pb-10 px-4 text-white">
    <!-- Header -->
    <div class="text-center mb-16">
      <h1 class="text-5xl md:text-7xl font-extrabold mb-4 bg-clip-text text-transparent bg-gradient-to-r from-cyan-400 to-purple-500 animate-pulse-slow">
        The Portfolio
      </h1>
      <p class="text-xl text-gray-300 max-w-2xl mx-auto">
        A collection of advanced experiments and production-ready systems.
        Built with Java, Python, and Vue.js for maximum performance and intelligence.
      </p>
    </div>

    <!-- Error/Loading -->
    <div v-if="error" class="text-center text-red-400 mb-8">{{ error }}</div>
    <div v-if="loading" class="text-center text-cyan-300 mb-8 text-xl">Loading Artifacts...</div>

    <!-- Grid -->
    <div v-else class="container mx-auto grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
      
      <!-- Card -->
      <div 
        v-for="item in items" 
        :key="item.id" 
        class="group relative bg-white/5 backdrop-blur-lg border border-white/10 rounded-2xl overflow-hidden hover:border-cyan-500/50 transition-all duration-500 hover:shadow-[0_0_30px_rgba(34,211,238,0.2)] hover:-translate-y-2"
      >
      <!-- ✨ GLASSMORPHISM EXPLAINED:
           bg-white/5: Halbtransparenter Hintergrund (5% Opacity)
           backdrop-blur-lg: Der "Milchglas"-Effekt, der den Hintergrund unscharf macht.
           border-white/10: Subtiler Rand für Kontrast.
           hover:shadow-...: Neon-Glow Effekt beim Hover.
      -->
        <!-- Image Area (Gradient Fallback if no image) -->
        <div class="h-48 overflow-hidden bg-gray-900 relative">
             <img 
                v-if="item.imageUrl" 
                :src="item.imageUrl" 
                class="w-full h-full object-cover transition-transform duration-700 group-hover:scale-110"
                alt="Project Image"
             />
             <div v-else class="w-full h-full bg-gradient-to-br from-gray-800 to-black flex items-center justify-center">
                <span class="text-4xl">🚀</span>
             </div>
             
             <!-- Overlay on Hover -->
             <div class="absolute inset-0 bg-black/60 opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex items-center justify-center">
                 <a 
                    v-if="item.projectUrl" 
                    :href="item.projectUrl" 
                    target="_blank"
                    class="bg-cyan-500 text-black font-bold py-2 px-6 rounded-full transform scale-90 group-hover:scale-100 transition-transform hover:bg-cyan-400"
                 >
                    View Project
                 </a>
             </div>
        </div>

        <!-- Content -->
        <div class="p-6">
          <h2 class="text-2xl font-bold mb-2 group-hover:text-cyan-400 transition-colors">{{ item.title }}</h2>
          <p class="text-gray-400 text-sm mb-4 leading-relaxed line-clamp-3">
            {{ item.description }}
          </p>
          
          <!-- Tags -->
          <div class="flex flex-wrap gap-2 mt-4">
            <span 
                v-for="tag in (item.tags ? item.tags.split(',') : [])" 
                :key="tag"
                class="text-xs font-mono bg-white/10 px-2 py-1 rounded text-cyan-200 border border-white/5"
            >
                {{ tag.trim() }}
            </span>
          </div>
        </div>
      </div>
      
    </div>

    <!-- Empty State -->
    <div v-if="!loading && items.length === 0" class="text-center text-gray-500 mt-20">
        <p class="text-2xl">No artifacts found yet.</p>
        <p class="text-sm mt-2">Check back later or access the Admin console.</p>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { api } from '@/services/api';

const items = ref([]);
const loading = ref(true);
const error = ref(null);

onMounted(async () => {
  try {
    items.value = await api.portfolio.getAll();
  } catch (err) {
    console.error("Failed to load portfolio", err);
    error.value = "Failed to load portfolio items.";
    // Mock Data Fallback for Demo if backend fails or empty (Optional, but nice for first run)
    if (items.value.length === 0) {
        // items.value = [
        //     { id: 1, title: 'NSPACE 2.0', description: 'The Future of AI Architecture.', tags: 'Java, Python, Vue' },
        //     { id: 2, title: 'DeepSeek Integration', description: 'Cost-effective LLM integration.', tags: 'AI, Python' }
        // ]
    }
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
    /* Subtle noise texture or gradient background if needed, 
       but assumes App.vue has a dark background already. */
    position: relative; /* Placeholder to fix lint warning */
.animate-pulse-slow {
    animation: pulse 4s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}
@keyframes pulse {
    0%, 100% { opacity: 1; }
    50% { opacity: .7; }
}
</style>

<template>
  <section>
    <h2>Java Termine</h2>
    <p>Hier werden Java-bezogene Termine und Projekte angezeigt.</p>
    <div class="placeholder">
      <h3>Coming Soon</h3>
      <p>Java Backend Integration wird implementiert...</p>
    </div>
  </section>
</template>

<script>
export default {
  name: 'TermineJavaView'
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

.placeholder {
  background: #f5f5f5;
  padding: 40px;
  border-radius: 8px;
  text-align: center;
  margin-top: 20px;
}

.placeholder h3 {
  color: #666;
  margin-bottom: 10px;
}

.placeholder p {
  color: #888;
}
</style>
<template>
  <section>
    <h2>Profil</h2>
    <ul>
      <li>Name: Kevin Nguefack</li>
      <li>Rolle: Student, Entwickler, IT-Projektmanager</li>
      <li>Kontakt: kevin@nspace.com</li>
    </ul>
  </section>
</template>

<script>
export default {
  name: 'ProfileView'
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

ul {
  list-style: none;
  padding: 0;
}

li {
  padding: 10px 0;
  border-bottom: 1px solid #eee;
  font-size: 16px;
}

li:last-child {
  border-bottom: none;
}
</style>
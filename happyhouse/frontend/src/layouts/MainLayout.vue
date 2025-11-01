<template>
  <div id="wrapper">
    <TheHeader />
    <main id="content-wrapper">
      <RouterView />
    </main>
  </div>
</template>

<script>
import TheHeader from "../components/TheHeader.vue";

export default {
  name: "MainLayout",
  components: { TheHeader },
};
</script>

<style scoped>
#content-wrapper {
  padding: 1rem;
}
</style>

<template>
  <div id="app">
    <TheHeader />
    <div id="wrapper">
      <TheNavbar />
      <main id="content-wrapper">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script>
import TheHeader from "./components/TheHeader.vue";
import TheNavbar from "./components/TheNavbar.vue";

export default {
  name: "App",
  components: { TheHeader, TheNavbar },
};
</script>

<style>
#app {
  word-break: keep-all;
  font-family: "Noto Sans KR", sans-serif;
}
</style>

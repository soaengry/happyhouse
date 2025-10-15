<template>
  <div id="app">
    <div id="wrapper">
      <div>
        <TheHeader />
      </div>
      <main id="content-wrapper">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script>
import TheHeader from "./components/TheHeader.vue";

export default {
  name: "App",
  components: { TheHeader },
};
</script>

<style>
#app {
  word-break: keep-all;
  font-family: "Noto Sans KR", sans-serif;
}
</style>

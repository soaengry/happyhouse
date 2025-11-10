<template>
  <div id="app">
    <RouterView />
  </div>
</template>

<style>
@import "./assets/css/app.css";

#app {
  position: relative;
  height: 100vh;
  overflow-x: hidden;
  word-break: keep-all;
  font-family: "Noto Sans KR", sans-serif;
  min-width: 320px;
  max-width: 1200px;
  margin: 0 auto;
}
</style>

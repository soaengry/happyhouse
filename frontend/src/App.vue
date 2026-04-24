<template>
  <div id="app">
    <RouterView />
  </div>
</template>

<style>
@import "./assets/css/app.css";
@import url("https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;600;700&display=swap");

* { box-sizing: border-box; }

body {
  font-family: "Noto Sans KR", -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
}

#app {
  position: relative;
  height: 100vh;
  overflow-x: hidden;
  word-break: keep-all;
  min-width: 320px;
  max-width: 1440px;
  margin: 0 auto;
}

/* ── Scrollbar ─────────────────────────────────────────── */
::-webkit-scrollbar { width: 5px; height: 5px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 999px; }
::-webkit-scrollbar-thumb:hover { background: #94A3B8; }
</style>

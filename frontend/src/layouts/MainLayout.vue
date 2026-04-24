<template>
  <div class="layout">
    <!-- 모바일 전용 햄버거 버튼 -->
    <button
      class="hamburger-btn"
      :class="{ open: isSidebarOpen }"
      @click="isSidebarOpen = !isSidebarOpen"
      aria-label="메뉴"
    >
      <font-awesome-icon :icon="isSidebarOpen ? 'fa-solid fa-xmark' : 'fa-solid fa-bars'" />
    </button>

    <!-- 모바일 사이드바 백드롭 -->
    <transition name="backdrop">
      <div
        v-if="isSidebarOpen"
        class="sidebar-backdrop"
        @click="isSidebarOpen = false"
      />
    </transition>

    <TheHeader :is-open="isSidebarOpen" @close="isSidebarOpen = false" />

    <main class="layout-main">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { ref } from "vue";
import TheHeader from "@/components/TheHeader.vue";

const isSidebarOpen = ref(false);
</script>

<style scoped>
.layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.layout-main {
  margin-left: 240px;
  flex: 1;
  overflow-y: auto;
  background: var(--bg);
  min-width: 0;
}

/* ── 백드롭 ──────────────────────────────────────────── */
.sidebar-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.5);
  z-index: 99;
}
.backdrop-enter-active,
.backdrop-leave-active { transition: opacity 0.25s ease; }
.backdrop-enter-from,
.backdrop-leave-to    { opacity: 0; }

/* ── 햄버거 버튼 (모바일 전용) ─────────────────────── */
.hamburger-btn {
  display: none;
  position: fixed;
  top: 0.875rem;
  left: 0.875rem;
  z-index: 101;
  width: 2.25rem;
  height: 2.25rem;
  background: var(--sidebar-bg);
  color: #F8FAFC;
  border: none;
  border-radius: var(--radius);
  font-size: 1rem;
  align-items: center;
  justify-content: center;
  transition: background 0.15s;
  box-shadow: var(--shadow);
}
.hamburger-btn:hover { background: #1E293B; }

/* ── 반응형 ──────────────────────────────────────────── */
@media (max-width: 992px) {
  .layout-main { margin-left: 64px; }
}

@media (max-width: 639px) {
  .layout-main {
    margin-left: 0;
    padding-top: 4rem; /* 햄버거 버튼 공간 확보 */
  }
  .hamburger-btn { display: flex; }
}
</style>

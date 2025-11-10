<template>
  <div class="layout-wrapper">
    <TheHeader />
    <main class="content-area">
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
.layout-wrapper {
  position: relative;
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* 사이드바 */
.layout-wrapper > :first-child {
  width: 260px;
  flex-shrink: 0;
}

/* 콘텐츠 영역 */
.content-area {
  flex-grow: 1;
  overflow-y: auto;
  background-color: #f8f9fc;
}

/* 태블릿 이하 */
@media (max-width: 992px) {
  .layout-wrapper > :first-child {
    width: 60px;
  }
}
</style>

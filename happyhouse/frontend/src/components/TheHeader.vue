<template>
  <header>
    <div class="header-container">
      <div class="nav-container">
        <router-link to="/" class="brand text-light">
          <font-awesome-icon class="brand-icon" icon="fa-solid fa-house" />
          <h1 class="brand-name">HAPPY HOUSE</h1>
        </router-link>
        <hr />
        <nav class="global-nav">
          <!-- 메뉴 영역 -->
          <ul class="menu" :class="{ open: menuOpen || !isMobile }">
            <li class="menu-item">
              <router-link to="/" class="text-light">
                <font-awesome-icon
                  class="menu-icon"
                  icon="fa-solid fa-magnifying-glass"
                />
                <span class="menu-name">매물 검색</span>
              </router-link>
            </li>
            <li class="menu-item">
              <router-link to="/" class="text-light">
                <font-awesome-icon
                  class="menu-icon"
                  icon="fa-solid fa-bookmark"
                />
                <span class="menu-name">북마크</span>
              </router-link>
            </li>
            <li class="menu-item">
              <router-link to="/" class="text-light">
                <font-awesome-icon
                  class="menu-icon"
                  icon="fa-solid fa-newspaper"
                />
                <span class="menu-name">주요 뉴스</span>
              </router-link>
            </li>
            <li class="menu-item">
              <router-link to="/" class="text-light">
                <font-awesome-icon class="menu-icon" icon="fa-solid fa-globe" />
                <span class="menu-name">커뮤니티</span>
              </router-link>
            </li>
          </ul>
        </nav>
      </div>
      <!-- end of .nav-container -->
      <router-link to="/user" class="profile-container text-light">
        <div class="profile-image"></div>
        <div class="profile-name">{{ nickname }}</div>
      </router-link>
    </div>
  </header>
</template>

<script setup>
import { useUserStore } from "@/stores/userStore";
import { computed, onMounted } from "vue";

const userStore = useUserStore();

const nickname = computed(() => userStore.user?.nickname || "로그인");

onMounted(() => {
  if (!userStore.user && localStorage.getItem("accessToken")) {
    userStore.fetchUserInfo?.();
  }
});
</script>

<style scoped>
.header-container {
  position: fixed;
  top: 0;
  left: 0;
  background-color: #4e73df;
  height: 100vh;
  width: 260px;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.brand {
  margin-bottom: 0.5rem;
  margin: 0.5rem 0 1rem 0;
  font-size: 1.5rem;
}

.menu-item {
  padding: 0.5rem 0;
  display: flex;
  align-items: center;
}

.menu-icon {
  font-size: 1.5rem;
}

.menu-name {
  padding-left: 0.5rem;
}

@media (max-width: 992px) {
  .header-container {
    width: 60px;
    align-items: center;
  }
  .brand-name {
    display: none;
  }
  .menu-name {
    display: none;
  }
}
</style>

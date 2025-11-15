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
          <ul class="menu">
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
      <div class="profile-container text-light" @click="handleProfileClick">
        <div class="profile-image">
          <img
            :src="profileImageUrl"
            alt="프로필 이미지"
            class="w-100 h-100 rounded-circle"
          />
        </div>
        <div class="profile-name">{{ nickname }}</div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useUserStore } from "@/stores/userStore";
import { computed, onMounted } from "vue";
import { useRouter } from "vue-router";

const userStore = useUserStore();
const router = useRouter();

const nickname = computed(() => userStore.user?.nickname || "로그인");
const profileImageUrl = computed(() =>
  getProfileImageUrl(userStore.user?.profileImageUrl),
);
const BASE_URL = process.env.VUE_APP_BASE_URL || "http://localhost:8080";

onMounted(() => {
  if (!userStore.user && localStorage.getItem("accessToken")) {
    userStore.fetchUserInfo?.();
  }
});

function getProfileImageUrl(fileName) {
  if (!fileName) return `${BASE_URL}/api/user/image?default.png`; // 기본 이미지
  return `${BASE_URL}/api/user/image?fileName=${encodeURIComponent(fileName)}`;
}

function handleProfileClick() {
  if (userStore.user) {
    router.push("/user");
  } else {
    router.push("/login");
  }
}
</script>

<style scoped>
.header-container {
  position: absolute;
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
  padding: 0.6rem 0;
  display: flex;
  align-items: center;
}

.menu-item a {
  display: flex;
  align-items: center;
}

.menu-icon {
  font-size: 1.5rem;
}

.menu-name {
  padding-left: 0.5rem;
}

.profile-container {
  display: flex;
  cursor: pointer;
}

.profile-image {
  height: 2rem;
  width: 2rem;
  margin-right: 1rem;
}

.profile-image img {
  object-fit: cover;
}

.profile-name {
  line-height: 2rem;
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

  .profile-image {
    margin: 0;
  }

  .profile-name {
    display: none;
  }
}
</style>

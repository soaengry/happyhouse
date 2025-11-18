<template>
  <header>
    <div class="header-container">
      <div class="nav-container">
        <a class="brand text-light" @click="onClickSearch">
          <font-awesome-icon class="brand-icon" icon="fa-solid fa-house" />
          <h1 class="brand-name">HAPPY HOUSE</h1>
        </a>
        <hr />
        <nav class="global-nav">
          <!-- 메뉴 영역 -->
          <ul class="menu">
            <li class="menu-item">
              <a class="text-light" @click="onClickSearch">
                <font-awesome-icon
                  class="menu-icon"
                  icon="fa-solid fa-magnifying-glass"
                />
                <span class="menu-name">매물 검색</span></a
              >
            </li>
            <li class="menu-item">
              <div class="bookmark-meni">
                <a @click="bookmarkOpen = !bookmarkOpen" class="text-light">
                  <font-awesome-icon
                    class="menu-icon"
                    icon="fa-solid fa-bookmark"
                  />
                  <span class="menu-name">북마크</span>
                </a>
                <transition name="slide-fade">
                  <ul v-if="bookmarkOpen" class="submenu">
                    <li class="submenu-item">
                      <router-link to="/bookmark/region" class="text-light"
                        >관심 지역</router-link
                      >
                    </li>
                    <li class="submenu-item">
                      <router-link to="/bookmark/house" class="text-light"
                        >관심 매물</router-link
                      >
                    </li>
                  </ul>
                </transition>
              </div>
            </li>
            <li class="menu-item">
              <router-link to="/news" class="text-light">
                <font-awesome-icon
                  class="menu-icon"
                  icon="fa-solid fa-newspaper"
                />
                <span class="menu-name">주요 뉴스</span>
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
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";
import { useHouseStore } from "@/stores/houseStore";

const router = useRouter();
const userStore = useUserStore();
const houseStore = useHouseStore();

const nickname = computed(() => userStore.user?.nickname || "로그인");
const profileImageUrl = computed(() =>
  getProfileImageUrl(userStore.user?.profileImageUrl),
);
const BASE_URL = process.env.VUE_APP_BASE_URL || "http://localhost:8080";

const bookmarkOpen = ref(false);

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

function onClickSearch() {
  houseStore.resetSearch(); // 지역코드 초기화
  router.push("/"); // HouseMain.vue로 이동
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
  font-size: 1.2rem;
  margin-right: 0.4rem;
}

.menu-name {
  padding-left: 0.5rem;
}

.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.3s ease;
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateY(-10px);
  opacity: 0;
}

.bookmark-menu {
  display: flex;
}

.submenu {
  margin-top: 0.5rem;
  padding-left: 2.4rem;
}

.submenu-item {
  margin-top: 0.8rem;
  font-size: small;
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

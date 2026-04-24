<template>
  <aside :class="['sidebar', { open: isOpen }]">
    <!-- Brand -->
    <div class="sidebar-brand" @click="onClickSearch">
      <div class="brand-icon-wrap">
        <font-awesome-icon icon="fa-solid fa-house" />
      </div>
      <span class="brand-name">HAPPY HOUSE</span>
    </div>

    <div class="sidebar-divider"></div>

    <!-- Navigation -->
    <nav class="sidebar-nav">
      <a class="nav-item" @click="onClickSearch">
        <font-awesome-icon class="nav-icon" icon="fa-solid fa-magnifying-glass" />
        <span class="nav-label">매물 검색</span>
      </a>

      <div class="nav-group">
        <a class="nav-item" @click="bookmarkOpen = !bookmarkOpen">
          <font-awesome-icon class="nav-icon" icon="fa-solid fa-bookmark" />
          <span class="nav-label">북마크</span>
          <font-awesome-icon
            class="nav-chevron"
            icon="fa-solid fa-chevron-down"
            :style="{ transform: bookmarkOpen ? 'rotate(180deg)' : 'rotate(0deg)' }"
          />
        </a>
        <transition name="submenu-slide">
          <ul v-if="bookmarkOpen" class="submenu">
            <li>
              <router-link to="/bookmark/region" class="submenu-item">관심 지역</router-link>
            </li>
            <li>
              <router-link to="/bookmark/house" class="submenu-item">관심 매물</router-link>
            </li>
          </ul>
        </transition>
      </div>

      <router-link to="/news" class="nav-item">
        <font-awesome-icon class="nav-icon" icon="fa-solid fa-newspaper" />
        <span class="nav-label">주요 뉴스</span>
      </router-link>

      <router-link to="/community" class="nav-item">
        <font-awesome-icon class="nav-icon" icon="fa-solid fa-users" />
        <span class="nav-label">커뮤니티</span>
      </router-link>
    </nav>

    <!-- Profile -->
    <div class="sidebar-profile" @click="handleProfileClick">
      <div class="avatar">
        <img :src="profileImageUrl" alt="프로필" />
      </div>
      <div class="profile-text">
        <p class="profile-name">{{ nickname }}</p>
        <p class="profile-hint">{{ userStore.user ? "내 계정 보기" : "로그인하기" }}</p>
      </div>
      <font-awesome-icon class="profile-arrow" icon="fa-solid fa-arrow-right" />
    </div>
  </aside>
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useUserStore } from "@/stores/userStore";
import { useHouseStore } from "@/stores/houseStore";
import { BASE_URL } from "@/utils/constants";

defineProps({
  isOpen: { type: Boolean, default: false },
});
const emit = defineEmits(["close"]);

const router    = useRouter();
const route     = useRoute();
const userStore = useUserStore();
const houseStore = useHouseStore();
const bookmarkOpen = ref(false);

const nickname = computed(() => userStore.user?.nickname || "로그인");
const profileImageUrl = computed(() =>
  getProfileImageUrl(userStore.user?.profileImageUrl),
);

// 라우트 변경 시 모바일 사이드바 자동 닫기
watch(() => route.fullPath, () => emit("close"));

onMounted(() => {
  if (!userStore.user && localStorage.getItem("accessToken")) {
    userStore.fetchUserInfo();
  }
});

function getProfileImageUrl(fileName) {
  if (!fileName) return `${BASE_URL}/api/user/image?fileName=default.png`;
  return `${BASE_URL}/api/user/image?fileName=${encodeURIComponent(fileName)}`;
}

function handleProfileClick() {
  router.push(userStore.user ? "/user" : "/login");
}

function onClickSearch() {
  houseStore.resetSearch();
  router.push("/");
}
</script>

<style scoped>
/* ── 사이드바 기본 (데스크탑) ─────────────────────────── */
.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  width: 240px;
  height: 100vh;
  background: var(--sidebar-bg);
  display: flex;
  flex-direction: column;
  padding: 1.25rem 0;
  z-index: 100;
  overflow: hidden;
}

/* Brand */
.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.25rem 1.25rem 1rem;
  cursor: pointer;
  user-select: none;
}
.brand-icon-wrap {
  width: 2rem;
  height: 2rem;
  background: var(--primary);
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 0.9rem;
  flex-shrink: 0;
}
.brand-name {
  font-size: 0.95rem;
  font-weight: 700;
  letter-spacing: 0.04em;
  color: #F8FAFC;
}

.sidebar-divider {
  height: 1px;
  background: rgba(255, 255, 255, 0.06);
  margin: 0 1.25rem 0.75rem;
}

/* Nav */
.sidebar-nav {
  flex: 1;
  padding: 0 0.75rem;
  display: flex;
  flex-direction: column;
  gap: 0.125rem;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.65rem 0.875rem;
  border-radius: var(--radius);
  color: var(--sidebar-text);
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.15s, color 0.15s;
  user-select: none;
}
.nav-item:hover {
  background: var(--sidebar-hover);
  color: var(--sidebar-text-active);
}
.nav-item.router-link-active {
  background: var(--sidebar-active-bg);
  color: var(--sidebar-text-active);
}

.nav-icon {
  width: 1rem;
  text-align: center;
  flex-shrink: 0;
  opacity: 0.8;
}
.nav-label  { flex: 1; }
.nav-chevron {
  font-size: 0.7rem;
  transition: transform 0.2s ease;
  opacity: 0.6;
}

/* Submenu */
.submenu {
  margin: 0.25rem 0 0.25rem 2.4rem;
  display: flex;
  flex-direction: column;
  gap: 0.125rem;
}
.submenu-item {
  display: block;
  padding: 0.5rem 0.75rem;
  border-radius: var(--radius-sm);
  font-size: 0.825rem;
  color: var(--sidebar-text);
  transition: background 0.12s, color 0.12s;
}
.submenu-item:hover,
.submenu-item.router-link-active {
  background: var(--sidebar-hover);
  color: var(--sidebar-text-active);
}

.submenu-slide-enter-active,
.submenu-slide-leave-active { transition: opacity 0.2s ease, transform 0.2s ease; }
.submenu-slide-enter-from,
.submenu-slide-leave-to     { opacity: 0; transform: translateY(-6px); }

/* Profile */
.sidebar-profile {
  margin: 0 0.75rem;
  padding: 0.75rem 0.875rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  border-radius: var(--radius);
  border: 1px solid rgba(255, 255, 255, 0.07);
  cursor: pointer;
  transition: background 0.15s;
}
.sidebar-profile:hover { background: var(--sidebar-hover); }

.avatar {
  width: 2.25rem;
  height: 2.25rem;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  background: rgba(255, 255, 255, 0.1);
}
.avatar img { width: 100%; height: 100%; object-fit: cover; }

.profile-text { flex: 1; min-width: 0; }
.profile-name {
  font-size: 0.8rem;
  font-weight: 600;
  color: #F8FAFC;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.profile-hint { font-size: 0.72rem; color: var(--sidebar-text); margin-top: 0.1rem; }
.profile-arrow { font-size: 0.7rem; color: var(--sidebar-text); opacity: 0.5; }

/* ── 태블릿 (640px ~ 992px): 아이콘만 표시 ─────────────── */
@media (max-width: 992px) {
  .sidebar { width: 64px; }

  .brand-name,
  .nav-label,
  .nav-chevron,
  .submenu,
  .profile-text,
  .profile-arrow { display: none; }

  .sidebar-brand  { justify-content: center; padding: 0.25rem 0 1rem; }
  .nav-item       { justify-content: center; padding: 0.75rem; }
  .nav-icon       { width: auto; opacity: 1; }
  .sidebar-profile { justify-content: center; padding: 0.75rem; }
}

/* ── 모바일 (< 640px): 사이드바 슬라이드 인/아웃 ─────────
   기본값: 화면 왼쪽 바깥으로 숨김
   .open 클래스: 오른쪽으로 슬라이드 인
─────────────────────────────────────────────────────────── */
@media (max-width: 639px) {
  .sidebar {
    width: 240px;            /* 태블릿 64px 덮어쓰기 */
    transform: translateX(-100%);
    transition: transform 0.28s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: var(--shadow-lg);
  }

  .sidebar.open {
    transform: translateX(0);
  }

  /* 사이드바 열렸을 때 라벨 복원 (태블릿의 display:none 덮어쓰기) */
  .sidebar.open .brand-name    { display: block; }
  .sidebar.open .nav-label     { display: block; flex: 1; }
  .sidebar.open .nav-chevron   { display: block; }
  .sidebar.open .submenu       { display: flex; }
  .sidebar.open .profile-text  { display: block; flex: 1; min-width: 0; }
  .sidebar.open .profile-arrow { display: block; }

  /* 정렬 복원 */
  .sidebar.open .sidebar-brand  { justify-content: flex-start; padding: 0.25rem 1.25rem 1rem; }
  .sidebar.open .nav-item       { justify-content: flex-start; padding: 0.65rem 0.875rem; }
  .sidebar.open .nav-icon       { width: 1rem; }
  .sidebar.open .sidebar-profile { justify-content: flex-start; padding: 0.75rem 0.875rem; }
}
</style>

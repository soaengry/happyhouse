<template>
  <nav class="navbar">
    <!-- 햄버거 버튼 (모바일용) -->
    <button class="hamburger" @click="toggleMenu" v-if="isMobile">☰</button>

    <!-- 메뉴 영역 -->
    <ul class="menu" :class="{ open: menuOpen || !isMobile }">
      <li class="menu-item">
        <span class="menu-icon">🏠</span>
        <span class="menu-name" v-if="!isTablet && !isMobile">매물 검색</span>
      </li>
      <!-- 추가 메뉴 항목들 -->
    </ul>
  </nav>
</template>

<script>
export default {
  name: "TheNavbar",
  data() {
    return {
      menuOpen: false,
      windowWidth: window.innerWidth,
    };
  },
  computed: {
    isMobile() {
      return this.windowWidth < 768;
    },
    isTablet() {
      return this.windowWidth >= 768 && this.windowWidth < 992;
    },
    isDesktop() {
      return this.windowWidth >= 992;
    },
  },
  mounted() {
    window.addEventListener("resize", this.handleResize);
  },
  beforeUnmount() {
    window.removeEventListener("resize", this.handleResize);
  },
  methods: {
    toggleMenu() {
      this.menuOpen = !this.menuOpen;
    },
    handleResize() {
      this.windowWidth = window.innerWidth;
    },
  },
};
</script>

<style scoped>
.navbar {
  background-color: #fff;
  padding: 1rem;
  border-right: 1px solid #ddd;
}

.hamburger {
  display: none;
  font-size: 1.5rem;
  background: none;
  border: none;
  cursor: pointer;
}

.menu {
  list-style: none;
  padding: 0;
  margin: 0;
  transition: max-height 0.3s ease;
  overflow: hidden;
}

.menu.open {
  max-height: 500px;
}

.menu:not(.open) {
  max-height: 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 0.5rem;
}

.menu-icon {
  margin-right: 0.5rem;
}

/* 반응형 */
@media (max-width: 767px) {
  .hamburger {
    display: block;
  }
  .menu-name {
    display: none;
  }
}

@media (min-width: 768px) and (max-width: 991px) {
  .menu-name {
    display: none;
  }
}

@media (min-width: 992px) {
  .menu {
    max-height: none !important;
  }
  .menu-name {
    display: inline;
  }
}
</style>

<template>
  <div class="page">
    <!-- Page Header -->
    <div class="content-header">
      <h2 v-if="!isBookmarkMode">매물 검색</h2>
      <h2 v-else>관심 매물</h2>
      <p v-if="!isBookmarkMode">지역 또는 아파트명으로 매물을 검색하세요.</p>
    </div>

    <div class="content-body">
      <!-- Search Bar -->
      <div v-if="!isBookmarkMode" class="search-bar">
        <div class="select-group">
          <select v-model="sidoCode" @change="onChangeSido" class="search-select">
            <option value="0">시/도</option>
            <option v-for="sido in sidoList" :key="sido.sidoCode" :value="sido.sidoCode">
              {{ sido.sidoName }}
            </option>
          </select>
          <select v-model="gugunCode" @change="onChangeGugun" class="search-select">
            <option value="0">구/군</option>
            <option v-for="gugun in gugunList" :key="gugun.gugunCode" :value="gugun.gugunCode">
              {{ gugun.gugunName }}
            </option>
          </select>
          <select v-model="dongCode" class="search-select">
            <option value="0">읍/면/동</option>
            <option v-for="dong in dongList" :key="dong.dongCode" :value="dong.dongCode">
              {{ dong.dongName }}
            </option>
          </select>
        </div>
        <div class="keyword-group">
          <input
            v-model="keyword"
            type="text"
            placeholder="아파트명으로 검색"
            @keydown.enter="onSearch"
            class="keyword-input"
          />
          <button class="btn btn-primary search-btn" @click="onSearch">
            <font-awesome-icon icon="fa-solid fa-magnifying-glass" />
            검색
          </button>
        </div>
      </div>

      <!-- Result Info -->
      <div class="result-info">
        <span class="result-count">
          총 <strong>{{ houseCount }}</strong> 건
        </span>
      </div>

      <!-- House List -->
      <div ref="scrollContainer" class="scroll-area">
        <div v-if="houseList.length > 0" class="card-grid">
          <HouseCard
            v-for="house in houseList"
            :key="house.aptCode"
            :house="house"
            @click="openModal(house)"
          />
        </div>

        <div v-else-if="!isLoading" class="empty-state">
          <font-awesome-icon icon="fa-solid fa-magnifying-glass" class="empty-icon" />
          <p>검색 결과가 없습니다.</p>
          <span>다른 지역이나 키워드로 검색해 보세요.</span>
        </div>

        <div v-if="isLoading" class="loading-area">
          <font-awesome-icon icon="fa-solid fa-spinner" spin />
          <span>불러오는 중...</span>
        </div>
      </div>
    </div>
  </div>

  <HouseDetailModal
    v-if="selectedHouse"
    :house="selectedHouse"
    @close="closeModal"
  />
</template>

<script setup>
import { onMounted, onBeforeUnmount, ref, watch } from "vue";
import { useRoute } from "vue-router";
import { storeToRefs } from "pinia";
import { useHouseStore } from "@/stores/houseStore";
import { useAddressStore } from "@/stores/addressStore";
import HouseCard from "@/components/HouseCard.vue";
import HouseDetailModal from "@/components/HouseDetailModal.vue";

const route = useRoute();
const houseStore = useHouseStore();
const addressStore = useAddressStore();

const isBookmarkMode = ref(false);
const selectedHouse = ref(null);
const scrollContainer = ref(null);
let observer;

const { houseCount, houseList, sidoCode, gugunCode, dongCode, keyword, isLoading } =
  storeToRefs(houseStore);
const { sidoList, gugunList, dongList } = storeToRefs(addressStore);

watch(
  () => route.fullPath,
  (newPath) => {
    if (newPath.startsWith("/bookmark/house")) {
      isBookmarkMode.value = true;
      houseStore.getBookmarkHouseList();
    } else {
      isBookmarkMode.value = false;
      if (route.query.sidoCode) {
        houseStore.sidoCode = Number(route.query.sidoCode);
        houseStore.gugunCode = Number(route.query.gugunCode);
        houseStore.dongCode = Number(route.query.dongCode);
      }
      houseStore.resetHouseList();
      houseStore.fetchNextPage();
    }
  },
  { immediate: true },
);

onMounted(async () => {
  await addressStore.getSidoList();
  houseStore.loadBookmarks();
  if (!isBookmarkMode.value) setupInfiniteScroll();
});

onBeforeUnmount(() => observer?.disconnect());

function setupInfiniteScroll() {
  observer = new IntersectionObserver(
    ([entry]) => {
      if (entry.isIntersecting && !isLoading.value) houseStore.fetchNextPage();
    },
    { threshold: 1.0 },
  );
  const sentinel = document.createElement("div");
  scrollContainer.value?.appendChild(sentinel);
  observer.observe(sentinel);
}

function onChangeSido() {
  houseStore.gugunCode = 0;
  houseStore.dongCode = 0;
  addressStore.gugunList = [];
  addressStore.dongList = [];
  addressStore.getGugunList(houseStore.sidoCode);
}

function onChangeGugun() {
  houseStore.dongCode = 0;
  addressStore.dongList = [];
  addressStore.getDongList(houseStore.gugunCode);
}

function onSearch() {
  houseStore.resetHouseList();
  houseStore.fetchNextPage();
}

function openModal(house) { selectedHouse.value = house; }
function closeModal()      { selectedHouse.value = null; }
</script>

<style scoped>
.page { min-height: 100vh; }

/* Search Bar */
.search-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  margin-bottom: 1.25rem;
  padding-bottom: 1.25rem;
  border-bottom: 1px solid var(--border);
}

.select-group {
  display: flex;
  gap: 0.5rem;
  flex: 1;
  min-width: 0;
}
.search-select {
  flex: 1;
  min-width: 80px;
  margin-right: 0;
}

.keyword-group {
  display: flex;
  gap: 0.5rem;
  flex: 1.5;
  min-width: 200px;
}
.keyword-input { flex: 1; margin-right: 0; }
.search-btn { flex-shrink: 0; white-space: nowrap; }

/* Result Info */
.result-info {
  margin-bottom: 1rem;
}
.result-count {
  font-size: 0.85rem;
  color: var(--text-muted);
}
.result-count strong {
  color: var(--primary);
  font-weight: 700;
}

/* Card Grid */
.scroll-area { min-height: 60vh; }

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 1rem;
}

/* Empty State */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 1rem;
  color: var(--text-muted);
  text-align: center;
}
.empty-icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  opacity: 0.3;
}
.empty-state p {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-secondary);
  margin-bottom: 0.4rem;
}
.empty-state span { font-size: 0.85rem; }

/* Loading */
.loading-area {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 1.5rem;
  color: var(--text-muted);
  font-size: 0.875rem;
}
</style>

<template>
  <div>
    <div class="content-header">
      <h2 v-if="!isBookmarkMode">매물 검색</h2>
      <h2 v-else>관심 매물</h2>
    </div>
    <section class="content-body">
      <!-- 검색창 -->
      <fieldset
        class="form-group d-flex align-items-center justify-content-center mb-3 mt-3"
        v-if="!isBookmarkMode"
      >
        <select class="form-select" v-model="sidoCode" @change="onChangeSido">
          <option value="0">시/도</option>
          <option
            v-for="sido in sidoList"
            :key="sido.sidoCode"
            :value="sido.sidoCode"
          >
            {{ sido.sidoName }}
          </option>
        </select>

        <select class="form-select" v-model="gugunCode" @change="onChangeGugun">
          <option value="0">구/군</option>
          <option
            v-for="gugun in gugunList"
            :key="gugun.gugunCode"
            :value="gugun.gugunCode"
          >
            {{ gugun.gugunName }}
          </option>
        </select>

        <select class="form-select" v-model="dongCode">
          <option value="0">읍/면/동</option>
          <option
            v-for="dong in dongList"
            :key="dong.dongCode"
            :value="dong.dongCode"
          >
            {{ dong.dongName }}
          </option>
        </select>

        <input
          v-model="keyword"
          class="form-control"
          type="text"
          placeholder="아파트명"
          @keydown.enter="onSearch"
        />
        <!-- 검색 버튼 -->
        <button class="btn btn-primary" type="button" @click="onSearch">
          <font-awesome-icon icon="fa-solid fa-magnifying-glass" />
        </button>
      </fieldset>
      <!-- end of form-group -->

      <!-- 검색 결과 -->
      <div class="list-info d-flex">
        <p>
          총&nbsp;<span class="text-primary">{{ houseCount }} </span>&nbsp;건
        </p>
      </div>

      <div ref="scrollContainer" class="scroll-container">
        <div class="card-grid">
          <HouseCard
            v-for="house in houseList"
            :key="house.aptCode"
            :house="house"
            @click="openModal(house)"
          />
        </div>

        <div v-if="isLoading" class="text-center py-3">로딩 중...</div>
      </div>
    </section>
  </div>

  <HouseDetailModal
    v-if="selectedHouse"
    :house="selectedHouse"
    @close="closeModal"
  />
</template>

<script setup>
import { onMounted, ref, onBeforeUnmount, watch } from "vue";
import { useRoute } from "vue-router";
import { storeToRefs } from "pinia";
import { useHouseStore } from "@/stores/houseStore";
import { useAddressStore } from "@/stores/addressStore";

import HouseCard from "@/components/HouseCard.vue";
import HouseDetailModal from "@/components/HouseDetailModal.vue";

const route = useRoute();

const isBookmarkMode = ref(false);
const selectedHouse = ref(null);
const scrollContainer = ref(null);
let observer;

const houseStore = useHouseStore();
const addressStore = useAddressStore();

const {
  houseCount,
  houseList,
  sidoCode,
  gugunCode,
  dongCode,
  keyword,
  isLoading,
} = storeToRefs(houseStore);
const { sidoList, gugunList, dongList } = storeToRefs(addressStore);

watch(
  () => route.fullPath,
  (newPath) => {
    if (newPath === "/bookmark/house") {
      houseStore.getBookmarkList();
    } else {
      houseStore.resetHouseList();
      houseStore.fetchNextPage();
    }
  },
);

onMounted(async () => {
  await addressStore.getSidoList();
  houseStore.loadBookmarks();

  if (route.path === "/bookmark/house") {
    isBookmarkMode.value = true;
    await houseStore.getBookmarkList();
  } else {
    houseStore.resetHouseList();
    houseStore.fetchNextPage();

    observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting && !isLoading.value) {
          houseStore.fetchNextPage();
        }
      },
      { threshold: 1.0 },
    );

    const sentinel = document.createElement("div");
    scrollContainer.value.appendChild(sentinel);
    observer.observe(sentinel);
  }
});

onBeforeUnmount(() => {
  observer?.disconnect();
});

// 시/도 선택필드 변경 시
function onChangeSido() {
  // 구/군, 동 코드 초기화
  houseStore.gugunCode = 0;
  houseStore.dongCode = 0;

  // 리스트 초기화
  addressStore.gugunList = [];
  addressStore.dongList = [];

  // 새 시/도에 맞는 구/군 목록 불러오기
  addressStore.getGugunList(houseStore.sidoCode);
}

function onChangeGugun() {
  // 동 초기화
  houseStore.dongCode = 0;
  addressStore.dongList = [];

  addressStore.getDongList(houseStore.gugunCode);
}

function onSearch() {
  houseStore.resetHouseList();
  houseStore.fetchNextPage();
}

function openModal(house) {
  selectedHouse.value = house;
}

function closeModal() {
  selectedHouse.value = null;
}
</script>

<style scoped>
.content-header {
  padding: 1rem 1rem 0 1rem;
}

.content-body {
  margin: 1rem;
  padding: 1rem;
  background-color: white;
  border-radius: 1rem;
}

.scroll-container {
  padding: 1rem 0;
  min-height: 80vh;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr); /* ✅ 2열 */
  gap: 1rem;
}

select,
input {
  margin-right: 0.5rem;
}

/* 반응형: 모바일에서는 1열 */
@media (max-width: 768px) {
  .card-grid {
    grid-template-columns: 1fr;
  }
}
</style>

<template>
  <div>
    <div class="content-header">
      <h2>관심 지역</h2>
    </div>
    <section class="content-body">
      <!-- 검색창 -->
      <form
        @submit.prevent="addRegion"
        class="form-group d-flex align-items-center justify-content-center mb-3 mt-3"
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

        <button class="btn btn-primary" type="submit">
          <font-awesome-icon icon="fa-solid fa-plus" />
        </button>
      </form>
      <!-- end of form-group -->
      <!-- 관심지역 테이블 -->
      <table>
        <thead>
          <tr>
            <th>시/도</th>
            <th>구/군</th>
            <th>읍/면/동</th>
            <th>검색</th>
            <th>삭제</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="region in bookmarkRegionList" :key="region.dongCode">
            <td>{{ region.sidoName }}</td>
            <td>{{ region.gugunName }}</td>
            <td>{{ region.dongName }}</td>
            <td>
              <button class="btn text-primary" @click="searchRegion(region)">
                <font-awesome-icon icon="fa-solid fa-magnifying-glass" />
              </button>
            </td>
            <td>
              <button
                class="btn text-danger"
                @click="removeRegion(region.dongCode)"
              >
                <font-awesome-icon icon="fa-solid fa-xmark" />
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>

  <HouseDetailModal
    v-if="selectedHouse"
    :house="selectedHouse"
    @close="closeModal"
  />
</template>

<script setup>
import { onMounted } from "vue";
import { useRouter } from "vue-router";
import { storeToRefs } from "pinia";
import { useHouseStore } from "@/stores/houseStore";
import { useAddressStore } from "@/stores/addressStore";
import addressService from "@/services/addressService";

const router = useRouter();

const houseStore = useHouseStore();
const addressStore = useAddressStore();

const { sidoList, gugunList, dongList, bookmarkRegionList } =
  storeToRefs(addressStore);
const { sidoCode, gugunCode, dongCode } = storeToRefs(houseStore);

onMounted(async () => {
  await addressStore.getSidoList();
  await loadRegions();
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
// 관심지역 목록 불러오기
async function loadRegions() {
  addressStore.bookmarkRegionList = await addressService.getBookmarkRegions();
}

// 관심지역 추가
async function addRegion() {
  if (dongCode.value === 0) return;
  await addressService.addBookmarkRegion(dongCode.value);
  await loadRegions(); // 목록 갱신
}

// 관심지역 삭제
async function removeRegion(dongCode) {
  await addressService.removeBookmarkRegion(dongCode);
  await loadRegions(); // 목록 갱신
}

// BookmarkRegion.vue
async function searchRegion(region) {
  houseStore.dongCode = region.dongCode;
  router.push({
    path: "/",
    query: {
      dongCode: region.dongCode,
    },
  });
}
</script>

<style scoped>
td {
  padding: 0;
  vertical-align: middle;
}
</style>

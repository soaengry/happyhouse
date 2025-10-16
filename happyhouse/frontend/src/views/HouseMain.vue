<template>
  <div>
    <div class="title-box d-flex justify-content-between">
      <h3>매물 검색</h3>
    </div>

    <!-- 검색창 -->
    <fieldset
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

      <input
        v-model="keyword"
        class="form-control"
        type="text"
        placeholder="아파트명"
      />
      <!-- 검색 버튼 -->
      <button
        class="btn btn-primary"
        type="button"
        @click="houseStore.fetchHouseList"
      >
        <font-awesome-icon icon="fa-solid fa-magnifying-glass" />
      </button>
    </fieldset>
    <!-- end of form-group -->

    <!-- 검색 결과 -->
    <div class="list-info d-flex">
      <p>
        총&nbsp;<span class="text-primary">{{ count }}</span
        >&nbsp;건
      </p>
    </div>

    <div class="table-responsive">
      <!-- 데이터 있을 때 -->
      <table
        v-if="houseList.length > 0"
        class="table table-hover text-center mb-3"
      >
        <colgroup>
          <col width="8.8%" />
          <col width="28%" />
          <col width="27%" />
          <col width="10%" />
          <col width="14.2%" />
          <col width="12%" />
        </colgroup>
        <thead>
          <tr>
            <th>번호</th>
            <th>아파트명</th>
            <th>주소</th>
            <th>건축연도</th>
            <th>최근거래금액</th>
            <th>최근거래일</th>
          </tr>
        </thead>
        <tbody v-if="isLoading">
          <tr v-for="n in 10" :key="n">
            <td colspan="6">
              <div class="skeleton-row">&nbsp;</div>
            </td>
          </tr>
        </tbody>
        <tbody v-else>
          <tr
            v-for="house in houseList"
            :key="house.no"
            style="cursor: pointer"
          >
            <td>{{ house.no }}</td>
            <td class="text-start">{{ house.aptName }}</td>
            <td>{{ house.address }}</td>
            <td>{{ house.buildYear }}</td>
            <td>{{ house.dealAmount }}만 원</td>
            <td>
              {{
                makeDateStr(house.dealYear, house.dealMonth, house.dealDay, "-")
              }}
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 데이터 없을 때 -->
      <div
        v-else
        class="w-100 text-center"
        style="border-bottom: 1px solid #dedede"
      >
        <p class="pt-5 pb-5 mb-0">조회된 데이터가 없습니다.</p>
      </div>
    </div>
    <!-- end of .table-responsive-->
    <!-- Pagination -->
    <ThePagination @page-changed="onPageChanged" />
  </div>
</template>

<script setup>
import { onMounted } from "vue";
import { storeToRefs } from "pinia";
import { useHouseStore } from "@/stores/houseStore";
import { useAddressStore } from "@/stores/addressStore";
import ThePagination from "@/components/ThePagination.vue";
import { makeDateStr } from "@/utils/date";

const houseStore = useHouseStore();
const addressStore = useAddressStore();

const { count, houseList, sidoCode, gugunCode, dongCode, keyword, isLoading } =
  storeToRefs(houseStore);
const { sidoList, gugunList, dongList } = storeToRefs(addressStore);

onMounted(async () => {
  await addressStore.getSidoList();
  await houseStore.fetchHouseList();
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

// 페이지 변경 시
function onPageChanged() {
  houseStore.fetchHouseList();
}
</script>
<style scoped>
.skeleton-row {
  height: 1.6rem;
  background: linear-gradient(90deg, #eee, #ddd, #eee);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
  border-radius: 4px;
}

@keyframes skeleton-loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}
</style>

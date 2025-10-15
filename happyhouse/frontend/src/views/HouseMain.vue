<script setup>
import { onMounted, ref } from "vue";
import { storeToRefs } from "pinia";
import { useHouseStore } from "@/stores/houseStore";
import { useAddressStore } from "@/stores/addressStore";

const houseStore = useHouseStore();
const addressStore = useAddressStore();

const { count, houseList } = storeToRefs(houseStore);
const { sido, gugun, dong, sidoList, gugunList, dongList } =
  storeToRefs(addressStore);

const keyword = ref("");

onMounted(async () => {
  await addressStore.getSidoList();
  await houseStore.fetchHouseList();
});
</script>

<template>
  <div>
    <div class="title-box d-flex justify-content-between">
      <h3>매물 검색</h3>
    </div>

    <!-- 검색창 -->
    <fieldset
      class="form-group d-flex align-items-center justify-content-center mb-3 mt-3"
    >
      <select
        class="form-select"
        v-model="sido"
        @change="addressStore.getGugunList"
      >
        <option value="0">시/도</option>
        <option v-for="s in sidoList" :key="s.code" :value="s.code">
          {{ s.name }}
        </option>
      </select>

      <select
        class="form-select"
        v-model="gugun"
        @change="addressStore.getDongList"
      >
        <option value="0">구/군</option>
        <option v-for="g in gugunList" :key="g.code" :value="g.code">
          {{ g.name }}
        </option>
      </select>

      <select class="form-select" v-model="dong">
        <option value="0">읍/면/동</option>
        <option v-for="d in dongList" :key="d.code" :value="d.code">
          {{ d.name }}
        </option>
      </select>

      <input
        v-model="keyword"
        class="form-control"
        type="text"
        placeholder="아파트명"
      />
      <button class="btn btn-primary" type="button">
        <font-awesome-icon icon="fa-solid fa-magnifying-glass" />
      </button>
    </fieldset>

    <!-- 검색 결과 -->
    <div class="list-info d-flex">
      <p>
        총&nbsp;<span class="text-primary">{{ count }}</span
        >&nbsp;건
      </p>
    </div>

    <div class="table-responsive">
      <table class="table table-hover text-center mb-0">
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
        <tbody v-if="houseList.length > 0">
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
              {{ house.dealYear }}-{{ house.dealMonth }}-{{ house.dealDay }}
            </td>
          </tr>
        </tbody>
      </table>
      <div
        v-if="houseList.length == 0"
        class="w-100 text-center"
        style="border-bottom: 1px solid #dedede"
      >
        <p class="pt-5 pb-5 mb-0">조회된 데이터가 없습니다.</p>
      </div>
    </div>
  </div>
</template>

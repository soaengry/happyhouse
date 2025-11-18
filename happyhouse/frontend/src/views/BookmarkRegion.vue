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
      <table class="bookmark-table">
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
          <tr
            v-for="region in bookmarkRegionList"
            :key="region.dongCode"
            @click="
              [drawChart(region.dongCode), (selectedRow = region.dongCode)]
            "
            :class="{ selected: selectedRow === region.dongCode }"
          >
            <td>{{ region.sidoName }}</td>
            <td>{{ region.gugunName }}</td>
            <td>{{ region.dongName }}</td>
            <td>
              <button
                class="btn text-primary"
                @click.stop="searchRegion(region)"
              >
                <font-awesome-icon icon="fa-solid fa-magnifying-glass" />
              </button>
            </td>
            <td>
              <button
                class="btn text-danger"
                @click.stop="removeRegion(region.dongCode)"
              >
                <font-awesome-icon icon="fa-solid fa-xmark" />
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      <div class="population-content">
        <!-- 지역별 생활 인구 수 Bar Chart -->
        <div>
          <canvas id="total-bar"></canvas>
        </div>
        <!-- 성별/나이 생활 인구 구분 Donut Chart -->
        <div class="gender-chart">
          <div>
            <canvas id="male-donut"></canvas>
          </div>
          <div>
            <canvas id="female-donut"></canvas>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { storeToRefs } from "pinia";
import { useHouseStore } from "@/stores/houseStore";
import { useAddressStore } from "@/stores/addressStore";
import addressService from "@/services/addressService";
import Chart from "chart.js/auto";

const router = useRouter();

const houseStore = useHouseStore();
const addressStore = useAddressStore();

const { sidoList, gugunList, dongList, bookmarkRegionList } =
  storeToRefs(addressStore);
const { sidoCode, gugunCode, dongCode, population } = storeToRefs(houseStore);
const selectedRow = ref(null);

// 차트 인스턴스 저장 (중복 생성 방지)
let barChartInstance, maleDonutInstance, femaleDonutInstance;

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

async function drawChart(dongCode) {
  await houseStore.getPopulation(dongCode);
  console.log(selectedRow);
  // 기존 차트 제거
  if (barChartInstance) barChartInstance.destroy();
  if (maleDonutInstance) maleDonutInstance.destroy();
  if (femaleDonutInstance) femaleDonutInstance.destroy();

  // Bar Chart
  barChartInstance = new Chart(document.getElementById("total-bar"), {
    type: "bar",
    data: {
      labels: ["총 인구", "남성 인구", "여성 인구"],
      datasets: [
        {
          label: "생활 인구 수",
          data: [
            Math.round(population.value.totalLocal),
            Math.round(population.value.totalMale),
            Math.round(population.value.totalFemale),
          ],
          backgroundColor: ["#36A2EB", "#4BC0C0", "#FF6384"],
        },
      ],
    },
    options: {
      maintainAspectRatio: false,
      plugins: {
        title: {
          display: true,
          text: "지역별 생활 인구 수",
        },
      },
    },
  });

  // Male Donut Chart
  maleDonutInstance = new Chart(document.getElementById("male-donut"), {
    type: "doughnut",
    data: {
      labels: ["0~19세", "20~39세", "40~59세", "60~74세"],
      datasets: [
        {
          data: [
            Math.round(population.value.maleTo19),
            Math.round(population.value.maleTo39),
            Math.round(population.value.maleTo59),
            Math.round(population.value.maleTo74),
          ],
          backgroundColor: ["#36A2EB", "#4BC0C0", "#FFCE56", "#9966FF"],
        },
      ],
    },
    options: {
      maintainAspectRatio: false,
      plugins: {
        title: {
          display: true,
          text: "남성 연령별 생활 인구",
        },
      },
    },
  });

  // Female Donut Chart
  femaleDonutInstance = new Chart(document.getElementById("female-donut"), {
    type: "doughnut",
    data: {
      labels: ["0~19세", "20~39세", "40~59세", "60~74세"],
      datasets: [
        {
          data: [
            Math.round(population.value.femaleTo19),
            Math.round(population.value.femaleTo39),
            Math.round(population.value.femaleTo59),
            Math.round(population.value.femaleTo74),
          ],
          backgroundColor: ["#FF6384", "#FF9F40", "#FFCD56", "#C9CBCF"],
        },
      ],
    },
    options: {
      maintainAspectRatio: false,
      plugins: {
        title: {
          display: true,
          text: "여성 연령별 생활 인구",
        },
      },
    },
  });
}
</script>

<style scoped>
.bookmark-table {
  margin-bottom: 1rem;
}

table tbody tr.selected {
  background-color: #e9f2ff; /* hover와 동일한 색상 */
}

td {
  padding: 0;
  vertical-align: middle;
}

.population-content {
  display: flex;
  justify-content: space-between;
  width: 90%;
  margin: 0 auto;
}

.population-content div {
  width: 45%;
}

.gender-chart {
  display: flex;
}

@media (max-width: 768px) {
  .population-content {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }
  .population-content div {
    width: 100%;
    height: 200px;
    margin-top: 1rem;
  }
}
</style>

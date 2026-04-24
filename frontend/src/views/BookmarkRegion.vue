<template>
  <div>
    <div class="content-header">
      <h2>관심 지역</h2>
      <p>자주 확인하는 지역을 북마크하고 인구 통계를 분석하세요.</p>
    </div>

    <div class="content-body">
      <!-- Add Region Form -->
      <form @submit.prevent="addRegion" class="region-form">
        <div class="select-group">
          <select v-model="sidoCode" @change="onChangeSido" class="region-select">
            <option value="0">시/도</option>
            <option v-for="sido in sidoList" :key="sido.sidoCode" :value="sido.sidoCode">
              {{ sido.sidoName }}
            </option>
          </select>
          <select v-model="gugunCode" @change="onChangeGugun" class="region-select">
            <option value="0">구/군</option>
            <option v-for="gugun in gugunList" :key="gugun.gugunCode" :value="gugun.gugunCode">
              {{ gugun.gugunName }}
            </option>
          </select>
          <select v-model="dongCode" class="region-select">
            <option value="0">읍/면/동</option>
            <option v-for="dong in dongList" :key="dong.dongCode" :value="dong.dongCode">
              {{ dong.dongName }}
            </option>
          </select>
        </div>
        <button class="btn btn-primary add-btn" type="submit" :disabled="dongCode === 0">
          <font-awesome-icon icon="fa-solid fa-plus" />
          추가
        </button>
      </form>

      <!-- Bookmark Table -->
      <div class="table-wrap">
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
            <tr
              v-for="region in bookmarkRegionList"
              :key="region.dongCode"
              :class="{ selected: selectedRow === region.dongCode }"
              @click="onRowClick(region.dongCode)"
            >
              <td>{{ region.sidoName }}</td>
              <td>{{ region.gugunName }}</td>
              <td><strong>{{ region.dongName }}</strong></td>
              <td>
                <button class="icon-btn text-primary" @click.stop="searchRegion(region)">
                  <font-awesome-icon icon="fa-solid fa-magnifying-glass" />
                </button>
              </td>
              <td>
                <button class="icon-btn text-danger" @click.stop="removeRegion(region.dongCode)">
                  <font-awesome-icon icon="fa-solid fa-xmark" />
                </button>
              </td>
            </tr>
            <tr v-if="bookmarkRegionList.length === 0">
              <td colspan="5" class="empty-row">북마크된 지역이 없습니다.</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Stats Section -->
      <template v-if="selectedRow !== null">
        <div class="section-title">
          <span>인구 통계</span>
          <span class="section-badge">선택된 지역 기준</span>
        </div>

        <div v-if="isLoadingChart" class="loading-state">
          <font-awesome-icon icon="fa-solid fa-spinner" spin />
          <span>통계 데이터 불러오는 중...</span>
        </div>

        <div v-else-if="population" class="charts-grid">
          <div class="chart-card">
            <div class="chart-title">생활 인구 현황</div>
            <div class="chart-wrap"><canvas id="total-bar"></canvas></div>
          </div>
          <div class="chart-card">
            <div class="chart-title">남성 연령별 인구</div>
            <div class="chart-wrap chart-wrap-sm"><canvas id="male-donut"></canvas></div>
          </div>
          <div class="chart-card">
            <div class="chart-title">여성 연령별 인구</div>
            <div class="chart-wrap chart-wrap-sm"><canvas id="female-donut"></canvas></div>
          </div>
        </div>

        <!-- Regional News -->
        <div v-if="newsList.length" class="section-title">
          <span>지역 뉴스</span>
        </div>
        <div v-if="isLoading" class="loading-state">
          <font-awesome-icon icon="fa-solid fa-spinner" spin />
        </div>
        <div v-else-if="newsList.length" class="news-list">
          <a
            v-for="news in newsList"
            :key="news.url"
            :href="news.url"
            target="_blank"
            rel="noopener noreferrer"
            class="news-row"
          >
            <img v-if="news.img" :src="news.img" class="news-thumb" alt="" />
            <div class="news-info">
              <p class="news-title">{{ news.title }}</p>
              <span class="news-meta">{{ news.publish }} · {{ news.date }}</span>
            </div>
          </a>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from "vue";
import { useRouter } from "vue-router";
import { storeToRefs } from "pinia";
import { useHouseStore } from "@/stores/houseStore";
import { useAddressStore } from "@/stores/addressStore";
import addressService from "@/services/addressService";
import Chart from "chart.js/auto";

const router       = useRouter();
const houseStore   = useHouseStore();
const addressStore = useAddressStore();

const { sidoList, gugunList, dongList, bookmarkRegionList } = storeToRefs(addressStore);
const { sidoCode, gugunCode, dongCode, population, newsList } = storeToRefs(houseStore);

const selectedRow    = ref(null);
const isLoading      = ref(false);
const isLoadingChart = ref(false);

let barChart, maleDonut, femaleDonut;

onMounted(async () => {
  await addressStore.getSidoList();
  addressStore.bookmarkRegionList = await addressService.getBookmarkRegions();
});

onUnmounted(() => {
  barChart?.destroy();
  maleDonut?.destroy();
  femaleDonut?.destroy();
});

function onChangeSido() {
  houseStore.gugunCode = 0;
  houseStore.dongCode  = 0;
  addressStore.gugunList = [];
  addressStore.dongList  = [];
  addressStore.getGugunList(houseStore.sidoCode);
}

function onChangeGugun() {
  houseStore.dongCode = 0;
  addressStore.dongList = [];
  addressStore.getDongList(houseStore.gugunCode);
}

async function loadRegions() {
  addressStore.bookmarkRegionList = await addressService.getBookmarkRegions();
}

async function addRegion() {
  if (dongCode.value === 0) return;
  await addressService.addBookmarkRegion(dongCode.value);
  await loadRegions();
}

async function removeRegion(code) {
  await addressService.removeBookmarkRegion(code);
  if (selectedRow.value === code) selectedRow.value = null;
  await loadRegions();
}

async function searchRegion(region) {
  houseStore.dongCode = region.dongCode;
  router.push({ path: "/", query: { dongCode: region.dongCode } });
}

async function onRowClick(code) {
  selectedRow.value = code;
  await drawChart(code);
}

async function drawChart(code) {
  isLoadingChart.value = true;
  isLoading.value = true;

  await Promise.all([
    houseStore.getPopulation(code),
    houseStore.getNews(code),
  ]);

  isLoading.value = false;
  isLoadingChart.value = false;

  if (!population.value) return;

  // population이 set된 후 Vue DOM 업데이트 대기 (canvas가 v-else-if로 조건부 렌더링됨)
  await nextTick();

  barChart?.destroy();
  maleDonut?.destroy();
  femaleDonut?.destroy();

  const CHART_COLORS = {
    blue:   "#3B82F6",
    teal:   "#14B8A6",
    pink:   "#EC4899",
    purple: "#8B5CF6",
    amber:  "#F59E0B",
    slate:  "#64748B",
  };

  barChart = new Chart(document.getElementById("total-bar"), {
    type: "bar",
    data: {
      labels: ["총 인구", "남성", "여성"],
      datasets: [{
        data: [
          Math.round(population.value.totalLocal),
          Math.round(population.value.totalMale),
          Math.round(population.value.totalFemale),
        ],
        backgroundColor: [CHART_COLORS.blue, CHART_COLORS.teal, CHART_COLORS.pink],
        borderRadius: 4,
        borderSkipped: false,
      }],
    },
    options: {
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        y: { grid: { color: "#F1F5F9" }, ticks: { color: "#94A3B8" } },
        x: { grid: { display: false }, ticks: { color: "#64748B" } },
      },
    },
  });

  const AGE_LABELS = ["0~19세", "20~39세", "40~59세", "60~74세"];
  const DONUT_COLORS_M = [CHART_COLORS.blue, CHART_COLORS.teal, CHART_COLORS.purple, CHART_COLORS.slate];
  const DONUT_COLORS_F = [CHART_COLORS.pink, CHART_COLORS.amber, "#F87171", "#CBD5E1"];

  maleDonut = new Chart(document.getElementById("male-donut"), {
    type: "doughnut",
    data: {
      labels: AGE_LABELS,
      datasets: [{
        data: [
          Math.round(population.value.maleTo19),
          Math.round(population.value.maleTo39),
          Math.round(population.value.maleTo59),
          Math.round(population.value.maleTo74),
        ],
        backgroundColor: DONUT_COLORS_M,
      }],
    },
    options: {
      maintainAspectRatio: false,
      plugins: { legend: { position: "bottom", labels: { font: { size: 11 } } } },
      cutout: "60%",
    },
  });

  femaleDonut = new Chart(document.getElementById("female-donut"), {
    type: "doughnut",
    data: {
      labels: AGE_LABELS,
      datasets: [{
        data: [
          Math.round(population.value.femaleTo19),
          Math.round(population.value.femaleTo39),
          Math.round(population.value.femaleTo59),
          Math.round(population.value.femaleTo74),
        ],
        backgroundColor: DONUT_COLORS_F,
      }],
    },
    options: {
      maintainAspectRatio: false,
      plugins: { legend: { position: "bottom", labels: { font: { size: 11 } } } },
      cutout: "60%",
    },
  });
}
</script>

<style scoped>
/* Region Form */
.region-form {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1.25rem;
  flex-wrap: wrap;
}
.select-group { display: flex; gap: 0.5rem; flex: 1; min-width: 0; }
.region-select { flex: 1; min-width: 80px; margin-right: 0; }
.add-btn { flex-shrink: 0; white-space: nowrap; }

/* Table */
.table-wrap {
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  overflow: hidden;
  margin-bottom: 1.5rem;
}
.table-wrap table { margin: 0; }
.table-wrap td { padding: 0.625rem 1rem; vertical-align: middle; }

tbody tr.selected { background: var(--primary-50) !important; }

.icon-btn {
  background: none;
  border: none;
  padding: 0.3rem 0.5rem;
  border-radius: var(--radius-sm);
  font-size: 0.875rem;
  transition: background 0.15s;
}
.icon-btn:hover { background: var(--bg); }

.empty-row { color: var(--text-muted); font-size: 0.875rem; padding: 1.5rem !important; }

/* Section Title */
.section-title {
  display: flex;
  align-items: center;
  gap: 0.625rem;
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-secondary);
  margin-bottom: 1rem;
  padding-top: 0.5rem;
}
.section-badge {
  font-size: 0.72rem;
  font-weight: 500;
  color: var(--primary);
  background: var(--primary-50);
  padding: 0.15rem 0.55rem;
  border-radius: 999px;
}

/* Loading */
.loading-state {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1.5rem;
  color: var(--text-muted);
  font-size: 0.875rem;
}

/* Charts */
.charts-grid {
  display: grid;
  grid-template-columns: 1.4fr 1fr 1fr;
  gap: 1rem;
  margin-bottom: 1.5rem;
}
.chart-card {
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  padding: 1rem;
}
.chart-title {
  font-size: 0.78rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: var(--text-muted);
  margin-bottom: 0.75rem;
}
.chart-wrap { height: 180px; }
.chart-wrap-sm { height: 160px; }

/* News */
.news-list { display: flex; flex-direction: column; gap: 0.625rem; margin-bottom: 1rem; }
.news-row {
  display: flex;
  align-items: center;
  gap: 0.875rem;
  padding: 0.75rem;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  transition: background 0.15s, border-color 0.15s;
}
.news-row:hover { background: var(--primary-50); border-color: var(--primary-100); }

.news-thumb {
  width: 72px;
  height: 52px;
  object-fit: cover;
  border-radius: var(--radius-sm);
  flex-shrink: 0;
}
.news-title {
  font-size: 0.85rem;
  font-weight: 500;
  color: var(--text);
  line-height: 1.4;
  margin-bottom: 0.25rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.news-meta { font-size: 0.75rem; color: var(--text-muted); }

/* Responsive */
@media (max-width: 768px) {
  .charts-grid { grid-template-columns: 1fr; }
}
</style>

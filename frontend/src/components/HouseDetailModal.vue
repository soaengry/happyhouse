<template>
  <div class="modal-backdrop" @click.self="onClose">
    <div class="modal-box">

      <!-- Header -->
      <div class="modal-header">
        <div class="modal-header-info">
          <div class="header-meta">
            <span class="year-badge">{{ house.buildYear }}년 준공</span>
            <span class="address-text">{{ house.address }}</span>
          </div>
          <h3 class="apt-title">{{ house.aptName }}</h3>
        </div>
        <button class="close-btn" @click="onClose" aria-label="닫기">
          <font-awesome-icon icon="fa-solid fa-xmark" />
        </button>
      </div>

      <!-- Body -->
      <div class="modal-body">

        <!-- Left: Deal Table -->
        <section class="panel deal-panel">
          <div class="panel-header">
            <span class="panel-title">거래 내역</span>
            <span class="deal-count">{{ dealCount }}건</span>
          </div>
          <div class="table-scroll">
            <table>
              <thead>
                <tr>
                  <th>면적(㎡)</th>
                  <th>층</th>
                  <th>거래금액</th>
                  <th>거래일</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="deal in dealList" :key="deal.no">
                  <td>{{ parseFloat(deal.area).toFixed(1) }}</td>
                  <td>{{ deal.floor }}층</td>
                  <td class="amount-cell">{{ deal.dealAmount }}만원</td>
                  <td>{{ makeDateStr(deal.dealYear, deal.dealMonth, deal.dealDay, ".") }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </section>

        <!-- Right: Map + Category -->
        <section class="panel map-panel">
          <div class="map-controls">
            <button
              class="map-reset-btn"
              @click="resetMapCenter"
            >
              <font-awesome-icon icon="fa-solid fa-house" />
              아파트
            </button>
            <div class="category-btns">
              <button
                v-for="(cat, i) in categories"
                :key="cat.id"
                :data-order="i"
                :class="['cat-btn', `cat-${cat.color}`, activeCategory === cat.id ? 'active' : '']"
                @click="(e) => toggleCategory(cat.id, e)"
              >
                {{ cat.label }}
              </button>
            </div>
          </div>
          <div id="map" class="kakao-map"></div>
        </section>

      </div>

      <!-- Footer: Transit -->
      <div class="modal-footer">
        <div class="transit-tabs">
          <button
            :class="['transit-tab', activeTab === 'bus' ? 'active' : '']"
            @click="activeTab = 'bus'"
          >버스정류장</button>
          <button
            :class="['transit-tab', activeTab === 'subway' ? 'active' : '']"
            @click="activeTab = 'subway'"
          >지하철역</button>
        </div>

        <div class="transit-list">
          <template v-if="activeTab === 'bus'">
            <div v-if="busStopList.length" class="transit-items">
              <div v-for="stop in busStopList" :key="stop.nodeid" class="transit-item">
                <span class="transit-badge bus-badge">{{ stop.nodeno }}</span>
                <span class="transit-name">{{ stop.nodenm }}</span>
              </div>
            </div>
            <p v-else class="transit-empty">주변 버스정류장 정보가 없습니다.</p>
          </template>

          <template v-else>
            <div v-if="subwayStationList.length" class="transit-items">
              <div v-for="station in subwayStationList" :key="station.bldnNm + station.route" class="transit-item">
                <span :class="['transit-badge', getRouteClass(station.route)]">{{ station.route }}</span>
                <span class="transit-name">{{ station.bldnNm }}</span>
                <span class="transit-dist">{{ Math.round(station.distance) }}m</span>
              </div>
            </div>
            <p v-else class="transit-empty">주변 지하철역 정보가 없습니다.</p>
          </template>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { onMounted, onBeforeUnmount, reactive, ref } from "vue";
import { storeToRefs } from "pinia";
import { useHouseStore } from "@/stores/houseStore";
import { makeDateStr } from "@/utils/date";

const props = defineProps({ house: Object });
const houseStore = useHouseStore();
const emit = defineEmits(["close"]);
const { dealCount, dealList, busStopList, subwayStationList } = storeToRefs(houseStore);

let map = null;
let initCenter = null;
const state = reactive({ markers: [], infos: [] });
const activeCategory = ref("");
const activeTab = ref("bus");

// color 값은 public/assets/img/markers/marker_{color}.png 파일명과 일치해야 함
const categories = [
  { id: "SC4", label: "학교",   color: "success"   },
  { id: "PO3", label: "공공",   color: "dark"      },
  { id: "BK9", label: "은행",   color: "danger"    },
  { id: "HP8", label: "병원",   color: "warning"   },
  { id: "CE7", label: "카페",   color: "secondary" },
  { id: "CS2", label: "편의점", color: "info"      },
];

const ROUTE_COLORS = {
  "1호선": "line1", "2호선": "line2", "3호선": "line3",
  "4호선": "line4", "5호선": "line5", "6호선": "line6",
  "7호선": "line7", "8호선": "line8",
};

function getRouteClass(route) {
  return ROUTE_COLORS[route] || "line-default";
}

const initMap = () => {
  const container = document.getElementById("map");
  if (!container) return;
  const center = new window.kakao.maps.LatLng(props.house.lat, props.house.lng);
  initCenter = center;

  map = new window.kakao.maps.Map(container, { center, level: 4 });
  map.setZoomable(false);
  map.addControl(new window.kakao.maps.MapTypeControl(), window.kakao.maps.ControlPosition.TOPRIGHT);
  map.addControl(new window.kakao.maps.ZoomControl(), window.kakao.maps.ControlPosition.RIGHT);

  new window.kakao.maps.Marker({
    map,
    position: center,
    image: new window.kakao.maps.MarkerImage(
      "/assets/img/markers/marker_primary.png",
      new window.kakao.maps.Size(24, 35),
    ),
  });
};

const loadScript = () => {
  const script = document.createElement("script");
  script.onload = () => window.kakao.maps.load(initMap);
  script.src = `//dapi.kakao.com/v2/maps/sdk.js?autoload=false&appkey=${process.env.VUE_APP_KAKAO_KEY}&libraries=services`;
  document.head.appendChild(script);
};

// 병렬 API 호출로 초기 로딩 최적화
onMounted(async () => {
  await Promise.all([
    houseStore.getDealList(props.house.aptCode),
    houseStore.getBusStopList(props.house.lat, props.house.lng),
    houseStore.getSubwayStationList(props.house.lat, props.house.lng),
  ]);
  window.kakao?.maps ? initMap() : loadScript();
});

onBeforeUnmount(() => {
  clearCategoryMarkers();
  map = null;
  document.querySelector('script[src*="dapi.kakao.com"]')?.remove();
});

function onClose() { emit("close"); }

function resetMapCenter(event) {
  map?.setCenter(initCenter);
  event.target.blur();
}

function toggleCategory(id, event) {
  if (activeCategory.value === id) {
    activeCategory.value = "";
    clearCategoryMarkers();
  } else {
    activeCategory.value = id;
    showCategoryPlaces(id);
  }
  event.target.blur();
}

function showCategoryPlaces(categoryCode) {
  if (!map || !window.kakao?.maps?.services) return;
  const cat = categories.find((c) => c.id === categoryCode);
  const ps = new window.kakao.maps.services.Places(map);
  const markerImage = new window.kakao.maps.MarkerImage(
    `/assets/img/markers/marker_${cat.color}.png`,
    new window.kakao.maps.Size(24, 35),
  );

  ps.categorySearch(
    categoryCode,
    (data, status) => {
      if (status !== window.kakao.maps.services.Status.OK) return;
      clearCategoryMarkers();
      data.forEach((place) => {
        const pos = new window.kakao.maps.LatLng(place.y, place.x);
        const marker = new window.kakao.maps.Marker({ map, position: pos, image: markerImage });
        const overlay = new window.kakao.maps.CustomOverlay({
          position: pos,
          content: `<span class="badge rounded-pill bg-${cat.color}">${place.place_name}</span>`,
          yAnchor: 3, zIndex: 3,
        });
        window.kakao.maps.event.addListener(marker, "mouseover", () => overlay.setMap(map));
        window.kakao.maps.event.addListener(marker, "mouseout",  () => overlay.setMap(null));
        state.markers.push(marker);
        state.infos.push(overlay);
      });
    },
    { location: map.getCenter(), radius: 1000 },
  );
}

function clearCategoryMarkers() {
  state.markers.forEach((m) => m.setMap(null));
  state.infos.forEach((i) => i.setMap(null));
  state.markers = [];
  state.infos = [];
}
</script>

<style scoped>
/* Backdrop */
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.55);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
  padding: 1rem;
}

/* Modal Box */
.modal-box {
  background: var(--bg-card);
  border-radius: var(--radius-2xl);
  width: 100%;
  max-width: 920px;
  min-height: 80vh;
  max-height: 92vh;
  display: flex;
  flex-direction: column;
  box-shadow: var(--shadow-lg);
  overflow: hidden;
}

/* Header */
.modal-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 1.25rem 1.5rem 1.125rem;
  background: var(--sidebar-bg);
  flex-shrink: 0;
}
.modal-header-info { min-width: 0; }
.header-meta {
  display: flex;
  align-items: center;
  gap: 0.625rem;
  margin-bottom: 0.4rem;
}
.year-badge {
  background: rgba(37,99,235,0.3);
  color: #93C5FD;
  font-size: 0.72rem;
  font-weight: 600;
  padding: 0.15rem 0.55rem;
  border-radius: 999px;
}
.address-text {
  font-size: 0.8rem;
  color: #94A3B8;
}
.apt-title {
  font-size: 1.3rem;
  font-weight: 700;
  color: #F8FAFC;
  letter-spacing: -0.02em;
}

.close-btn {
  background: rgba(255,255,255,0.08);
  border: none;
  color: #94A3B8;
  width: 2rem;
  height: 2rem;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9rem;
  flex-shrink: 0;
  margin-left: 1rem;
  transition: background 0.15s, color 0.15s;
}
.close-btn:hover { background: rgba(255,255,255,0.15); color: #F8FAFC; }

/* Body */
.modal-body {
  display: flex;
  height: 450px;
  flex-shrink: 0;
  overflow: hidden;
}

/* Panels */
.panel {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.875rem 1rem 0.625rem;
  border-bottom: 1px solid var(--border);
  flex-shrink: 0;
}
.panel-title {
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: var(--text-muted);
}
.deal-count {
  font-size: 0.8rem;
  font-weight: 700;
  color: var(--primary);
}

.deal-panel {
  width: 300px;
  flex-shrink: 0;
  border-right: 1px solid var(--border);
  display: flex;
  flex-direction: column;
}
.table-scroll {
  overflow-y: auto;
  flex: 1;
}
.table-scroll td { font-size: 0.82rem; }
.amount-cell { color: var(--primary); font-weight: 600; }

.map-panel {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}
.map-controls {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.625rem 0.875rem;
  border-bottom: 1px solid var(--border);
  flex-shrink: 0;
  flex-wrap: wrap;
}
.map-reset-btn {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  padding: 0.3rem 0.75rem;
  border: 1.5px solid var(--border);
  border-radius: 999px;
  background: var(--bg-card);
  font-size: 0.78rem;
  font-weight: 500;
  color: var(--text-secondary);
  transition: border-color 0.15s, color 0.15s;
}
.map-reset-btn:hover { border-color: var(--primary); color: var(--primary); }

.category-btns { display: flex; gap: 0.35rem; flex-wrap: wrap; }
.cat-btn {
  padding: 0.25rem 0.65rem;
  border-radius: 999px;
  border: 1.5px solid var(--border);
  background: transparent;
  font-size: 0.75rem;
  font-weight: 500;
  color: var(--text-muted);
  transition: all 0.15s;
}
.cat-btn:hover  { border-color: currentColor; }
.cat-btn.active { color: #fff; }

.cat-success.active   { background: #059669; border-color: #059669; }
.cat-dark.active      { background: #334155; border-color: #334155; }
.cat-danger.active    { background: #DC2626; border-color: #DC2626; }
.cat-warning.active   { background: #D97706; border-color: #D97706; }
.cat-secondary.active { background: #64748B; border-color: #64748B; }
.cat-info.active      { background: #0891B2; border-color: #0891B2; }

/* 지도는 map-panel의 남은 높이를 모두 채움 */
.kakao-map {
  flex: 1;
  min-height: 0;
  width: 100%;
}

/* Footer: Transit */
.modal-footer {
  border-top: 1px solid var(--border);
  flex-shrink: 0;
  height: 160px;
  width: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.transit-tabs {
  display: flex;
  width: 100%;
  border-bottom: 1px solid var(--border);
  flex-shrink: 0;
  background: var(--bg);
}
.transit-tab {
  padding: 0.55rem 1.125rem;
  background: none;
  border: none;
  font-size: 0.82rem;
  font-weight: 500;
  color: var(--text-muted);
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
  transition: color 0.15s, border-color 0.15s;
}
.transit-tab:hover  { color: var(--primary); }
.transit-tab.active { color: var(--primary); border-bottom-color: var(--primary); background: var(--bg-card); }

.transit-list {
  flex: 1;
  width: 100%;
  overflow-y: auto;
  padding: 0.5rem 1rem;
}
.transit-items {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  width: 100%;
}
.transit-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  width: 100%;
  font-size: 0.82rem;
  padding: 0.3rem 0.25rem;
  border-radius: var(--radius-sm);
  transition: background 0.1s;
}
.transit-item:hover { background: var(--bg); }
.transit-name {
  flex: 1;
  color: var(--text-secondary);
}
.transit-badge {
  display: inline-flex;
  align-items: center;
  padding: 0.15rem 0.5rem;
  border-radius: 4px;
  font-size: 0.72rem;
  font-weight: 600;
  color: #fff;
  white-space: nowrap;
}
.bus-badge   { background: #334155; }
.line1  { background: #0052A4; }
.line2  { background: #009D3E; }
.line3  { background: #EF7C1C; }
.line4  { background: #00A5DE; }
.line5  { background: #996CAC; }
.line6  { background: #CD7C2F; }
.line7  { background: #747F00; }
.line8  { background: #E6186C; }
.line-default { background: var(--text-muted); }

.transit-dist { color: var(--text-muted); font-size: 0.75rem; margin-left: auto; }
.transit-empty { font-size: 0.82rem; color: var(--text-muted); padding: 0.5rem 0; }

/* Responsive */
@media (max-width: 768px) {
  .modal-body { flex-direction: column; }
  .deal-panel  { width: 100%; border-right: none; border-bottom: 1px solid var(--border); max-height: 220px; }
  .kakao-map   { height: 240px; }
}
</style>

<template>
  <div class="modal-backdrop" @click.self="onClose">
    <div class="modal-container">
      <div class="modal-header">
        <h4>{{ house.aptName }}</h4>
        <div class="apt-info-box">
          <p>{{ house.address }}</p>
          <p>건축년도: {{ house.buildYear }}</p>
        </div>
        <button class="close-btn" @click="onClose"></button>
      </div>
      <div class="modal-body">
        <div class="top-content">
          <div class="deal-content">
            <p class="mb-3">
              총&nbsp;<span class="text-primary">{{ dealCount }}</span
              >&nbsp;건
            </p>
            <div class="scroll-container">
              <table>
                <thead>
                  <tr>
                    <th>면적(㎡)</th>
                    <th>층</th>
                    <th>거래금액</th>
                    <th>거래일시</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="deal in dealList" :key="deal.no">
                    <td>{{ parseFloat(deal.area).toFixed(1) }}</td>
                    <td>{{ deal.floor }}</td>
                    <td>{{ deal.dealAmount }}만 원</td>
                    <td>
                      {{
                        makeDateStr(
                          deal.dealYear,
                          deal.dealMonth,
                          deal.dealDay,
                          "-",
                        )
                      }}
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <!-- end of .scroll-container -->
          </div>
          <!-- end of .deal-content -->
          <div class="map-content">
            <div class="btn-box">
              <button
                class="apt-btn btn rounded-pill btn-outline-primary"
                @click="resetMapCenter"
              >
                아파트
              </button>
              <div class="category">
                <button
                  v-for="(category, index) in categories"
                  :key="category.id"
                  :id="category.id"
                  :data-order="index"
                  :class="[
                    'category-btn btn rounded-pill',
                    category.class,
                    activeCategory === category.id ? 'active' : '',
                  ]"
                  @click="(event) => toggleCategory(category.id, event)"
                >
                  {{ category.label }}
                </button>
              </div>
            </div>
            <div id="map"></div>
          </div>
        </div>
        <!-- .map-content -->
        <div class="surrounding-content">
          <div class="tabs">
            <button
              :class="['tab-btn', activeTab === 'bus' ? 'active' : '']"
              @click="activeTab = 'bus'"
            >
              버스정류장
            </button>
            <button
              :class="['tab-btn', activeTab === 'subway' ? 'active' : '']"
              @click="activeTab = 'subway'"
            >
              지하철역
            </button>
          </div>

          <div class="tab-content">
            <div class="bus-content">
              <ul v-if="activeTab === 'bus' && busStopList.length">
                <li v-for="stop in busStopList" :key="stop.nodeid">
                  <span class="nodeno">{{ stop.nodeno }}</span>
                  <span class="nodenm">{{ stop.nodenm }}</span>
                </li>
              </ul>
            </div>
            <!-- end of .bus-content -->
            <div
              class="subway-content"
              v-if="activeTab === 'subway' && subwayStationList.length"
            >
              <ul>
                <li
                  v-for="station in subwayStationList"
                  :key="station.stationId"
                >
                  <span :class="['route', getRouteClass(station.route)]">{{
                    station.route
                  }}</span>
                  <span class="bldnNm">{{ station.bldnNm }}</span>
                  <span class="distance"
                    >{{ Math.round(station.distance) }}m</span
                  >
                </li>
              </ul>
            </div>
            <!-- end of .subway-content -->
          </div>
          <!-- end of .tab-content -->
        </div>
        <!-- end of .surrounding-content -->
      </div>
      <!-- end of .modal-body -->
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { storeToRefs } from "pinia";
import { useHouseStore } from "@/stores/houseStore";
import { makeDateStr } from "@/utils/date";
import { Chart, registerables } from "chart.js";

Chart.register(...registerables);

const props = defineProps({
  house: Object,
});

const houseStore = useHouseStore();
const emit = defineEmits(["close"]);
const { dealCount, dealList, busStopList, subwayStationList } =
  storeToRefs(houseStore);

const state = reactive({
  map: null,
  initCenter: null,
  markers: [],
  infos: [],
  results: [],
  currCategory: "",
  order: "",
  places: null,
});

const activeCategory = ref("");
const activeTab = ref("bus");

const categories = [
  { id: "SC4", label: "학교", class: "btn-outline-success" },
  { id: "PO3", label: "공공기관", class: "btn-outline-dark" },
  { id: "BK9", label: "은행", class: "btn-outline-danger" },
  { id: "HP8", label: "병원", class: "btn-outline-warning" },
  { id: "CE7", label: "카페", class: "btn-outline-secondary" },
  { id: "CS2", label: "편의점", class: "btn-outline-info" },
];

const initMap = () => {
  const container = document.getElementById("map"); // 지도를 표시할 div
  const center = new window.kakao.maps.LatLng(props.house.lat, props.house.lng);
  state.initCenter = center;

  const options = {
    center,
    level: 4,
  };

  state.map = new window.kakao.maps.Map(container, options);
  state.map.setZoomable(false);

  // 일반 지도와 스카이뷰로 지도 타입을 전환할 수 있는 지도타입 컨트롤을 생성합니다
  const mapTypeControl = new window.kakao.maps.MapTypeControl();

  // 지도에 컨트롤을 추가해야 지도위에 표시됩니다
  state.map.addControl(
    mapTypeControl,
    window.kakao.maps.ControlPosition.TOPRIGHT, // 컨트롤이 표시될 위치
  );

  // 지도 확대 축소를 제어할 수 있는  줌 컨트롤을 생성
  const zoomControl = new window.kakao.maps.ZoomControl();
  state.map.addControl(zoomControl, window.kakao.maps.ControlPosition.RIGHT);
  const marker = new window.kakao.maps.Marker({
    position: new window.kakao.maps.LatLng(props.house.lat, props.house.lng),
    image: new window.kakao.maps.MarkerImage(
      "/assets/img/markers/marker_primary.png",
      new window.kakao.maps.Size(24, 35),
    ),
  });
  marker.setMap(state.map);
};

const loadScript = () => {
  const appKey = process.env.VUE_APP_KAKAO_KEY;
  const script = document.createElement("script");

  script.onload = () => window.kakao.maps.load(initMap);
  script.src = `//dapi.kakao.com/v2/maps/sdk.js?autoload=false&appkey=${appKey}&libraries=services`;
  script.addEventListener("load", () => window.kakao.maps.load(initMap));

  document.head.appendChild(script);
};

onMounted(async () => {
  await houseStore.getDealList(props.house.aptCode);
  await houseStore.getBusStopList(props.house.lat, props.house.lng);
  await houseStore.getSubwayStationList(props.house.lat, props.house.lng);
  if (window.kakao && window.kakao.maps) {
    initMap();
  } else {
    loadScript();
  }
});

function onClose() {
  emit("close");
}

function toggleCategory(id, event) {
  if (activeCategory.value === id) {
    activeCategory.value = "";
    clearCategoryMarkers(); // 마커 제거
  } else {
    activeCategory.value = id;
    showCategoryPlaces(id); // 마커 표시
  }
  event.target.blur();
}

function getCategoryColor(categoryClass) {
  return categoryClass.replace("btn-outline-", "");
}

// 아파트를 중심으로 지도 리셋
function resetMapCenter(event) {
  if (state.map && state.initCenter) {
    state.map.setCenter(state.initCenter);
  }
  event.target.blur(); // 포커스 해제
}

// 카카오맵 장소 검색 및 마커 표시
function showCategoryPlaces(categoryCode) {
  if (!state.map || !window.kakao.maps.services) return;

  const ps = new window.kakao.maps.services.Places(state.map);
  const center = state.map.getCenter();

  const category = categories.find((c) => c.id === categoryCode);
  const color = getCategoryColor(category.class);
  const markerImage = new window.kakao.maps.MarkerImage(
    `/assets/img/markers/marker_${color}.png`,
    new window.kakao.maps.Size(24, 35),
  );

  ps.categorySearch(
    categoryCode,
    (data, status) => {
      if (status !== window.kakao.maps.services.Status.OK) return;

      clearCategoryMarkers();

      data.forEach((place) => {
        const position = new window.kakao.maps.LatLng(place.y, place.x);

        const marker = new window.kakao.maps.Marker({
          map: state.map,
          position,
          image: markerImage,
        });
        state.markers.push(marker);

        const overlay = new window.kakao.maps.CustomOverlay({
          position,
          content:
            `<span class="badge rounded-pill bg-` +
            color +
            `">` +
            place.place_name +
            `</span>`,
          yAnchor: 3,
          zIndex: 3,
        });

        window.kakao.maps.event.addListener(marker, "mouseover", () => {
          overlay.setMap(state.map);
        });
        window.kakao.maps.event.addListener(marker, "mouseout", () => {
          overlay.setMap(null);
        });
        state.infos.push(overlay);
      });
    },
    {
      location: center,
      radius: 1000,
    },
  );
}

// 마커 제거 함수
function clearCategoryMarkers() {
  state.markers.forEach((marker) => marker.setMap(null));
  state.infos.forEach((info) => info.setMap(null));
  state.markers = [];
  state.infos = [];
}

function getRouteClass(route) {
  switch (route) {
    case "1호선":
      return "line1";
    case "2호선":
      return "line2";
    case "3호선":
      return "line3";
    case "4호선":
      return "line4";
    case "5호선":
      return "line5";
    case "6호선":
      return "line6";
    case "7호선":
      return "line7";
    case "8호선":
      return "line8";
    default:
      return "";
  }
}
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.modal-container {
  background: white;
  border-radius: 0.5rem;
  width: 80%;
  min-height: 60vh;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.modal-header {
  display: flex;
  flex-direction: column;
  position: relative;
  background-color: var(--primary);
  padding: 1rem;
  color: var(--light);
  font-size: 0.8rem;
  flex-shrink: 0;
}

.modal-header h4 {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1rem;
}

.apt-info-box {
  width: 100%;
  display: flex;
  justify-content: space-between;
}

.close-btn {
  position: absolute;
  top: 1rem;
  right: 1rem;
  height: 1.5rem;
  width: 1.5rem;
  background-color: transparent;
  border: none;
}

.close-btn::before,
.close-btn::after {
  position: absolute;
  top: 0;
  content: "";
  height: 1.5rem;
  width: 2px;
  background-color: var(--light);
}

.close-btn::before {
  transform: rotate(45deg);
}

.close-btn::after {
  transform: rotate(-45deg);
}

.modal-body {
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.top-content {
  display: flex;
  grid-area: 1rem;
  flex: 1;
  margin-bottom: 1rem;
}

.deal-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  margin-right: 1rem;
}

.scroll-container {
  max-height: 400px;
  overflow-y: auto;
  scrollbar-width: thin;
}

.scroll-container::-webkit-scrollbar {
  width: 6px;
}

.scroll-container::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.1);
  border-radius: 3px;
}

.scroll-container::-webkit-scrollbar-button {
  display: none;
}

.map-content {
  flex: 1;
  min-height: 400px;
}

.btn-box {
  margin-bottom: 0.4rem;
  display: flex;
  justify-content: space-between;
}

.btn {
  font-size: small;
  padding: 0.2rem 0.5rem;
}

.category-btn {
  margin-left: 0.2rem;
}

.category-btn.active {
  background-color: currentColor;
  color: white;
  border-color: currentColor;
}

#map {
  width: 100%;
  height: 400px;
}

.tab-btn {
  background-color: transparent;
  color: #4e73df;
  font-weight: 500;
  font-size: 0.9rem;
  border: none;
  padding: 0.6rem 1rem;
  border-radius: 4px 4px 0 0;
}

.tab-btn.active {
  background-color: aliceblue;
  border-bottom: 2px solid #4e73df;
}

.tab-content {
  padding: 1rem;
}

.bus-content li,
.subway-content li {
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}

.bus-content .nodeno,
.subway-content .route {
  margin-right: 0.5rem;
  color: var(--light);
  border-radius: 0.5rem;
  padding: 0rem 0.5rem;
  font-size: 0.8rem;
}

.bus-content .nodeno {
  background-color: var(--dark);
}
.line1 {
  background-color: #0052a4;
} /* 1호선 파랑 */
.line2 {
  background-color: #009d3e;
} /* 2호선 초록 */
.line3 {
  background-color: #ef7c1c;
} /* 3호선 주황 */
.line4 {
  background-color: #00a5de;
} /* 4호선 하늘 */
.line5 {
  background-color: #996cac;
} /* 5호선 보라 */
.line6 {
  background-color: #cd7c2f;
} /* 6호선 갈색 */
.line7 {
  background-color: #747f00;
} /* 7호선 올리브 */
.line8 {
  background-color: #e6186c;
} /* 8호선 분홍 */

.bldnNm {
  margin-right: 0.5rem;
}

.distance {
  font-size: small;
}

@media (max-width: 992px) {
  .top-content {
    flex-direction: column;
  }

  .deal-content {
    margin-right: 0;
    margin-bottom: 2rem;
  }
}
</style>

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
        <div class="deal-content">
          <p class="mb-2">
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
                  <th>관심거래</th>
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
                  <td>ㅇ</td>
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
                id="SC4"
                data-order="0"
                class="category-btn btn rounded-pill btn-outline-success"
                @click="btnToggle"
                @mouseup="setCategoryOrder"
              >
                학교
              </button>
              <button
                id="PO3"
                data-order="1"
                class="category-btn btn rounded-pill btn-outline-dark"
                @click="btnToggle"
                @mouseup="setCategoryOrder"
              >
                공공기관
              </button>
              <button
                id="BK9"
                data-order="2"
                class="category-btn btn rounded-pill btn-outline-danger"
                @click="btnToggle"
                @mouseup="setCategoryOrder"
              >
                은행
              </button>
              <button
                id="HP8"
                data-order="3"
                class="category-btn btn rounded-pill btn-outline-warning"
                @click="btnToggle"
                @mouseup="setCategoryOrder"
              >
                병원
              </button>
              <button
                id="CE7"
                data-order="4"
                class="category-btn btn rounded-pill btn-outline-secondary"
                @click="btnToggle"
                @mouseup="setCategoryOrder"
              >
                카페
              </button>
              <button
                id="CS2"
                data-order="5"
                class="category-btn btn rounded-pill btn-outline-info"
                @click="btnToggle"
                @mouseup="setCategoryOrder"
              >
                편의점
              </button>
            </div>
          </div>
          <div id="map"></div>
        </div>
      </div>
      <!-- end of .modal-body -->
    </div>
  </div>
</template>

<script setup>
import { useHouseStore } from "@/stores/houseStore";
import { makeDateStr } from "@/utils/date";
import { storeToRefs } from "pinia";
import { onMounted, reactive } from "vue";

const props = defineProps({
  house: Object,
});

const houseStore = useHouseStore();
const emit = defineEmits(["close"]);
const { dealCount, dealList } = storeToRefs(houseStore);

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
      "assets/img/markers/marker_primary.png",
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

  if (window.kakao && window.kakao.maps) {
    initMap();
  } else {
    loadScript();
  }
});

function onClose() {
  emit("close");
}

function resetMapCenter() {
  if (state.map && state.initCenter) {
    state.map.setCenter(state.initCenter);
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
  height: 80vh;
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
  grid-area: 1rem;
  flex: 1;
  overflow-y: auto;
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

#map {
  width: 100%;
  height: 400px;
}

.deal-content table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.9rem;
}

.deal-content thead {
  background-color: #f8f9fc;
  position: sticky;
  top: 0;
  z-index: 1;
}

.deal-content th {
  padding: 0.75rem;
  text-align: center;
  font-weight: 600;
  color: #4e73df;
  border-bottom: 1px solid #dee2e6;
}

.deal-content td {
  padding: 0.75rem;
  text-align: center;
  border-bottom: 1px solid #eee;
}

.deal-content tbody tr:nth-child(even) {
  background-color: #f9f9f9;
}

.deal-content tbody tr:hover {
  background-color: #e9f2ff;
}

.deal-content td:last-child {
  text-align: center;
}

@media (max-width: 992px) {
  .modal-body {
    flex-direction: column;
  }

  .deal-content {
    margin-right: 0;
    margin-bottom: 2rem;
  }
}
</style>

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
        <div class="map-content"></div>
      </div>
      <!-- end of .modal-body -->
    </div>
  </div>
</template>

<script setup>
import { useHouseStore } from "@/stores/houseStore";
import { makeDateStr } from "@/utils/date";
import { storeToRefs } from "pinia";
import { onMounted } from "vue";

const props = defineProps({
  house: Object,
});

const houseStore = useHouseStore();
const emit = defineEmits(["close"]);
const { dealCount, dealList } = storeToRefs(houseStore);

onMounted(async () => {
  await houseStore.getDealList(props.house.aptCode);
});

function onClose() {
  emit("close");
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
  max-height: 80vh;
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
  overflow: hidden;
}

.deal-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
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
  min-width: 0;
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
}
</style>

<template>
  <div class="modal-backdrop" @click.self="onClose">
    <div class="modal-content">
      <h4>{{ house.aptName }}</h4>
      <p><strong>주소:</strong> {{ house.address }}</p>
      <p><strong>건축연도:</strong> {{ house.buildYear }}</p>
      <p><strong>최근 거래:</strong> {{ house.dealAmount }}만 원</p>
      <p>
        <strong>거래일:</strong>
        {{ makeDateStr(house.dealYear, house.dealMonth, house.dealDay, "-") }}
      </p>
      <p>{{ count }}</p>
      <div
        class="d-flex justify-content-between"
        v-for="deal in dealList"
        :key="deal.no"
      >
        <p>{{ deal.area }}</p>
        <p>{{ deal.dealAmount }}</p>
        <p>
          {{ makeDateStr(deal.dealYear, deal.dealMonth, deal.dealDay, "-") }}
        </p>
        <p>{{ deal.floor }}</p>
      </div>
      <button class="btn btn-secondary mt-3" @click="onClose">닫기</button>
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
const { count, dealList } = storeToRefs(houseStore);

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
.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  width: 400px;
  max-width: 90%;
}
</style>

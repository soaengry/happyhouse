<script setup>
import houseService from "@/services/houseService";
import { ref, onMounted } from "vue";

const houseList = ref([]);

onMounted(async () => {
  const res = await houseService.getAllDealList();
  houseList.value = res.houseList; // 배열만 꺼내서 대입
});
</script>

<template>
  <div>
    <h2 class="home">home</h2>
    <ul>
      <li v-for="house in houseList" :key="house.no">
        <div class="aptName">{{ house.aptName }}</div>
        <div class="address">{{ house.address }}</div>
      </li>
    </ul>
  </div>
</template>

<style></style>

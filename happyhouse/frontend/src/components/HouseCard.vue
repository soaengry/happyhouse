<template>
  <div class="card mb-3">
    <div class="card-header">
      <p class="apt-text fw-bold mb-2">{{ house.aptName }}</p>
      <p class="small">{{ house.address }}</p>
    </div>
    <div class="card-body">
      <ul class="house-list">
        <li><span>건축연도</span> {{ house.buildYear || "-" }} 년</li>
        <li><span>최근거래금액</span>{{ house.dealAmount }}만 원</li>
        <li>
          <span>최근거래일</span> {{ house.dealYear }}-{{ house.dealMonth }}-{{
            house.dealDay
          }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
defineProps({
  house: {
    type: Object,
    required: true,
  },
});
</script>

<style scoped>
.card {
  border: 1px solid #dedede;
  cursor: pointer;
}

.card-header {
  padding: 1rem;
}

.apt-text {
  font-size: 1.1rem;
}

.card-body {
  font-size: 0.9rem;
  padding: 0.5rem 1rem;
}

.house-list li {
  line-height: 1.2rem;
}

.house-list span {
  color: #444;
  font-weight: 500;
  display: inline-block;
  margin-right: 0.5rem;
}
</style>

<template>
  <div class="content-header">
    <h2>주요 뉴스</h2>
  </div>
  <section class="content-body">
    <div class="news-container">
      <div v-for="news in newsList" :key="news.url" class="card news-card">
        <img
          v-if="news.img"
          :src="news.img"
          class="card-img-top"
          alt="뉴스 이미지"
        />
        <div class="card-body">
          <h5 class="card-title">
            <a :href="news.url" target="_blank">{{ news.title }}</a>
          </h5>
          <p class="card-text">{{ news.content }}</p>
          <div class="card-footer">
            <small class="text-muted"
              >{{ news.publish }} | {{ news.date }}</small
            >
          </div>
        </div>
      </div>
    </div>
    <div v-if="isLoading" class="loading">
      <span>불러오는 중...</span>
    </div>
    <button class="mt-3 btn btn-outline-primary">
      <a href="https://land.naver.com/news/region.naver" target="_blank"
        >더 많은 뉴스 보기</a
      >
    </button>
  </section>
</template>

<script setup>
import { useHouseStore } from "@/stores/houseStore";
import { storeToRefs } from "pinia";
import { onMounted, ref } from "vue";

const houseStore = useHouseStore();
const { newsList } = storeToRefs(houseStore);

const isLoading = ref(false);

async function loadNews() {
  isLoading.value = true;
  await houseStore.getNews(0);
  isLoading.value = false;
}

onMounted(async () => {
  await loadNews();
});
</script>

<style scoped>
.content-body {
  display: flex;
  flex-direction: column;
}

.news-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 1rem;
}

.news-card {
  width: 48%;
}

.card-img-top {
  height: 180px;
  object-fit: cover;
}

.loading {
  text-align: center;
  padding: 1rem;
  color: #666;
}

.card-footer {
  margin-top: 1rem;
  background-color: transparent;
  text-align: end;
  padding: 1rem 0 0 0;
}

.content-body .btn-outline-primary {
  align-self: flex-end;
}

.content-body .btn-outline-primary:hover {
  background-color: #e9f2ff;
}

@media (max-width: 768px) {
  .news-card {
    width: 100%;
  }
}
</style>

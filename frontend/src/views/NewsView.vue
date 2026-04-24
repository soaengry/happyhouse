<template>
  <div>
    <div class="content-header">
      <h2>주요 뉴스</h2>
      <p>지역 부동산 최신 뉴스를 확인하세요.</p>
    </div>

    <div class="content-body">
      <div v-if="isLoading" class="loading-state">
        <font-awesome-icon icon="fa-solid fa-spinner" spin />
        <span>불러오는 중...</span>
      </div>

      <div v-else-if="newsList.length" class="news-grid">
        <a
          v-for="news in newsList"
          :key="news.url"
          :href="news.url"
          target="_blank"
          rel="noopener noreferrer"
          class="news-card"
        >
          <div class="news-img-wrap">
            <img
              v-if="news.img"
              :src="news.img"
              :alt="news.title"
              class="news-img"
            />
            <div v-else class="news-img-placeholder">
              <font-awesome-icon icon="fa-solid fa-newspaper" />
            </div>
          </div>
          <div class="news-content">
            <h3 class="news-title">{{ news.title }}</h3>
            <p class="news-desc">{{ news.content }}</p>
            <div class="news-meta">
              <span>{{ news.publish }}</span>
              <span>{{ news.date }}</span>
            </div>
          </div>
        </a>
      </div>

      <div v-else class="empty-state">
        <font-awesome-icon icon="fa-solid fa-newspaper" class="empty-icon" />
        <p>표시할 뉴스가 없습니다.</p>
      </div>

      <div class="more-link-wrap">
        <a
          href="https://land.naver.com/news/region.naver"
          target="_blank"
          rel="noopener noreferrer"
          class="btn btn-outline-primary more-link"
        >
          더 많은 뉴스 보기
          <font-awesome-icon icon="fa-solid fa-arrow-right" />
        </a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useHouseStore } from "@/stores/houseStore";
import { storeToRefs } from "pinia";

const houseStore = useHouseStore();
const { newsList } = storeToRefs(houseStore);
const isLoading = ref(false);

onMounted(async () => {
  isLoading.value = true;
  await houseStore.getNews(0);
  isLoading.value = false;
});
</script>

<style scoped>
.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 3rem;
  color: var(--text-muted);
  font-size: 0.875rem;
}

.news-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.news-card {
  display: flex;
  flex-direction: column;
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  overflow: hidden;
  transition: box-shadow 0.2s, transform 0.2s, border-color 0.2s;
  text-decoration: none;
}
.news-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
  border-color: transparent;
}

.news-img-wrap { height: 160px; overflow: hidden; background: var(--bg); }
.news-img { width: 100%; height: 100%; object-fit: cover; }
.news-img-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  font-size: 2rem;
  opacity: 0.3;
}

.news-content { padding: 0.875rem; display: flex; flex-direction: column; flex: 1; }
.news-title {
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--text);
  line-height: 1.4;
  margin-bottom: 0.5rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.news-desc {
  font-size: 0.8rem;
  color: var(--text-muted);
  line-height: 1.5;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 0.75rem;
}
.news-meta {
  display: flex;
  justify-content: space-between;
  font-size: 0.75rem;
  color: var(--text-muted);
  padding-top: 0.625rem;
  border-top: 1px solid var(--border);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 3rem;
  color: var(--text-muted);
  gap: 0.75rem;
}
.empty-icon { font-size: 2.5rem; opacity: 0.25; }

.more-link-wrap {
  display: flex;
  justify-content: flex-end;
  padding-top: 0.5rem;
}
.more-link {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  font-size: 0.875rem;
}
</style>

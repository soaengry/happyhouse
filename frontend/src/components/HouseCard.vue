<template>
  <article class="house-card">
    <div class="card-header">
      <div class="card-title-area">
        <h3 class="apt-name">{{ house.aptName }}</h3>
        <p class="apt-address">{{ house.address }}</p>
      </div>
      <button
        v-if="isLoggedIn"
        class="bookmark-btn"
        :class="{ active: isBookmarked }"
        @click.stop="toggleBookmark"
        :title="isBookmarked ? '북마크 해제' : '북마크 추가'"
      >
        <font-awesome-icon :icon="isBookmarked ? 'fa-solid fa-star' : 'fa-regular fa-star'" />
      </button>
    </div>

    <div class="card-body">
      <div class="stat-item">
        <span class="stat-label">건축연도</span>
        <span class="stat-value">{{ house.buildYear || "-" }}년</span>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <span class="stat-label">최근 거래</span>
        <span class="stat-value amount">{{ house.dealAmount }}만원</span>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <span class="stat-label">거래일</span>
        <span class="stat-value">{{ formatDate(house.dealYear, house.dealMonth, house.dealDay) }}</span>
      </div>
    </div>
  </article>
</template>

<script setup>
import { computed } from "vue";
import { useHouseStore } from "@/stores/houseStore";
import { useUserStore } from "@/stores/userStore";
import { useToast } from "vue-toast-notification";
import { TOAST_DURATION } from "@/utils/constants";

const props = defineProps({
  house: { type: Object, required: true, default: () => ({}) },
});

const houseStore = useHouseStore();
const userStore = useUserStore();
const toast = useToast();

const isBookmarked = computed(() =>
  houseStore.bookmarks.includes(props.house.aptCode),
);
const isLoggedIn = computed(() => !!userStore.user);

function formatDate(y, m, d) {
  if (!y || !m || !d) return "-";
  return `${y}.${String(m).padStart(2, "0")}.${String(d).padStart(2, "0")}`;
}

async function toggleBookmark() {
  const wasBookmarked = isBookmarked.value;
  await houseStore.toggleBookmark(props.house.aptCode);
  toast.open({
    message: wasBookmarked ? "북마크가 해제되었습니다." : "북마크에 추가되었습니다.",
    type: "success",
    duration: TOAST_DURATION,
  });
}
</script>

<style scoped>
.house-card {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s ease, transform 0.2s ease, border-color 0.2s ease;
}
.house-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
  border-color: transparent;
}

/* Header */
.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 1rem 1rem 0.75rem;
  gap: 0.5rem;
}
.card-title-area { min-width: 0; }

.apt-name {
  font-size: 0.95rem;
  font-weight: 700;
  color: var(--text);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.4;
}
.apt-address {
  font-size: 0.78rem;
  color: var(--text-muted);
  margin-top: 0.2rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.bookmark-btn {
  background: none;
  border: none;
  padding: 0.25rem;
  color: var(--text-muted);
  font-size: 1rem;
  transition: color 0.15s, transform 0.15s;
  flex-shrink: 0;
  line-height: 1;
}
.bookmark-btn:hover { color: #F59E0B; transform: scale(1.15); }
.bookmark-btn.active { color: #F59E0B; }

/* Body */
.card-body {
  display: flex;
  align-items: stretch;
  border-top: 1px solid var(--border);
}
.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0.75rem 0.5rem;
  gap: 0.25rem;
}
.stat-divider {
  width: 1px;
  background: var(--border);
  margin: 0.5rem 0;
}
.stat-label {
  font-size: 0.7rem;
  color: var(--text-muted);
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.03em;
}
.stat-value {
  font-size: 0.82rem;
  font-weight: 600;
  color: var(--text-secondary);
}
.stat-value.amount { color: var(--primary); }
</style>

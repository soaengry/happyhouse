<template>
  <div class="card mb-3">
    <div class="card-header d-flex justify-content-between align-items-center">
      <div>
        <p class="apt-text fw-bold mb-2">{{ house.aptName }}</p>
        <p class="small">{{ house.address }}</p>
      </div>
      <font-awesome-icon
        v-if="isLoggedIn"
        :icon="isBookmarked ? 'fa-solid fa-star' : 'fa-regular fa-star'"
        class="bookmark-icon"
        @click.stop="toggleBookmark"
      />
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
import { ref, computed, watch } from "vue";

import { useHouseStore } from "@/stores/houseStore";
import { useUserStore } from "@/stores/userStore";
import { useToast } from "vue-toast-notification";

const props = defineProps({
  house: { type: Object, required: true, default: () => ({}) },
});
const houseStore = useHouseStore();
const userStore = useUserStore();
const toast = useToast();

const bookmarked = ref(props.house.bookmarked);

// props가 바뀔 때 로컬 상태도 동기화
watch(
  () => props.house.bookmarked,
  (newVal) => {
    bookmarked.value = newVal;
  },
);

const isBookmarked = computed(() => {
  if (
    houseStore.bookmarks.length === 0 &&
    props.house.bookmarked !== undefined
  ) {
    return props.house.bookmarked;
  }
  return houseStore.bookmarks.includes(props.house.aptCode);
});
const isLoggedIn = computed(() => !!userStore.user);

async function toggleBookmark() {
  const aptCode = props.house.aptCode;

  await houseStore.toggleBookmark(aptCode);
  bookmarked.value = !bookmarked.value;

  toast.open({
    message: bookmarked.value ? "북마크 해제됨" : "북마크 설정됨",
    type: "success",
    duration: 1500,
  });
}
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
.bookmark-icon {
  cursor: pointer;
  color: gold;
}
</style>

<template>
  <div>
    <div class="content-header">
      <h2>커뮤니티</h2>
      <p>부동산 정보를 이웃과 나눠보세요.</p>
    </div>

    <div class="content-body">
      <!-- 검색 -->
      <div class="toolbar">
        <div class="search-group">
          <select v-model="searchType" class="search-select">
            <option value="all">전체</option>
            <option value="title">제목</option>
            <option value="content">내용</option>
          </select>
          <input
            v-model="keyword"
            type="text"
            placeholder="검색어 입력"
            class="search-input"
            @keydown.enter="onSearch"
          />
          <button class="btn btn-primary" @click="onSearch">검색</button>
        </div>
        <router-link
          v-if="isLoggedIn"
          to="/community/write"
          class="btn btn-primary write-btn"
        >
          글쓰기
        </router-link>
      </div>

      <!-- 목록 -->
      <div v-if="isLoading" class="loading-state">
        <font-awesome-icon icon="fa-solid fa-spinner" spin />
        <span>불러오는 중...</span>
      </div>

      <table v-else class="board-table">
        <thead>
          <tr>
            <th class="col-no">번호</th>
            <th class="col-title">제목</th>
            <th class="col-author">작성자</th>
            <th class="col-date">작성일</th>
            <th class="col-views">조회</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="(board, idx) in boardList"
            :key="board.id"
            class="board-row"
            @click="goDetail(board.id)"
          >
            <td class="col-no text-muted">{{ totalCount - offset - idx }}</td>
            <td class="col-title">
              <span class="board-title">{{ board.title }}</span>
              <span v-if="board.replyList?.length" class="reply-count">
                [{{ board.replyList.length }}]
              </span>
            </td>
            <td class="col-author">{{ board.nickname }}</td>
            <td class="col-date text-muted">{{ formatDateTime(board.createdDate) }}</td>
            <td class="col-views text-muted">{{ board.readCount }}</td>
          </tr>
          <tr v-if="!boardList.length">
            <td colspan="5" class="empty-row">게시글이 없습니다.</td>
          </tr>
        </tbody>
      </table>

      <!-- 페이지네이션 -->
      <div class="pagination">
        <button class="page-btn" :disabled="page <= 1" @click="changePage(page - 1)">‹</button>
        <button
          v-for="p in pageNumbers"
          :key="p"
          class="page-btn"
          :class="{ active: p === page }"
          @click="changePage(p)"
        >{{ p }}</button>
        <button class="page-btn" :disabled="page >= totalPages" @click="changePage(page + 1)">›</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { formatDateTime } from "@/utils/date";
import { useRouter } from "vue-router";
import { storeToRefs } from "pinia";
import { useBoardStore } from "@/stores/boardStore";
import { useUserStore } from "@/stores/userStore";

const router     = useRouter();
const boardStore = useBoardStore();
const userStore  = useUserStore();

const { boardList, totalCount, isLoading } = storeToRefs(boardStore);
const isLoggedIn = computed(() => !!userStore.user);

const PAGE_SIZE  = 10;
const page       = ref(1);
const searchType = ref("all");
const keyword    = ref("");

const offset      = computed(() => (page.value - 1) * PAGE_SIZE);
const totalPages  = computed(() => Math.ceil(totalCount.value / PAGE_SIZE));
const pageNumbers = computed(() => {
  const start = Math.max(1, page.value - 2);
  const end   = Math.min(totalPages.value, start + 4);
  return Array.from({ length: end - start + 1 }, (_, i) => start + i);
});

function fetchList() {
  boardStore.fetchBoardList({
    limit:      PAGE_SIZE,
    offset:     offset.value,
    searchType: searchType.value,
    keyword:    keyword.value,
  });
}

function onSearch() {
  page.value = 1;
  fetchList();
}

function changePage(p) {
  page.value = p;
  fetchList();
}

function goDetail(id) {
  router.push(`/community/${id}`);
}


onMounted(fetchList);
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  gap: 0.75rem;
  flex-wrap: wrap;
}
.search-group {
  display: flex;
  gap: 0.5rem;
  flex: 1;
}
.search-select { width: 100px; flex-shrink: 0; margin-right: 0; }
.search-input  { flex: 1; margin-right: 0; }
.write-btn     { flex-shrink: 0; white-space: nowrap; }

.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 3rem;
  color: var(--text-muted);
  font-size: 0.875rem;
}

/* Table overrides */
.board-table { margin-bottom: 1.25rem; }
.board-row { cursor: pointer; }

.col-no     { width: 60px; }
.col-title  { text-align: left !important; }
.col-author { width: 100px; }
.col-date   { width: 140px; }
.col-views  { width: 60px; }

.board-title {
  font-weight: 500;
  color: var(--text);
}
.board-title:hover { color: var(--primary); }

.reply-count {
  margin-left: 0.4rem;
  font-size: 0.8rem;
  color: var(--primary);
  font-weight: 600;
}

.empty-row {
  color: var(--text-muted);
  font-size: 0.875rem;
  padding: 2rem !important;
}

/* Pagination */
.pagination {
  display: flex;
  justify-content: center;
  gap: 0.25rem;
}
.page-btn {
  min-width: 2rem;
  height: 2rem;
  padding: 0 0.5rem;
  border: 1.5px solid var(--border);
  border-radius: var(--radius-sm);
  background: var(--bg-card);
  font-size: 0.85rem;
  color: var(--text-secondary);
  transition: all 0.15s;
}
.page-btn:hover:not(:disabled) {
  border-color: var(--primary);
  color: var(--primary);
}
.page-btn.active {
  background: var(--primary);
  border-color: var(--primary);
  color: #fff;
}
.page-btn:disabled { opacity: 0.4; cursor: not-allowed; }
</style>

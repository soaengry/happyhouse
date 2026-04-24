<template>
  <div>
    <div class="content-header">
      <h2>커뮤니티</h2>
    </div>

    <div class="content-body">
      <div v-if="isLoading" class="loading-state">
        <font-awesome-icon icon="fa-solid fa-spinner" spin />
      </div>

      <template v-else-if="board">
        <!-- 게시글 헤더 -->
        <div class="post-header">
          <h3 class="post-title">{{ board.title }}</h3>
          <div class="post-meta">
            <div class="meta-left">
              <img :src="authorAvatar" class="author-avatar" alt="작성자" />
              <span class="author-name">{{ board.nickname }}</span>
              <span class="post-date">{{ formatDateTime(board.createdDate) }}</span>
            </div>
            <div class="meta-right">
              <span class="view-count">조회 {{ board.readCount }}</span>
              <template v-if="board.sameUser">
                <router-link :to="`/community/${board.id}/edit`" class="btn btn-ghost action-btn">
                  수정
                </router-link>
                <button class="btn btn-ghost action-btn danger" @click="onDelete">삭제</button>
              </template>
            </div>
          </div>
        </div>

        <!-- 본문 (CKEditor HTML) -->
        <div class="post-content ck-content" v-html="board.content"></div>

        <!-- 첨부파일 -->
        <div v-if="board.fileList?.length" class="file-section">
          <p class="file-title">첨부파일 ({{ board.fileList.length }})</p>
          <ul class="file-list">
            <li v-for="file in board.fileList" :key="file.id">
              <a :href="getFileUrl(file.fileUrl)" :download="file.fileName" class="file-link">
                <font-awesome-icon icon="fa-solid fa-xmark" class="file-icon" />
                {{ file.fileName }}
              </a>
            </li>
          </ul>
        </div>

        <!-- 댓글 -->
        <div class="reply-section">
          <p class="reply-section-title">댓글 {{ board.replyList?.length ?? 0 }}개</p>

          <!-- 댓글 입력 -->
          <div v-if="isLoggedIn" class="reply-input">
            <textarea
              v-model="replyText"
              class="reply-textarea"
              placeholder="댓글을 입력하세요."
              rows="2"
              @keydown.ctrl.enter="submitReply"
            ></textarea>
            <button class="btn btn-primary reply-submit" @click="submitReply">
              등록
            </button>
          </div>

          <!-- 댓글 목록 -->
          <div class="reply-list">
            <div
              v-for="reply in board.replyList"
              :key="reply.id"
              class="reply-item"
            >
              <div class="reply-meta">
                <span class="reply-author">{{ reply.nickname }}</span>
                <span class="reply-date text-muted">{{ formatDateTime(reply.createdDate) }}</span>
                <template v-if="reply.sameUser">
                  <button class="reply-action" @click="startEdit(reply)">수정</button>
                  <button class="reply-action danger" @click="removeReply(reply.id)">삭제</button>
                </template>
              </div>

              <!-- 수정 모드 -->
              <template v-if="editingId === reply.id">
                <textarea v-model="editText" class="reply-textarea" rows="2"></textarea>
                <div class="edit-actions">
                  <button class="btn btn-primary" style="height:2rem; font-size:0.8rem;" @click="saveEdit(reply.id)">저장</button>
                  <button class="btn btn-ghost" style="height:2rem; font-size:0.8rem;" @click="cancelEdit">취소</button>
                </div>
              </template>
              <p v-else class="reply-content">{{ reply.content }}</p>
            </div>
          </div>
        </div>

        <div class="post-footer">
          <router-link to="/community" class="btn btn-ghost">← 목록으로</router-link>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { storeToRefs } from "pinia";
import { useBoardStore } from "@/stores/boardStore";
import { useUserStore } from "@/stores/userStore";
import { useToast } from "vue-toast-notification";
import { BASE_URL } from "@/utils/constants";
import { formatDateTime } from "@/utils/date";
import boardService from "@/services/boardService";

const route      = useRoute();
const router     = useRouter();
const boardStore = useBoardStore();
const userStore  = useUserStore();
const toast      = useToast();

const { currentBoard: board, isLoading } = storeToRefs(boardStore);
const isLoggedIn = computed(() => !!userStore.user);

const replyText = ref("");
const editingId = ref(null);
const editText  = ref("");

const authorAvatar = computed(() => {
  const url = board.value?.profileImageUrl;
  if (!url) return `${BASE_URL}/api/user/image?fileName=default.png`;
  return `${BASE_URL}/api/user/image?fileName=${encodeURIComponent(url)}`;
});

onMounted(() => boardStore.fetchBoard(Number(route.params.id)));

function getFileUrl(fileUrl) {
  return boardService.getFileUrl(fileUrl);
}

async function onDelete() {
  if (!confirm("게시글을 삭제하시겠습니까?")) return;
  await boardStore.deleteBoard(board.value.id);
  toast.open({ message: "삭제되었습니다.", type: "success", duration: 1500 });
  router.push("/community");
}

async function submitReply() {
  if (!replyText.value.trim()) return;
  await boardStore.createReply(board.value.id, replyText.value);
  replyText.value = "";
}

function startEdit(reply) {
  editingId.value = reply.id;
  editText.value  = reply.content;
}

function cancelEdit() {
  editingId.value = null;
  editText.value  = "";
}

async function saveEdit(id) {
  if (!editText.value.trim()) return;
  await boardStore.updateReply(id, editText.value);
  cancelEdit();
}

async function removeReply(id) {
  if (!confirm("댓글을 삭제하시겠습니까?")) return;
  await boardStore.deleteReply(id);
}
</script>

<style scoped>
.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 3rem;
  color: var(--text-muted);
}

/* Post header */
.post-header {
  padding-bottom: 1rem;
  border-bottom: 1px solid var(--border);
  margin-bottom: 1.5rem;
}
.post-title {
  font-size: 1.3rem;
  font-weight: 700;
  color: var(--text);
  margin-bottom: 0.75rem;
  line-height: 1.4;
}
.post-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.5rem;
}
.meta-left {
  display: flex;
  align-items: center;
  gap: 0.625rem;
  font-size: 0.85rem;
}
.author-avatar {
  width: 1.75rem;
  height: 1.75rem;
  border-radius: 50%;
  object-fit: cover;
  border: 1.5px solid var(--border);
}
.author-name { font-weight: 600; color: var(--text); }
.post-date   { color: var(--text-muted); }

.meta-right {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}
.view-count { font-size: 0.82rem; color: var(--text-muted); }
.action-btn { font-size: 0.82rem; height: 1.75rem; padding: 0 0.625rem; }
.action-btn.danger { color: var(--danger); }
.action-btn.danger:hover { background: #FEF2F2; }

/* Post content */
.post-content {
  min-height: 200px;
  padding: 1rem 0;
  border-bottom: 1px solid var(--border);
  line-height: 1.75;
  color: var(--text);
  font-size: 0.95rem;
}

/* 파일 */
.file-section {
  padding: 1rem 0;
  border-bottom: 1px solid var(--border);
}
.file-title { font-size: 0.82rem; font-weight: 600; color: var(--text-muted); margin-bottom: 0.5rem; }
.file-list  { display: flex; flex-direction: column; gap: 0.3rem; }
.file-link {
  font-size: 0.85rem;
  color: var(--primary);
  display: flex;
  align-items: center;
  gap: 0.35rem;
}
.file-link:hover { text-decoration: underline; }
.file-icon { font-size: 0.7rem; }

/* 댓글 */
.reply-section { margin-top: 1.5rem; }
.reply-section-title {
  font-size: 0.9rem;
  font-weight: 700;
  color: var(--text);
  margin-bottom: 1rem;
}

.reply-input {
  display: flex;
  gap: 0.75rem;
  margin-bottom: 1.25rem;
}
.reply-textarea {
  flex: 1;
  padding: 0.625rem 0.875rem;
  border: 1.5px solid var(--border);
  border-radius: var(--radius);
  font-size: 0.875rem;
  font-family: inherit;
  resize: vertical;
  min-height: 60px;
  transition: border-color 0.15s;
}
.reply-textarea:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(37,99,235,0.1);
}
.reply-submit { align-self: flex-end; }

.reply-list { display: flex; flex-direction: column; gap: 0; }
.reply-item {
  padding: 0.875rem 0;
  border-bottom: 1px solid var(--border);
}
.reply-item:last-child { border-bottom: none; }

.reply-meta {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.4rem;
  flex-wrap: wrap;
}
.reply-author { font-size: 0.85rem; font-weight: 600; color: var(--text); }
.reply-date   { font-size: 0.78rem; }
.reply-action {
  background: none;
  border: none;
  font-size: 0.78rem;
  color: var(--text-muted);
  padding: 0 0.2rem;
  cursor: pointer;
  transition: color 0.12s;
}
.reply-action:hover { color: var(--primary); }
.reply-action.danger:hover { color: var(--danger); }
.reply-content { font-size: 0.9rem; color: var(--text-secondary); line-height: 1.6; white-space: pre-wrap; }

.edit-actions { display: flex; gap: 0.5rem; margin-top: 0.4rem; }

.post-footer { margin-top: 1.5rem; padding-top: 1rem; border-top: 1px solid var(--border); }

/* CKEditor 본문 렌더링 스타일 */
.ck-content :deep(h1), .ck-content :deep(h2), .ck-content :deep(h3) {
  font-weight: 700; margin: 1rem 0 0.5rem;
}
.ck-content :deep(p)  { margin-bottom: 0.75rem; }
.ck-content :deep(ul), .ck-content :deep(ol) { margin: 0.5rem 0 0.75rem 1.5rem; }
.ck-content :deep(li) { list-style: inherit; margin-bottom: 0.25rem; }
.ck-content :deep(blockquote) {
  border-left: 4px solid var(--primary);
  padding-left: 1rem;
  color: var(--text-muted);
  margin: 0.75rem 0;
}
.ck-content :deep(table) { margin: 0.75rem 0; }
.ck-content :deep(img)   { max-width: 100%; height: auto; border-radius: var(--radius); }
</style>

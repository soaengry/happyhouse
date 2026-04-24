<template>
  <div>
    <div class="content-header">
      <h2>게시글 수정</h2>
    </div>

    <div class="content-body">
      <div v-if="isLoading" class="loading-state">
        <font-awesome-icon icon="fa-solid fa-spinner" spin />
      </div>

      <form v-else @submit.prevent="onSubmit" class="editor-form">
        <div class="field">
          <label class="field-label">제목</label>
          <input v-model="title" type="text" required class="title-input" />
        </div>

        <div class="field">
          <label class="field-label">내용</label>
          <div class="editor-wrap">
            <div ref="editorEl"></div>
          </div>
        </div>

        <div v-if="existingFiles.length" class="field">
          <label class="field-label">기존 첨부파일</label>
          <ul class="existing-files">
            <li v-for="file in existingFiles" :key="file.id">
              <a :href="getFileUrl(file.fileUrl)" :download="file.fileName" class="file-link">
                {{ file.fileName }}
              </a>
            </li>
          </ul>
          <p class="file-note">새 파일을 첨부하면 기존 파일이 모두 교체됩니다.</p>
        </div>

        <div class="field">
          <label class="field-label">새 첨부파일 (선택)</label>
          <input type="file" multiple @change="onFileChange" class="file-input" />
          <ul v-if="newFiles.length" class="selected-files">
            <li v-for="(f, i) in newFiles" :key="i">
              {{ f.name }}
              <button type="button" class="remove-file" @click="newFiles.splice(i, 1)">✕</button>
            </li>
          </ul>
        </div>

        <div class="form-actions">
          <button type="button" class="btn btn-ghost" @click="router.back()">취소</button>
          <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
            <font-awesome-icon v-if="isSubmitting" icon="fa-solid fa-spinner" spin />
            {{ isSubmitting ? "저장 중..." : "저장" }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useBoardStore } from "@/stores/boardStore";
import { useToast } from "vue-toast-notification";
import ClassicEditor from "@ckeditor/ckeditor5-build-classic";
import boardService from "@/services/boardService";

const route      = useRoute();
const router     = useRouter();
const boardStore = useBoardStore();
const toast      = useToast();

const title         = ref("");
const existingFiles = ref([]);
const newFiles      = ref([]);
const editorEl      = ref(null);
const isLoading     = ref(true);
const isSubmitting  = ref(false);

let editorInstance = null;

onMounted(async () => {
  const id = Number(route.params.id);
  await boardStore.fetchBoard(id);
  const board = boardStore.currentBoard;
  if (board) {
    title.value         = board.title;
    existingFiles.value = board.fileList ?? [];
  }
  isLoading.value = false;

  // DOM 렌더 완료 후 에디터 초기화
  await nextTick();
  editorInstance = await ClassicEditor.create(editorEl.value, {
    language: "ko",
    toolbar: [
      "heading", "|", "bold", "italic", "link", "|",
      "bulletedList", "numberedList", "|",
      "blockQuote", "insertTable", "|", "undo", "redo",
    ],
  });
  if (board?.content) {
    editorInstance.setData(board.content);
  }
});

onBeforeUnmount(() => editorInstance?.destroy());

function onFileChange(e) {
  newFiles.value.push(...Array.from(e.target.files));
  e.target.value = "";
}

function getFileUrl(fileUrl) {
  return boardService.getFileUrl(fileUrl);
}

async function onSubmit() {
  isSubmitting.value = true;
  try {
    const id = Number(route.params.id);
    const formData = new FormData();
    formData.append("title", title.value);
    formData.append("content", editorInstance?.getData() ?? "");
    newFiles.value.forEach((f) => formData.append("files", f));

    await boardStore.updateBoard(id, formData);
    toast.open({ message: "수정되었습니다.", type: "success", duration: 1500 });
    router.push(`/community/${id}`);
  } finally {
    isSubmitting.value = false;
  }
}
</script>

<style scoped>
.loading-state { display: flex; align-items: center; justify-content: center; padding: 3rem; color: var(--text-muted); }
.editor-form { display: flex; flex-direction: column; gap: 1.25rem; }
.field { display: flex; flex-direction: column; gap: 0.4rem; }
.field-label { font-size: 0.82rem; font-weight: 600; color: var(--text-secondary); }
.title-input { margin-right: 0; }

.editor-wrap :deep(.ck-editor__editable) { min-height: 400px; }
.editor-wrap :deep(.ck.ck-toolbar) {
  border: 1.5px solid var(--border);
  border-bottom: none;
  border-radius: var(--radius-sm) var(--radius-sm) 0 0;
}
.editor-wrap :deep(.ck.ck-editor__main > .ck-editor__editable) {
  border: 1.5px solid var(--border);
  border-radius: 0 0 var(--radius-sm) var(--radius-sm);
}

.existing-files { display: flex; flex-direction: column; gap: 0.25rem; }
.file-link { font-size: 0.85rem; color: var(--primary); }
.file-link:hover { text-decoration: underline; }
.file-note { font-size: 0.78rem; color: var(--text-muted); margin-top: 0.25rem; }

.file-input { font-size: 0.875rem; }
.selected-files { margin-top: 0.4rem; display: flex; flex-direction: column; gap: 0.25rem; }
.selected-files li { display: flex; align-items: center; gap: 0.5rem; font-size: 0.82rem; color: var(--text-secondary); }
.remove-file { background: none; border: none; color: var(--danger); cursor: pointer; }
.form-actions { display: flex; justify-content: flex-end; gap: 0.75rem; padding-top: 0.5rem; }
</style>

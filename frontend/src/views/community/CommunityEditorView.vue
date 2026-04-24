<template>
  <div>
    <div class="content-header">
      <h2>글쓰기</h2>
    </div>

    <div class="content-body">
      <form @submit.prevent="onSubmit" class="editor-form">
        <div class="field">
          <label class="field-label">제목</label>
          <input v-model="title" type="text" placeholder="제목을 입력하세요." required class="title-input" />
        </div>

        <div class="field">
          <label class="field-label">내용</label>
          <div class="editor-wrap">
            <div ref="editorEl"></div>
          </div>
        </div>

        <div class="field">
          <label class="field-label">첨부파일</label>
          <input type="file" multiple ref="fileInputEl" @change="onFileChange" class="file-input" />
          <ul v-if="files.length" class="selected-files">
            <li v-for="(f, i) in files" :key="i">
              {{ f.name }}
              <button type="button" class="remove-file" @click="files.splice(i, 1)">✕</button>
            </li>
          </ul>
        </div>

        <div class="form-actions">
          <router-link to="/community" class="btn btn-ghost">취소</router-link>
          <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
            <font-awesome-icon v-if="isSubmitting" icon="fa-solid fa-spinner" spin />
            {{ isSubmitting ? "등록 중..." : "등록" }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import { useRouter } from "vue-router";
import { useBoardStore } from "@/stores/boardStore";
import { useToast } from "vue-toast-notification";
import ClassicEditor from "@ckeditor/ckeditor5-build-classic";

const router      = useRouter();
const boardStore  = useBoardStore();
const toast       = useToast();

const title        = ref("");
const files        = ref([]);
const editorEl     = ref(null);
const fileInputEl  = ref(null);
const isSubmitting = ref(false);

let editorInstance = null;

onMounted(async () => {
  editorInstance = await ClassicEditor.create(editorEl.value, {
    language: "ko",
    toolbar: [
      "heading", "|", "bold", "italic", "link", "|",
      "bulletedList", "numberedList", "|",
      "blockQuote", "insertTable", "|", "undo", "redo",
    ],
  });
});

onBeforeUnmount(() => editorInstance?.destroy());

function onFileChange(e) {
  files.value.push(...Array.from(e.target.files));
  e.target.value = "";
}

async function onSubmit() {
  const content = editorInstance?.getData() ?? "";
  if (!content.trim()) {
    toast.open({ message: "내용을 입력해주세요.", type: "warning" });
    return;
  }
  isSubmitting.value = true;
  try {
    const formData = new FormData();
    formData.append("title", title.value);
    formData.append("content", content);
    files.value.forEach((f) => formData.append("files", f));

    const id = await boardStore.createBoard(formData);
    toast.open({ message: "게시글이 등록되었습니다.", type: "success", duration: 1500 });
    router.push(`/community/${id}`);
  } finally {
    isSubmitting.value = false;
  }
}
</script>

<style scoped>
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
.editor-wrap :deep(.ck.ck-editor__main > .ck-editor__editable:focus) {
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(37,99,235,0.1);
}

.file-input { font-size: 0.875rem; }
.selected-files { margin-top: 0.4rem; display: flex; flex-direction: column; gap: 0.25rem; }
.selected-files li { display: flex; align-items: center; gap: 0.5rem; font-size: 0.82rem; color: var(--text-secondary); }
.remove-file { background: none; border: none; color: var(--danger); cursor: pointer; }

.form-actions { display: flex; justify-content: flex-end; gap: 0.75rem; padding-top: 0.5rem; }
</style>

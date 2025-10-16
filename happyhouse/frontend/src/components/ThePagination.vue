<!-- components/ThePagination.vue -->
<template>
  <nav aria-label="Page navigation">
    <ul class="pagination justify-content-center mb-5">
      <li v-if="prev" class="page-item">
        <a
          class="page-link"
          href="#"
          @click.prevent="paginationChanged(startPageIndex - 1)"
          >«</a
        >
      </li>
      <li
        v-for="index in endPageIndex - startPageIndex + 1"
        :key="index"
        :class="{ active: startPageIndex + index - 1 === currentPageIndex }"
        class="page-item number"
      >
        <a
          class="page-link"
          href="#"
          @click.prevent="paginationChanged(startPageIndex + index - 1)"
        >
          {{ startPageIndex + index - 1 }}
        </a>
      </li>
      <li v-if="next" class="page-item">
        <a
          class="page-link"
          href="#"
          @click.prevent="paginationChanged(endPageIndex + 1)"
          >»</a
        >
      </li>
    </ul>
  </nav>
</template>

<script setup>
import { storeToRefs } from "pinia";
import { usePaginationStore } from "@/stores/paginationStore";

const emit = defineEmits(["page-changed"]);
const paginationStore = usePaginationStore();
const { startPageIndex, endPageIndex, currentPageIndex, prev, next } =
  storeToRefs(paginationStore);

function paginationChanged(pageIndex) {
  paginationStore.changePage(pageIndex);
  emit("page-changed", pageIndex);
}
</script>

import { defineStore } from "pinia";

export const usePaginationStore = defineStore("pagination", {
  state: () => ({
    currentPageIndex: 1, // 현재 페이지
    listRowCount: 10, // 한 페이지당 아이템 수
    pageLinkCount: 10, // 페이지 번호 버튼 개수
    totalListItemCount: 0, // 전체 아이템 수
  }),
  getters: {
    pageCount: (state) =>
      Math.ceil(state.totalListItemCount / state.listRowCount),
    startPageIndex: (state) => {
      if (state.currentPageIndex % state.pageLinkCount == 0) {
        return (
          (state.currentPageIndex / state.pageLinkCount - 1) *
            state.pageLinkCount +
          1
        );
      } else {
        return (
          Math.floor((state.currentPageIndex - 1) / state.pageLinkCount) *
            state.pageLinkCount +
          1
        );
      }
    },
    endPageIndex: (state) => {
      const end =
        Math.floor((state.currentPageIndex - 1) / state.pageLinkCount) *
          state.pageLinkCount +
        state.pageLinkCount;
      return end > Math.ceil(state.totalListItemCount / state.listRowCount)
        ? Math.ceil(state.totalListItemCount / state.listRowCount)
        : end;
    },
    prev: (state) => state.currentPageIndex > state.pageLinkCount,
    next: (state) =>
      state.currentPageIndex <
      Math.ceil(state.totalListItemCount / state.listRowCount),
  },
  actions: {
    setTotalCount(count) {
      this.totalListItemCount = count;
    },
    changePage(pageIndex) {
      this.currentPageIndex = pageIndex;
    },
  },
});

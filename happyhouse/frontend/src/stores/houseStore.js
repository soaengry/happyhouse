import { defineStore } from "pinia";
import houseService from "@/services/houseService";
import { usePaginationStore } from "./paginationStore";

export const useHouseStore = defineStore("house", {
  state: () => ({
    count: 0,
    houseList: [],
    sidoCode: 0,
    gugunCode: 0,
    dongCode: 0,
    keyword: "",
    isLoading: false, // ✅ 로딩 상태 추가
  }),
  actions: {
    async fetchHouseList() {
      const paginationStore = usePaginationStore();
      this.isLoading = true; // ✅ 시작 시 true
      try {
        const params = {
          limit: paginationStore.listRowCount,
          offset:
            (paginationStore.currentPageIndex - 1) *
            paginationStore.listRowCount,
          keyword: this.keyword,
          sidoCode: this.sidoCode,
          gugunCode: this.gugunCode,
          dongCode: this.dongCode,
        };
        const { count, houseList } = await houseService.getHouseList(params);
        this.houseList = houseList;
        this.count = count;
        paginationStore.setTotalCount(count);
      } finally {
        this.isLoading = false; // ✅ 완료 후 false
      }
    },
  },
});

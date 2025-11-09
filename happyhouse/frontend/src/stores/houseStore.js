import { defineStore } from "pinia";
import houseService from "@/services/houseService";
import { usePaginationStore } from "./paginationStore";

export const useHouseStore = defineStore("house", {
  state: () => ({
    houseCount: 0,
    houseList: [],
    dealCount: 0,
    dealList: [],
    sidoCode: 0,
    gugunCode: 0,
    dongCode: 0,
    keyword: "",
    aptCode: 0,
    isLoading: false,
  }),
  actions: {
    async getHouseList() {
      const paginationStore = usePaginationStore();
      this.isLoading = true;
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
        this.houseCount = count;
        paginationStore.setTotalCount(this.houseCount);
      } finally {
        this.isLoading = false;
      }
    },
    async getDealList(aptCode) {
      this.isLoading = true;
      try {
        const { count, houseList } = await houseService.getDealList(aptCode);
        this.dealList = houseList;
        this.dealCount = count;
      } finally {
        this.isLoading = false;
      }
    },
  },
});

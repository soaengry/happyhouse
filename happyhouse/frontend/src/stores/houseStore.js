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
    bookmarked: false,
    bookmarks: [],
    busStopList: [], // busStop: {citycode: number, gpslati: string, gpslong: string, nodeid: string, nodenm: string, nodeno: number}
    subwayStationList: [], // subwayStation: {bldnNm: string, distance: number, route: string}
  }),

  actions: {
    resetHouseList() {
      this.houseList = [];
      this.houseCount = 0;
      this.currentPage = 1;
      this.hasMore = true;
    },

    async fetchNextPage() {
      if (!this.hasMore || this.isLoading) return;

      this.isLoading = true;
      try {
        const params = {
          limit: 16,
          offset: (this.currentPage - 1) * 16,
          keyword: this.keyword,
          sidoCode: this.sidoCode,
          gugunCode: this.gugunCode,
          dongCode: this.dongCode,
        };
        const { count, houseList } = await houseService.getHouseList(params);

        if (houseList.length === 0) {
          this.hasMore = false;
        } else {
          this.houseList.push(...houseList);
          this.houseCount = count;
          this.currentPage++;
        }
      } finally {
        this.isLoading = false;
      }
    },

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

    async loadBookmarks() {
      const { houseList } = await houseService.getBookmarks();
      this.bookmarks = houseList.map((h) => h.aptCode);
    },

    async getBookmarkList() {
      this.isLoading = true;
      try {
        const { count, houseList } = await houseService.getBookmarks();
        this.houseList = houseList;
        this.houseCount = count;
        this.bookmarks = houseList.map((h) => h.aptCode);
      } finally {
        this.isLoading = false;
      }
    },

    async toggleBookmark(aptCode) {
      const isBookmarked = this.bookmarks.includes(aptCode);

      if (isBookmarked) {
        await houseService.removeBookmark(aptCode);
        this.bookmarks = this.bookmarks.filter((code) => code !== aptCode);
      } else {
        await houseService.addBookmark(aptCode);
        this.bookmarks.push(aptCode);
      }
      const target = this.houseList.find((h) => h.aptCode === aptCode);
      if (target) {
        target.bookmarked = !isBookmarked;
      }
    },

    async getBusStopList(lat, lng) {
      const params = {
        lat,
        lng,
      };
      const busStopList = await houseService.getBusStopList(params);
      this.busStopList = busStopList;
    },

    async getSubwayStationList(lat, lng) {
      const params = {
        lat,
        lng,
      };
      const subwayStationList = await houseService.getSubwayStationList(params);
      this.subwayStationList = subwayStationList;
    },
  },
});

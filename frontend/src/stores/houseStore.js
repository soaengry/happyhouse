import { defineStore } from "pinia";
import houseService from "@/services/houseService";
import { usePaginationStore } from "./paginationStore";

export const useHouseStore = defineStore("house", {
  state: () => ({
    houseCount: 0,
    houseList: [],
    currentPage: 1,
    hasMore: true,
    dealCount: 0,
    dealList: [],
    sidoCode: 0,
    gugunCode: 0,
    dongCode: 0,
    keyword: "",
    isLoading: false,
    bookmarks: [],
    busStopList: [],
    subwayStationList: [],
    population: null,
    newsList: [],
  }),

  actions: {
    resetHouseList() {
      this.houseList = [];
      this.houseCount = 0;
      this.currentPage = 1;
      this.hasMore = true;
    },

    resetSearch() {
      this.sidoCode = 0;
      this.gugunCode = 0;
      this.dongCode = 0;
      this.keyword = "";
    },

    async fetchNextPage() {
      if (!this.hasMore || this.isLoading) return;
      this.isLoading = true;

      try {
        const params = {
          limit: 16,
          offset: (this.currentPage - 1) * 16,
          keyword: this.keyword || "",
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
      const { houseList } = await houseService.getBookmarkHouses();
      this.bookmarks = houseList.map((h) => h.aptCode);
    },

    async getBookmarkHouseList() {
      this.isLoading = true;
      try {
        const { count, houseList } = await houseService.getBookmarkHouses();
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
        await houseService.removeBookmarkHouse(aptCode);
        this.bookmarks = this.bookmarks.filter((code) => code !== aptCode);
      } else {
        await houseService.addBookmarkHouse(aptCode);
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

    async getPopulation(dongCode) {
      const population = await houseService.getPopulation(dongCode);
      this.population = population;
    },

    async getNews(dongCode) {
      const params = { dongCode };
      const newsList = await houseService.getNews(params);
      this.newsList = newsList;
    },
  },
});

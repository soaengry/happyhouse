import { defineStore } from "pinia";
import api from "@/services/houseService";

export const useHouseStore = defineStore("house", {
  state: () => ({
    count: 0,
    houseList: [], // 매물 목록
    no: 0,
    limit: 10,
    offset: 0,
    keyword: "",
    sidoCode: 0,
    gugunCode: 0,
    dongCode: 0,
    aptCode: 0,
    aptName: "",
    address: "",
    buildYear: 0,
  }),

  actions: {
    async fetchHouseList() {
      const params = {
        limit: this.limit,
        offset: this.offset,
        keyword: this.keyword,
        sidoCode: this.sidoCode,
        gugunCode: this.gugunCode,
        dongCode: this.dongCode,
      };
      const { count, houseList } = await api.getHouseList(params);
      this.houseList = houseList;
      this.count = count;
    },
  },
});

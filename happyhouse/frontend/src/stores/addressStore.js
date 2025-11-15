import { defineStore } from "pinia";
import addressService from "@/services/addressService";

export const useAddressStore = defineStore("address", {
  state: () => ({
    sidoList: [],
    gugunList: [],
    dongList: [],
    bookmarkRegionList: [],
  }),
  actions: {
    async getSidoList() {
      this.sidoList = await addressService.getSidoList();
    },
    async getGugunList(sidoCode) {
      this.gugunList = await addressService.getGugunList(sidoCode);
    },
    async getDongList(gugunCode) {
      this.dongList = await addressService.getDongList(gugunCode);
    },
    resetGugun() {
      this.gugunList = [];
      this.dongList = [];
    },
    resetDong() {
      this.dongList = [];
    },
    async getBookmarkRegionList(dongCode) {
      this.bookmarkRegionList = await addressService.getBookmarkRegions(
        dongCode,
      );
    },
  },
});

import { defineStore } from "pinia";
import api from "@/services/addressService";

export const useAddressStore = defineStore("address", {
  state: () => ({
    sidoList: [],
    sidoCode: 0,
    gugunList: [],
    gugunCode: 0,
    dongList: [],
    dongCode: 0,
  }),
  actions: {
    async getSidoList() {
      this.sidoList = await api.getSidoList();
    },
    async getGugunList() {
      this.gugunList = await api.getGugunList(this.sidoCode);
    },
    async getDongList() {
      this.dongList = await api.getDongList(this.gugunCode);
    },
  },
});

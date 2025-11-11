import { defineStore } from "pinia";
import userService from "@/services/userService";

export const useUserStore = defineStore("user", {
  state: () => ({
    user: null,
    isLoading: false,
    error: "",
  }),

  actions: {
    async fetchUserInfo() {
      try {
        const data = await userService.fetchUserInfo();
        this.user = data;
      } catch (err) {
        console.error("사용자 정보 불러오기 실패", err);
      }
    },

    async updateUserInfo(payload) {
      await userService.updateUser(payload);
      await this.fetchUserInfo();
    },

    logout() {
      this.user = null;
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
    },
  },
});

import { defineStore } from "pinia";
import userService from "@/services/userService";
import { TOKEN_KEYS } from "@/utils/constants";

export const useUserStore = defineStore("user", {
  state: () => ({
    user: null,
    isLoading: false,
    error: "",
  }),

  actions: {
    async register(userData) {
      this.isLoading = true;
      this.error = "";
      try {
        await userService.register(userData);
        return true;
      } catch (err) {
        this.error = err.response?.data?.message || "회원가입에 실패했습니다.";
        return false;
      } finally {
        this.isLoading = false;
      }
    },

    // true = 사용 가능, false = 중복 또는 오류
    async checkUsername(username) {
      try {
        return await userService.checkUsername(username);
      } catch {
        return false;
      }
    },

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
      localStorage.removeItem(TOKEN_KEYS.ACCESS);
      localStorage.removeItem(TOKEN_KEYS.REFRESH);
    },
  },
});

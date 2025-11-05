import { defineStore } from "pinia";
import userService from "@/services/userService";

export const useUserStore = defineStore("user", {
  state: () => ({
    user: null,
    isLoading: false,
    error: "",
  }),

  actions: {
    async register({ username, password, nickname, email }) {
      this.isLoading = true;
      this.error = "";
      try {
        await userService.register({ username, password, nickname, email });
        return true;
      } catch (err) {
        console.error(err);
        this.error = "회원가입 중 오류가 발생했습니다.";
        return false;
      } finally {
        this.isLoading = false;
      }
    },

    async checkUsername(username) {
      try {
        const exists = await userService.checkUsername(username);
        return !exists; // true면 사용 가능
      } catch {
        return null;
      }
    },

    async login({ username, password }) {
      this.isLoading = true;
      this.error = "";
      try {
        const { accessToken, refreshToken } = await userService.login({
          username,
          password,
        });
        localStorage.setItem("accessToken", accessToken);
        localStorage.setItem("refreshToken", refreshToken);
        // 사용자 정보 가져오기
        const userInfo = await userService.fetchUserInfo();
        this.user = userInfo; // 예: { id, username, nickname, email }
        return true;
      } catch (err) {
        console.error(err);
        this.error = "아이디 또는 비밀번호를 확인하세요.";
        return false;
      } finally {
        this.isLoading = false;
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

    logout() {
      this.user = null;
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
    },
  },
});

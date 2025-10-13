import axios from "axios";

const api = axios.create({
  baseURL: process.env.VUE_APP_BASE_URL + "/api", // 기본 도메인 주소
  headers: {
    "Content-Type": "application/json",
  },
  // sessionId 고정
  // withCredentials: true // default: false, false인 경우 sessionId를 매번 신규로 발급
});

export default api;

import axios from "axios";

const api = axios.create({
  baseURL: process.env.VUE_APP_BASE_URL + "/api", // 기본 도메인 주소
  headers: {
    "Content-Type": "application/json",
  },
});

export default api;

import axios from "axios";
import { BASE_URL, TOKEN_KEYS } from "@/utils/constants";

const api = axios.create({
  baseURL: `${BASE_URL}/api`,
  headers: { "Content-Type": "application/json" },
  withCredentials: true,
});

// 요청 인터셉터: AccessToken 자동 포함
api.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEYS.ACCESS);
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 응답 인터셉터: 401 → RefreshToken으로 재발급 후 원래 요청 재시도
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      const refreshToken = localStorage.getItem(TOKEN_KEYS.REFRESH);
      if (!refreshToken) {
        redirectToLogin();
        return Promise.reject(error);
      }

      try {
        const { data } = await axios.post(
          `${BASE_URL}/api/jwt/refresh`,
          { refreshToken },
          { headers: { "Content-Type": "application/json" }, withCredentials: true },
        );

        localStorage.setItem(TOKEN_KEYS.ACCESS, data.accessToken);
        originalRequest.headers.Authorization = `Bearer ${data.accessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        redirectToLogin();
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  },
);

function redirectToLogin() {
  localStorage.removeItem(TOKEN_KEYS.ACCESS);
  localStorage.removeItem(TOKEN_KEYS.REFRESH);
  window.location.href = "/login";
}

export default api;

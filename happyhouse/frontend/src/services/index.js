import axios from "axios";

const BASE_URL = process.env.VUE_APP_BASE_URL || "http://localhost:8080";

const api = axios.create({
  baseURL: BASE_URL + "/api", // 기본 도메인 주소
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // 쿠키 기반 인증
});

// 요청 인터셉터: AccessToken 자동 포함
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 응답 인터셉터: 401 → RefreshToken으로 재발급 시도
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // AccessToken 만료로 401 발생한 경우
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      const refreshToken = localStorage.getItem("refreshToken");
      if (!refreshToken) {
        redirectToLogin();
        return Promise.reject(error);
      }

      try {
        // 4. RefreshToken을 Body에 붙여 재발급 요청
        const { data } = await axios.post(
          BASE_URL + "/api/jwt/refresh",
          { refreshToken }, // Body에 포함
          {
            headers: { "Content-Type": "application/json" },
            withCredentials: true,
          },
        );

        // 5. 재발급 성공 → AccessToken 저장 후 원래 요청 재실행
        localStorage.setItem("accessToken", data.accessToken);
        originalRequest.headers.Authorization = `Bearer ${data.accessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        // 6. 재발급 실패 → 로그인 페이지로 이동
        redirectToLogin();
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  },
);

// 로그인 페이지로 이동
function redirectToLogin() {
  localStorage.clear();
  window.location.href = "/login";
}

export default api;

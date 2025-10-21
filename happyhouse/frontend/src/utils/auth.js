const BASE_URL = process.env.VUE_APP_BASE_URL || "http://localhost:8080";

export async function refreshAccessToken() {
  const refreshToken = localStorage.getItem("refreshToken");
  if (!refreshToken) throw new Error("RefreshToken이 없습니다.");

  const response = await fetch(`${BASE_URL}/api/jwt/refresh`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ refreshToken }),
    credentials: "include", // 쿠키 기반 인증
  });

  if (!response.ok) throw new Error("AccessToken 갱신 실패");

  const data = await response.json();
  localStorage.setItem("accessToken", data.accessToken);
  localStorage.setItem("refreshToken", data.refreshToken);

  return data.accessToken;
}

export async function fetchWithAccess(url, options = {}) {
  let accessToken = localStorage.getItem("accessToken");

  if (!options.headers) options.headers = {};
  options.headers["Authorization"] = `Bearer ${accessToken}`;
  options.headers["Content-Type"] =
    options.headers["Content-Type"] || "application/json";

  let response = await fetch(url, options);

  if (response.status === 401) {
    try {
      accessToken = await refreshAccessToken();
      options.headers["Authorization"] = `Bearer ${accessToken}`;
      response = await fetch(url, options);
    } catch (err) {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      window.location.href = "/login"; // Vue에서도 window.location 사용 가능
    }
  }

  if (!response.ok) {
    throw new Error(`HTTP 오류 : ${response.status}`);
  }

  return response;
}

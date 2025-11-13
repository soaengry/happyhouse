<template>
  <div class="container mt-5" style="max-width: 500px">
    <p>로그인 처리 중입니다...</p>
  </div>
</template>

<script setup>
import { onMounted } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";

const router = useRouter();
const BASE_URL = process.env.VUE_APP_BASE_URL || "http://localhost:8080";

onMounted(() => {
  cookieToBody();
});

async function cookieToBody() {
  try {
    const res = await fetch(`${BASE_URL}/api/jwt/exchange`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include",
    });

    if (!res.ok) throw new Error("인증 실패");

    const data = await res.json();
    localStorage.setItem("accessToken", data.accessToken);
    localStorage.setItem("refreshToken", data.refreshToken);

    // 사용자 정보 요청
    const userInfo = await fetch(`${BASE_URL}/api/user`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${data.accessToken}`,
      },
    });
    if (!userInfo.ok) throw new Error("유저 정보 요청 실패");

    const userData = await userInfo.json();
    const userStore = useUserStore();
    userStore.user = userData;

    router.push("/");
  } catch (err) {
    alert("소셜 로그인 실패");
    router.push("/login");
  }
}
</script>

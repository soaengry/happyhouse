<template>
  <div class="container mt-5" style="max-width: 500px">
    <p>로그인 처리 중입니다...</p>
  </div>
</template>

<script setup>
import { onMounted } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";
import userService from "@/services/userService";
import { useToast } from "vue-toast-notification";
import { TOKEN_KEYS } from "@/utils/constants";

const router = useRouter();
const toast = useToast();

onMounted(cookieToBody);

async function cookieToBody() {
  try {
    const data = await userService.exchangeToken();
    localStorage.setItem(TOKEN_KEYS.ACCESS, data.accessToken);
    localStorage.setItem(TOKEN_KEYS.REFRESH, data.refreshToken);

    const userStore = useUserStore();
    await userStore.fetchUserInfo();

    router.push("/");
  } catch (err) {
    console.error("소셜 로그인 실패", err);
    toast.open({ message: "소셜 로그인에 실패했습니다.", type: "error", duration: 3000 });
    router.push("/login");
  }
}
</script>

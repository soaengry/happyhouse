<template>
  <div class="container mt-5" style="max-width: 500px">
    <h2>내 정보</h2>

    <div v-if="isLoading">불러오는 중...</div>
    <div v-else-if="error" class="text-danger">{{ error }}</div>
    <div v-else>
      <p><strong>아이디:</strong> {{ user.username }}</p>
      <p><strong>닉네임:</strong> {{ user.nickname }}</p>
      <p><strong>이메일:</strong> {{ user.email }}</p>
      <p><strong>소셜 로그인:</strong> {{ user.isSocial ? "예" : "아니오" }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import userService from "@/services/userService";

const user = ref({});
const isLoading = ref(true);
const error = ref("");

onMounted(async () => {
  try {
    const data = await userService.fetchUserInfo();
    user.value = data;
  } catch (err) {
    error.value = "유저 정보를 불러오지 못했습니다.";
    console.error(err);
  } finally {
    isLoading.value = false;
  }
});
</script>

<style scoped>
.text-danger {
  color: red;
}
</style>

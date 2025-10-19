<template>
  <div class="container mt-5" style="max-width: 500px">
    <h1>로그인</h1>

    <form @submit.prevent="onSubmit">
      <label for="username">아이디</label>
      <input
        id="username"
        type="text"
        v-model="username"
        placeholder="아이디"
        required
      />

      <label for="password">비밀번호</label>
      <input
        id="password"
        type="password"
        v-model="password"
        placeholder="비밀번호"
        required
      />

      <p v-if="userStore.error" class="text-danger">{{ userStore.error }}</p>

      <button type="submit" :disabled="userStore.isLoading">
        <span v-if="!userStore.isLoading">로그인</span>
        <span v-else>
          <font-awesome-icon icon="fa-solid fa-spinner" spin /> 로그인 중...
        </span>
      </button>
    </form>

    <div class="mt-3">
      <router-link to="/join">회원가입</router-link>
    </div>

    <div class="social-login mt-4">
      <h3>소셜 로그인</h3>
      <button @click="handleSocialLogin('google')">Google</button>
      <button @click="handleSocialLogin('kakao')">Kakao</button>
      <button @click="handleSocialLogin('naver')">Naver</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";

const router = useRouter();
const userStore = useUserStore();

const username = ref("");
const password = ref("");

async function onSubmit() {
  const success = await userStore.login({
    username: username.value,
    password: password.value,
  });
  if (success) {
    router.push("/");
  }
}

function handleSocialLogin(provider) {
  window.location.href = `${process.env.VUE_APP_BASE_URL}/${process.env.VUE_APP_OAUTH_URL}/${provider}`;
}
</script>

<style scoped>
.text-danger {
  color: red;
}
.social-login button {
  margin-right: 10px;
}
</style>

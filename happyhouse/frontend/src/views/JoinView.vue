<template>
  <div class="container mt-5" style="max-width: 500px">
    <h1>회원 가입</h1>

    <form @submit.prevent="onSubmit">
      <!-- 아이디 -->
      <label for="username">아이디</label>
      <input
        id="username"
        type="text"
        v-model="username"
        placeholder="아이디 (4자 이상)"
        required
        minlength="4"
      />
      <p v-if="username.length >= 4 && isUsernameValid === false">
        이미 사용 중인 아이디입니다.
      </p>
      <p v-if="username.length >= 4 && isUsernameValid === true">
        사용 가능한 아이디입니다.
      </p>

      <!-- 비밀번호 -->
      <label for="password">비밀번호</label>
      <input
        id="password"
        type="password"
        v-model="password"
        placeholder="비밀번호 (4자 이상)"
        required
        minlength="4"
      />

      <!-- 닉네임 -->
      <label for="nickname">닉네임</label>
      <input
        id="nickname"
        type="text"
        v-model="nickname"
        placeholder="닉네임"
        required
      />

      <!-- 이메일 -->
      <label for="email">이메일</label>
      <input
        id="email"
        type="email"
        v-model="email"
        placeholder="이메일 주소"
        required
      />

      <p v-if="userStore.error" class="text-danger">{{ userStore.error }}</p>

      <button
        type="submit"
        :disabled="isUsernameValid !== true || userStore.isLoading"
      >
        <span v-if="!userStore.isLoading">회원가입</span>
        <span v-else>
          <font-awesome-icon icon="fa-solid fa-spinner" spin /> 가입 중...
        </span>
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref, watch } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";

const router = useRouter();
const userStore = useUserStore();

const username = ref("");
const password = ref("");
const nickname = ref("");
const email = ref("");
const isUsernameValid = ref(null);

let debounceTimer = null;
watch(username, (newVal) => {
  clearTimeout(debounceTimer);
  if (newVal.length < 4) {
    isUsernameValid.value = null;
    return;
  }
  debounceTimer = setTimeout(async () => {
    isUsernameValid.value = await userStore.checkUsername(newVal);
  }, 300);
});

async function onSubmit() {
  const success = await userStore.register({
    username: username.value,
    password: password.value,
    nickname: nickname.value,
    email: email.value,
  });
  if (success) {
    router.push("/login");
  }
}
</script>

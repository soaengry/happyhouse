<template>
  <div id="wrap">
    <div class="container">
      <div class="inner">
        <h1>회원 가입</h1>

        <form @submit.prevent="onSubmit">
          <!-- 아이디 -->
          <div class="form-group">
            <input
              id="username"
              type="text"
              v-model="username"
              placeholder="아이디 (4자 이상)"
              required
              minlength="4"
            />
            <div class="info-text">
              <p v-if="username.length >= 4 && isUsernameValid === false">
                이미 사용 중인 아이디입니다.
              </p>
              <p v-if="username.length >= 4 && isUsernameValid === true">
                사용 가능한 아이디입니다.
              </p>
            </div>
          </div>
          <!-- 비밀번호 -->
          <div class="form-group">
            <input
              id="password"
              type="password"
              v-model="password"
              placeholder="비밀번호 (8자 이상)"
              required
              minlength="8"
            />
            <div class="info-text"></div>
          </div>
          <!-- 닉네임 -->
          <div class="form-group">
            <input
              id="nickname"
              type="text"
              v-model="nickname"
              placeholder="닉네임"
              required
            />
            <div class="info-text"></div>
          </div>
          <div class="form-group">
            <!-- 이메일 -->
            <input
              id="email"
              type="email"
              v-model="email"
              placeholder="이메일 주소"
              required
            />
            <div class="info-text">
              <p v-if="userStore.error" class="danger">{{ userStore.error }}</p>
            </div>
          </div>

          <button
            class="join-btn"
            type="submit"
            :disabled="isUsernameValid !== true || userStore.isLoading"
          >
            <span v-if="!userStore.isLoading">회원가입</span>
            <span v-else>
              <font-awesome-icon icon="fa-solid fa-spinner" spin /> 가입 중...
            </span>
          </button>
        </form>
        <router-link class="login-text" to="/login"
          >로그인 화면으로 돌아가기</router-link
        >
      </div>
    </div>
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

<style scoped>
.container {
  background-color: aliceblue;
  min-width: 320px;
  max-width: 500px;
  height: 600px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border-radius: 1rem;
}

.inner {
  width: 270px;
}

.inner h1 {
  color: var(--primary);
  font-weight: 600;
  font-size: 1.2rem;
  margin-bottom: 1.2rem;
  text-align: center;
}

.form-group {
  margin-bottom: 0;
}

.info-text {
  height: 1.1rem;
}

.info-text p {
  font-size: 0.8rem;
  padding-left: 0.3rem;
  line-height: 1rem;
}

.join-btn {
  width: 100%;
  height: 2.5rem;
  margin-bottom: 1rem;
  border: none;
  border-radius: 0.3rem;
  background-color: var(--primary);
  color: var(--light);
}

.join-btn:disabled {
  background-color: var(--light);
  color: var(--gray);
}

.login-text {
  display: block;
  text-align: center;
  font-size: 0.9rem;
}
</style>

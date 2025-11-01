<template>
  <div id="wrap">
    <div class="container">
      <router-link to="/" class="brand">
        <font-awesome-icon class="brand-icon" icon="fa-solid fa-house" />
        <h1 class="brand-name">HAPPY HOUSE</h1>
      </router-link>
      <div class="inner">
        <form @submit.prevent="onSubmit">
          <div class="form-group">
            <input
              id="username"
              type="text"
              v-model="username"
              placeholder="아이디"
              required
            />
          </div>
          <div class="form-group">
            <input
              id="password"
              type="password"
              v-model="password"
              placeholder="비밀번호"
              required
            />
          </div>

          <p v-if="userStore.error" class="info-text text-danger">
            {{ userStore.error }}
          </p>

          <button
            class="login-btn"
            type="submit"
            :disabled="userStore.isLoading"
          >
            <span v-if="!userStore.isLoading">로그인</span>
            <span v-else>
              <font-awesome-icon icon="fa-solid fa-spinner" spin /> 로그인 중...
            </span>
          </button>
        </form>
        <router-link class="join-text" to="/join">회원가입</router-link>
        <hr class="mb-lg-5" />

        <div class="social-login">
          <span>SNS계정으로 로그인하기</span>
          <div class="btn-box">
            <button
              class="social-login-btn"
              @click="handleSocialLogin('google')"
            >
              <img src="../assets/img/logo_google.png" alt="구글 로그인" />
            </button>
            <button
              class="social-login-btn"
              @click="handleSocialLogin('kakao')"
            >
              <img src="../assets/img/logo_kakao.png" alt="카카오 로그인" />
            </button>
            <button
              class="social-login-btn"
              @click="handleSocialLogin('naver')"
            >
              <img src="../assets/img/logo_naver.png" alt="네이버 로그인" />
            </button>
          </div>
        </div>
        <!-- end of .social-login -->
      </div>
      <!-- end of .inner -->
    </div>
    <!-- end of .container -->
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

.brand {
  font-size: 2rem;
  margin-bottom: 2rem;
}

.inner {
  width: 270px;
}

.info-text {
  font-size: 0.9rem;
  margin-bottom: 1rem;
}

.login-btn {
  width: 100%;
  height: 2.5rem;
  margin-bottom: 1rem;
  border: none;
  border-radius: 0.3rem;
  background-color: var(--primary);
  color: var(--light);
}

.social-login {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.social-login span {
  font-size: 0.8rem;
  margin-bottom: 1rem;
}

.social-login .btn-box {
  display: flex;
  justify-content: center;
}

.social-login-btn {
  border: none;
  border-radius: 50%;
  background-color: var(--light);
  height: 36px;
  width: 36px;
  padding: 0;
  margin: 0 1rem;
}

.social-login-btn img {
  height: 80%;
  width: 80%;
}

.join-text {
  display: block;
  text-align: center;
  font-size: 0.9rem;
}
</style>

<template>
  <div id="wrap">
    <div class="container">
      <router-link to="/" class="brand">
        <font-awesome-icon class="brand-icon" icon="fa-solid fa-house" />
        <h1 class="brand-name">HAPPY HOUSE</h1>
      </router-link>
      <div class="inner">
        <div class="social-login">
          <button
            class="social-login-btn"
            @click="handleSocialLogin('google')"
            style="background-color: #ea4335"
          >
            <img src="../assets/img/logo_google.png" alt="구글 로그인" />
            구글 로그인
          </button>
          <button
            class="social-login-btn"
            @click="handleSocialLogin('kakao')"
            style="background-color: #f9e000"
          >
            <img src="../assets/img/logo_kakao.png" alt="카카오 로그인" />
            카카오 로그인
          </button>
          <button
            class="social-login-btn"
            @click="handleSocialLogin('naver')"
            style="background-color: #069729"
          >
            <img src="../assets/img/logo_naver.png" alt="네이버 로그인" />
            네이버 로그인
          </button>
        </div>
        <!-- end of .social-login -->
      </div>
      <!-- end of .inner -->
    </div>
    <!-- end of .container -->
  </div>
</template>

<script setup>
function handleSocialLogin(provider) {
  window.location.href = `${process.env.VUE_APP_BASE_URL}/${process.env.VUE_APP_OAUTH_URL}/${provider}`;
}
</script>

<style scoped>
.container {
  background-color: aliceblue;
  min-width: 320px;
  max-width: 500px;
  height: 400px;
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

.social-login {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.social-login-btn {
  border: none;
  border-radius: 0.2rem;
  width: 100%;
  height: 2.5rem;
  padding: 0;
  margin-bottom: 1rem;
  position: relative;
  color: white;
  font-size: 0.9rem;
  font-weight: 500;
}

.social-login-btn img {
  height: 1.5rem;
  width: 1.5rem;
  position: absolute;
  left: 1rem;
  top: 0.5rem;
}

.join-text {
  display: block;
  text-align: center;
  font-size: 0.9rem;
}
</style>

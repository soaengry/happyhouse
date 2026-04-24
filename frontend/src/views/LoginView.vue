<template>
  <div id="wrap">
    <div class="login-card">
      <div class="login-brand" @click="$router.push('/')">
        <div class="brand-icon-wrap">
          <font-awesome-icon icon="fa-solid fa-house" />
        </div>
        <span>HAPPY HOUSE</span>
      </div>

      <h2 class="login-title">로그인</h2>
      <p class="login-sub">소셜 계정으로 간편하게 시작하세요.</p>

      <div class="social-list">
        <button class="social-btn google" @click="handleSocialLogin('google')">
          <img src="../assets/img/logo_google.png" alt="Google" />
          구글로 계속하기
        </button>
        <button class="social-btn kakao" @click="handleSocialLogin('kakao')">
          <img src="../assets/img/logo_kakao.png" alt="Kakao" />
          카카오로 계속하기
        </button>
        <button class="social-btn naver" @click="handleSocialLogin('naver')">
          <img src="../assets/img/logo_naver.png" alt="Naver" />
          네이버로 계속하기
        </button>
      </div>

      <div class="login-footer">
        <span>아직 계정이 없으신가요?</span>
        <router-link to="/join">회원가입</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
function handleSocialLogin(provider) {
  window.location.href = `${process.env.VUE_APP_BASE_URL}/${process.env.VUE_APP_OAUTH_URL}/${provider}`;
}
</script>

<style scoped>
.login-card {
  background: var(--bg-card);
  border-radius: var(--radius-2xl);
  padding: 2.5rem 2rem;
  width: 100%;
  max-width: 400px;
  box-shadow: var(--shadow-lg);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.login-brand {
  display: flex;
  align-items: center;
  gap: 0.625rem;
  cursor: pointer;
  margin-bottom: 2rem;
}
.brand-icon-wrap {
  width: 2rem;
  height: 2rem;
  background: var(--primary);
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 0.9rem;
}
.login-brand span {
  font-size: 1rem;
  font-weight: 700;
  letter-spacing: 0.04em;
  color: var(--text);
}

.login-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text);
  margin-bottom: 0.375rem;
  text-align: center;
}
.login-sub {
  font-size: 0.875rem;
  color: var(--text-muted);
  margin-bottom: 2rem;
  text-align: center;
}

.social-list {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}
.social-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  width: 100%;
  height: 2.75rem;
  border: 1.5px solid var(--border);
  border-radius: var(--radius);
  background: var(--bg-card);
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--text);
  transition: background 0.15s, border-color 0.15s, transform 0.1s;
}
.social-btn:hover  { background: var(--bg); border-color: #CBD5E1; }
.social-btn:active { transform: scale(0.98); }
.social-btn img {
  width: 1.25rem;
  height: 1.25rem;
  object-fit: contain;
}

.login-footer {
  margin-top: 1.75rem;
  font-size: 0.85rem;
  color: var(--text-muted);
  display: flex;
  gap: 0.375rem;
  align-items: center;
}
.login-footer a {
  color: var(--primary);
  font-weight: 500;
}
.login-footer a:hover { text-decoration: underline; }
</style>

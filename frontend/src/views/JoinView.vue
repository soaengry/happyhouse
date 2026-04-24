<template>
  <div id="wrap">
    <div class="join-card">
      <div class="join-brand" @click="$router.push('/')">
        <div class="brand-icon-wrap">
          <font-awesome-icon icon="fa-solid fa-house" />
        </div>
        <span>HAPPY HOUSE</span>
      </div>

      <h2 class="join-title">회원가입</h2>
      <p class="join-sub">정보를 입력해 계정을 만드세요.</p>

      <form @submit.prevent="onSubmit" class="join-form">
        <!-- Username -->
        <div class="field">
          <label class="field-label" for="username">아이디</label>
          <input
            id="username"
            type="text"
            v-model="username"
            placeholder="4자 이상 입력"
            required
            minlength="4"
          />
          <p v-if="username.length >= 4 && isUsernameValid === false" class="field-hint danger">
            이미 사용 중인 아이디입니다.
          </p>
          <p v-if="username.length >= 4 && isUsernameValid === true" class="field-hint success">
            사용 가능한 아이디입니다.
          </p>
        </div>

        <!-- Password -->
        <div class="field">
          <label class="field-label" for="password">비밀번호</label>
          <input
            id="password"
            type="password"
            v-model="password"
            placeholder="8자 이상 입력"
            required
            minlength="8"
          />
        </div>

        <!-- Nickname -->
        <div class="field">
          <label class="field-label" for="nickname">닉네임</label>
          <input
            id="nickname"
            type="text"
            v-model="nickname"
            placeholder="사용할 닉네임"
            required
          />
        </div>

        <!-- Email -->
        <div class="field">
          <label class="field-label" for="email">이메일</label>
          <input
            id="email"
            type="email"
            v-model="email"
            placeholder="이메일 주소"
            required
          />
          <p v-if="userStore.error" class="field-hint danger">{{ userStore.error }}</p>
        </div>

        <button
          class="btn btn-primary submit-btn"
          type="submit"
          :disabled="isUsernameValid !== true || userStore.isLoading"
        >
          <font-awesome-icon v-if="userStore.isLoading" icon="fa-solid fa-spinner" spin />
          <span>{{ userStore.isLoading ? "가입 중..." : "회원가입" }}</span>
        </button>
      </form>

      <div class="join-footer">
        <span>이미 계정이 있으신가요?</span>
        <router-link to="/login">로그인</router-link>
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

const username       = ref("");
const password       = ref("");
const nickname       = ref("");
const email          = ref("");
const isUsernameValid = ref(null);

let debounceTimer = null;
watch(username, (val) => {
  clearTimeout(debounceTimer);
  if (val.length < 4) { isUsernameValid.value = null; return; }
  debounceTimer = setTimeout(async () => {
    isUsernameValid.value = await userStore.checkUsername(val);
  }, 300);
});

async function onSubmit() {
  const success = await userStore.register({
    username: username.value,
    password: password.value,
    nickname: nickname.value,
    email:    email.value,
  });
  if (success) router.push("/login");
}
</script>

<style scoped>
.join-card {
  background: var(--bg-card);
  border-radius: var(--radius-2xl);
  padding: 2.5rem 2rem;
  width: 100%;
  max-width: 420px;
  box-shadow: var(--shadow-lg);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.join-brand {
  display: flex;
  align-items: center;
  gap: 0.625rem;
  cursor: pointer;
  margin-bottom: 1.75rem;
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
.join-brand span {
  font-size: 1rem;
  font-weight: 700;
  letter-spacing: 0.04em;
  color: var(--text);
}

.join-title {
  font-size: 1.4rem;
  font-weight: 700;
  color: var(--text);
  margin-bottom: 0.3rem;
  text-align: center;
}
.join-sub {
  font-size: 0.875rem;
  color: var(--text-muted);
  margin-bottom: 1.75rem;
  text-align: center;
}

.join-form { width: 100%; display: flex; flex-direction: column; gap: 1rem; }

.field { display: flex; flex-direction: column; gap: 0.3rem; }
.field-label {
  font-size: 0.82rem;
  font-weight: 600;
  color: var(--text-secondary);
}
.field input { margin-right: 0; }

.field-hint {
  font-size: 0.78rem;
  line-height: 1.3;
}
.field-hint.success { color: var(--success); }
.field-hint.danger  { color: var(--danger); }

.submit-btn {
  width: 100%;
  height: 2.75rem;
  margin-top: 0.5rem;
  font-size: 0.925rem;
  border-radius: var(--radius);
}

.join-footer {
  margin-top: 1.5rem;
  font-size: 0.85rem;
  color: var(--text-muted);
  display: flex;
  gap: 0.375rem;
}
.join-footer a {
  color: var(--primary);
  font-weight: 500;
}
.join-footer a:hover { text-decoration: underline; }
</style>

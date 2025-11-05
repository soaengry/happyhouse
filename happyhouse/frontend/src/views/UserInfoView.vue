<template>
  <div class="container">
    <div class="content-title">
      <h2>프로필 정보</h2>
    </div>
    <hr />
    <section class="user-info">
      <div v-if="isLoading">불러오는 중...</div>
      <div v-else-if="error" class="text-danger">{{ error }}</div>
      <div v-else>
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label for="username">아이디</label>
            <input id="username" type="text" v-model="user.username" readonly />
          </div>
          <div class="form-group">
            <label for="nickname">닉네임</label>
            <input id="nickname" type="text" v-model="user.nickname" />
          </div>
          <div class="form-group">
            <label for="email">이메일</label>
            <input id="email" type="text" v-model="user.email" readonly />
          </div>
          <div v-if="!user.isSocial">
            <div class="form-group">
              <label for="newPassword">현재 비밀번호</label>
              <input id="newPassword" type="password" v-model="newPassword" />
            </div>
            <div class="form-group">
              <label for="comfirmPassword">새 비밀번호</label>
              <input
                id="comfirmPassword"
                type="password"
                v-model="comfirmPassword"
              />
            </div>
            <p class="text-danger" v-if="passwordMismatch">
              비밀번호가 일치하지 않습니다.
            </p>
          </div>
          <p class="info-text" v-else>소셜 로그인 계정입니다.</p>
          <div class="btn-box">
            <button class="logout-btn" type="button" @click="logout">
              로그아웃
            </button>
            <button class="form-btn" type="submit">프로필 수정</button>
          </div>
        </form>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import userService from "@/services/userService";
import { useUserStore } from "@/stores/userStore";
import { useRouter } from "vue-router";

const user = ref({});
const isLoading = ref(true);
const error = ref("");
const comfirmPassword = ref("");
const newPassword = ref("");
const successMessage = ref("");
const passwordMismatch = computed(() => {
  return newPassword.value && comfirmPassword.value !== newPassword.value;
});
const store = useUserStore();
const router = useRouter();

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

async function handleSubmit(e) {
  e.preventDefault();
  error.value = "";
  successMessage.value = "";

  if (!user.value.isSocial && comfirmPassword.value !== newPassword.value) {
    error.value = "비밀번호가 일치하지 않습니다.";
    return;
  }

  const payload = {
    username: user.value.username,
    nickname: user.value.nickname,
    email: user.value.email,
    password: comfirmPassword.value,
  };

  try {
    await userService.updateUser(payload);
    successMessage.value = "프로필이 성공적으로 수정되었습니다.";
    newPassword.value = "";
    comfirmPassword.value = "";
  } catch (err) {
    error.value = "프로필 수정에 실패했습니다.";
    console.error(err);
  }
}

function logout() {
  store.logout();
  router.push("/login");
}
</script>

<style scoped>
.container {
  padding: 0;
}

.form-group {
  display: flex;
}

.form-group label {
  font-size: 0.9rem;
  line-height: 2.5rem;
  width: 150px;
}

.form-group input:read-only {
  background-color: lightgray;
}

.form-group input:read-only:focus {
  border: 1px solid lightgray;
}

.info-text {
  font-size: 0.8rem;
  text-align: center;
  margin-bottom: 1rem;
}

.btn-box {
  display: flex;
  justify-content: end;
}

.form-btn {
  background-color: var(--primary);
  color: var(--light);
  height: 40px;
  width: 120px;
  border: none;
  border-radius: 0.2rem;
  margin-left: 1rem;
}

.logout-btn {
  color: var(--primary);
  height: 40px;
  width: 120px;
  border: none;
  border-radius: 0.2rem;
}
</style>

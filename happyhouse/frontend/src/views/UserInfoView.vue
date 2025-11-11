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
          <div class="row">
            <div class="img mb-4">
              <div
                class="mt-3 mb-3 rounded-circle position-relative"
                style="width: 200px; height: 200px; margin: 0 auto"
              >
                <div class="profile-image-box">
                  <img
                    :src="
                      previewUrl || getProfileImageUrl(user.profileImageUrl)
                    "
                    alt="프로필 이미지"
                    class="w-100 h-100 rounded-circle"
                  />
                  <button
                    class="profile-btn"
                    type="button"
                    @click="triggerFileInput"
                  >
                    <font-awesome-icon icon="fa-solid fa-camera" />
                  </button>

                  <!-- 숨겨진 파일 선택 input -->
                  <input
                    ref="fileInput"
                    type="file"
                    accept="image/*"
                    @change="handleImageSelect"
                    style="display: none"
                  />
                </div>
              </div>
            </div>
          </div>
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
import { ref, onMounted } from "vue";
import userService from "@/services/userService";
import { useUserStore } from "@/stores/userStore";
import { useRouter } from "vue-router";
import { useToast } from "vue-toast-notification";

const user = ref({});
const isLoading = ref(true);
const error = ref("");
const successMessage = ref("");
const fileInput = ref(null);
const selectedImageFile = ref(null); // 실제 파일 객체
const previewUrl = ref(""); // 미리보기용 URL

const store = useUserStore();
const router = useRouter();
const toast = useToast();

const BASE_URL = process.env.VUE_APP_BASE_URL || "http://localhost:8080";

onMounted(async () => {
  try {
    const data = await userService.fetchUserInfo();
    user.value = data;
  } catch (err) {
    toast.error("유저 정보 조회 실패");
    console.error(err);
    router.push("/login");
  } finally {
    isLoading.value = false;
  }
});

async function handleSubmit(e) {
  e.preventDefault();
  error.value = "";
  successMessage.value = "";

  const formData = new FormData();
  formData.append("username", user.value.username);
  formData.append("nickname", user.value.nickname);
  formData.append("email", user.value.email);

  if (selectedImageFile.value) {
    formData.append("file", selectedImageFile.value);
  }

  try {
    await store.updateUserInfo(formData);
    user.value = store.user; //
    toast.success("프로필이 수정되었습니다.", { duration: 1500 });
    selectedImageFile.value = null;
    previewUrl.value = "";
  } catch (err) {
    toast.error("프로필 수정 실패");
    console.error(err);
  }
}

function triggerFileInput() {
  fileInput.value?.click();
}

function handleImageSelect(event) {
  const file = event.target.files[0];
  if (!file) return;

  selectedImageFile.value = file;
  previewUrl.value = URL.createObjectURL(file); // 브라우저 미리보기 URL 생성
}

function getProfileImageUrl(fileName) {
  if (!fileName) return `${BASE_URL}/api/user/image?fileName=default.png`; // 기본 이미지
  return `${BASE_URL}/api/user/image?fileName=${encodeURIComponent(fileName)}`;
}

function logout() {
  store.logout();
  router.push("/login");
}
</script>

<style scoped>
.container {
  padding: 1rem;
}

.profile-image-box {
  width: 100%;
  height: 100%;
}

.profile-image-box img {
  object-fit: cover;
}

.profile-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  height: 3rem;
  width: 3rem;
  background-color: var(--primary);
  border-radius: 50%;
  color: var(--light);
  border: none;
  font-size: 1.3rem;
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

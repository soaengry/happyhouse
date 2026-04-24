<template>
  <div>
    <div class="content-header">
      <h2>프로필 정보</h2>
      <p>계정 정보를 확인하고 수정하세요.</p>
    </div>

    <div class="content-body">
      <div v-if="isLoading" class="loading-state">
        <font-awesome-icon icon="fa-solid fa-spinner" spin />
        <span>불러오는 중...</span>
      </div>

      <form v-else @submit.prevent="handleSubmit" class="profile-form">
        <!-- Avatar -->
        <div class="avatar-section">
          <div class="avatar-wrap">
            <img
              :src="previewUrl || getProfileImageUrl(user.profileImageUrl)"
              alt="프로필"
              class="avatar-img"
            />
            <button class="avatar-edit-btn" type="button" @click="triggerFileInput">
              <font-awesome-icon icon="fa-solid fa-camera" />
            </button>
            <input
              ref="fileInput"
              type="file"
              accept="image/*"
              @change="handleImageSelect"
              style="display: none"
            />
          </div>
          <div class="avatar-info">
            <p class="avatar-name">{{ user.nickname }}</p>
            <p class="avatar-username">@{{ user.username }}</p>
          </div>
        </div>

        <div class="form-divider"></div>

        <!-- Fields -->
        <div class="form-fields">
          <div class="field-row">
            <label class="field-label">아이디</label>
            <input type="text" v-model="user.username" readonly />
          </div>
          <div class="field-row">
            <label class="field-label">닉네임</label>
            <input type="text" v-model="user.nickname" placeholder="닉네임 입력" />
          </div>
          <div class="field-row">
            <label class="field-label">이메일</label>
            <input type="email" v-model="user.email" readonly />
          </div>
        </div>

        <!-- Actions -->
        <div class="form-actions">
          <button type="button" class="btn btn-ghost logout-btn" @click="logout">
            로그아웃
          </button>
          <button type="submit" class="btn btn-primary">
            저장하기
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import userService from "@/services/userService";
import { useUserStore } from "@/stores/userStore";
import { useRouter } from "vue-router";
import { useToast } from "vue-toast-notification";
import { BASE_URL } from "@/utils/constants";

const user            = ref({});
const isLoading       = ref(true);
const fileInput       = ref(null);
const selectedImageFile = ref(null);
const previewUrl      = ref("");

const store  = useUserStore();
const router = useRouter();
const toast  = useToast();

onMounted(async () => {
  try {
    user.value = await userService.fetchUserInfo();
  } catch (err) {
    toast.open({ message: "유저 정보를 불러올 수 없습니다.", type: "error" });
    router.push("/login");
  } finally {
    isLoading.value = false;
  }
});

async function handleSubmit() {
  const formData = new FormData();
  formData.append("username", user.value.username);
  formData.append("nickname", user.value.nickname);
  formData.append("email",    user.value.email);
  if (selectedImageFile.value) {
    formData.append("file", selectedImageFile.value);
  }

  try {
    await store.updateUserInfo(formData);
    user.value = store.user;
    selectedImageFile.value = null;
    previewUrl.value = "";
    toast.open({ message: "프로필이 수정되었습니다.", type: "success", duration: 1500 });
  } catch (err) {
    toast.open({ message: "프로필 수정에 실패했습니다.", type: "error" });
  }
}

function triggerFileInput() { fileInput.value?.click(); }

function handleImageSelect(event) {
  const file = event.target.files[0];
  if (!file) return;
  selectedImageFile.value = file;
  previewUrl.value = URL.createObjectURL(file);
}

function getProfileImageUrl(fileName) {
  if (!fileName) return `${BASE_URL}/api/user/image?fileName=default.png`;
  return `${BASE_URL}/api/user/image?fileName=${encodeURIComponent(fileName)}`;
}

function logout() {
  store.logout();
  router.push("/login");
}
</script>

<style scoped>
.loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 3rem;
  color: var(--text-muted);
  font-size: 0.875rem;
}

.profile-form { display: flex; flex-direction: column; gap: 1.5rem; }

/* Avatar */
.avatar-section {
  display: flex;
  align-items: center;
  gap: 1.25rem;
}
.avatar-wrap {
  position: relative;
  width: 5rem;
  height: 5rem;
  flex-shrink: 0;
}
.avatar-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid var(--border);
}
.avatar-edit-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 1.75rem;
  height: 1.75rem;
  background: var(--primary);
  border: 2px solid var(--bg-card);
  border-radius: 50%;
  color: #fff;
  font-size: 0.7rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.15s;
}
.avatar-edit-btn:hover { background: var(--primary-700); }

.avatar-name {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--text);
}
.avatar-username {
  font-size: 0.85rem;
  color: var(--text-muted);
  margin-top: 0.2rem;
}

.form-divider {
  height: 1px;
  background: var(--border);
}

/* Fields */
.form-fields { display: flex; flex-direction: column; gap: 1rem; }
.field-row {
  display: flex;
  align-items: center;
  gap: 1rem;
}
.field-label {
  width: 80px;
  flex-shrink: 0;
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--text-secondary);
}
.field-row input { flex: 1; margin-right: 0; }

/* Actions */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  padding-top: 0.5rem;
}
.logout-btn {
  color: var(--danger);
  font-weight: 500;
}
.logout-btn:hover { background: #FEF2F2; }
</style>

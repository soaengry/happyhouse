import { createRouter, createWebHistory } from "vue-router";
import MainLayout from "@/layouts/MainLayout.vue";
import { useUserStore } from "@/stores/userStore";
import { TOKEN_KEYS } from "@/utils/constants";

const routes = [
  {
    path: "/",
    component: MainLayout,
    children: [
      {
        path: "",
        name: "Home",
        component: () => import("@/views/HouseMain.vue"),
      },
      {
        path: "user",
        name: "UserInfoView",
        component: () => import("@/views/UserInfoView.vue"),
        meta: { requiresAuth: true },
      },
      {
        path: "bookmark",
        children: [
          {
            path: "house",
            name: "Bookmark",
            component: () => import("@/views/HouseMain.vue"),
            meta: { requiresAuth: true },
          },
          {
            path: "region",
            name: "BookmarkRegion",
            component: () => import("@/views/BookmarkRegion.vue"),
            meta: { requiresAuth: true },
          },
        ],
      },
      {
        path: "news",
        name: "News",
        component: () => import("@/views/NewsView.vue"),
      },
      {
        path: "community",
        name: "CommunityList",
        component: () => import("@/views/community/CommunityListView.vue"),
      },
      {
        path: "community/write",
        name: "CommunityWrite",
        component: () => import("@/views/community/CommunityEditorView.vue"),
        meta: { requiresAuth: true },
      },
      {
        path: "community/:id",
        name: "CommunityDetail",
        component: () => import("@/views/community/CommunityDetailView.vue"),
      },
      {
        path: "community/:id/edit",
        name: "CommunityEdit",
        component: () => import("@/views/community/CommunityUpdateView.vue"),
        meta: { requiresAuth: true },
      },
    ],
  },
  {
    path: "/login",
    name: "login",
    component: () => import("@/views/LoginView.vue"),
  },
  {
    path: "/join",
    name: "join",
    component: () => import("@/views/JoinView.vue"),
  },
  {
    path: "/cookie",
    name: "CookieView",
    component: () => import("@/views/CookieView.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

router.beforeEach(async (to, from, next) => {
  const store = useUserStore();
  const token = localStorage.getItem(TOKEN_KEYS.ACCESS);

  // 토큰이 있고 user가 아직 없으면 사용자 정보 로드
  if (token && !store.user) {
    await store.fetchUserInfo();
  }

  // 인증 필요 라우트 접근 시 토큰 없으면 로그인으로 이동
  if (to.meta.requiresAuth && !token) {
    next("/login");
    return;
  }

  next();
});

export default router;

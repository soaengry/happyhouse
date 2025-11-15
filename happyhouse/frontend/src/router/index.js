import { createRouter, createWebHistory } from "vue-router";
import MainLayout from "@/layouts/MainLayout.vue";
import { useUserStore } from "@/stores/userStore";

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
      },
      {
        path: "bookmark",
        children: [
          {
            path: "house",
            name: "Bookmark",
            component: () => import("@/views/HouseMain.vue"),
          },
          {
            path: "region",
            name: "BookmarkRegion",
            component: () => import("@/views/HouseMain.vue"),
          },
        ],
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
  const token = localStorage.getItem("accessToken");

  if (token && !store.user) {
    await store.fetchUserInfo?.();
  }

  next();
});

export default router;

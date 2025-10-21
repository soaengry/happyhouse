import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "main",
      component: () => import("@/views/HouseMain.vue"),
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
    {
      path: "/user",
      name: "UserInfoView",
      component: () => import("@/views/UserInfoView.vue"),
    },
  ],
});

export default router;

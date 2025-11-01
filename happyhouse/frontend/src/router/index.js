import { createRouter, createWebHistory } from "vue-router";
import MainLayout from "@/layouts/MainLayout.vue";

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

export default router;

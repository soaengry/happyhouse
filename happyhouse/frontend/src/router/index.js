import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "main",
      component: () => import("../views/HouseMain.vue"),
    },
    {
      path: "/login",
      name: "login",
      component: () => import("../views/LoginView.vue"),
    },
    {
      path: "/join",
      name: "join",
      component: () => import("../views/JoinView.vue"),
    },
  ],
});

export default router;

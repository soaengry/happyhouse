import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import { createPinia } from "pinia";

import ToastPlugin from "vue-toast-notification";
import "vue-toast-notification/dist/theme-bootstrap.css";

import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";

import {
  faHouse,
  faMagnifyingGlass,
  faSpinner,
  faBookmark,
  faNewspaper,
  faBars,
  faStar,
  faCamera,
  faXmark,
  faPlus,
  faUsers,
  faChevronDown,
  faArrowRight,
} from "@fortawesome/free-solid-svg-icons";

import { faStar as regularStar } from "@fortawesome/free-regular-svg-icons";

library.add(
  faHouse, faMagnifyingGlass, faSpinner, faBookmark, faNewspaper,
  faBars, faStar, faCamera, faXmark, faPlus, faUsers,
  faChevronDown, faArrowRight, regularStar,
);

const app = createApp(App);
app
  .use(createPinia())
  .use(router)
  .use(ToastPlugin)
  .component("font-awesome-icon", FontAwesomeIcon)
  .mount("#app");

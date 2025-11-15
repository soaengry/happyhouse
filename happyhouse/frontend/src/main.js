import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";

import ToastPlugin from "vue-toast-notification";
import "vue-toast-notification/dist/theme-bootstrap.css";

/* import the fontawesome core */
import { library } from "@fortawesome/fontawesome-svg-core";

/* import font awesome icon component */
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";

/* import icons and add them to the Library */
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
} from "@fortawesome/free-solid-svg-icons";

import { faStar as regularStar } from "@fortawesome/free-regular-svg-icons";

import { createPinia } from "pinia";

library.add(faMagnifyingGlass);
library.add(faSpinner);
library.add(faHouse);
library.add(faBookmark);
library.add(faNewspaper);
library.add(faBars);
library.add(faStar);
library.add(faCamera);
library.add(regularStar);
library.add(faXmark);
library.add(faPlus);
library.add(faUsers);

const pinia = createPinia();
const app = createApp(App);

app
  .use(pinia)
  .use(router)
  .use(ToastPlugin)
  .component("font-awesome-icon", FontAwesomeIcon)
  .mount("#app");

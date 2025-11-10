import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
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
  faGlobe,
  faBars,
  faStar,
} from "@fortawesome/free-solid-svg-icons";

import { createPinia } from "pinia";

library.add(faMagnifyingGlass);
library.add(faSpinner);
library.add(faHouse);
library.add(faBookmark);
library.add(faNewspaper);
library.add(faGlobe);
library.add(faBars);
library.add(faStar);

const pinia = createPinia();
const app = createApp(App);

app
  .use(pinia)
  .use(router)
  .component("font-awesome-icon", FontAwesomeIcon)
  .mount("#app");

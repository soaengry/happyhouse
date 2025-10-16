import { fetchData } from "./apiHelper";

const addressService = {
  getSidoList() {
    return fetchData("/sido");
  },
  getGugunList(sidoCode) {
    return fetchData(`/gugun/${sidoCode}`);
  },
  getDongList(gugunCode) {
    return fetchData(`/dong/${gugunCode}`);
  },
};

export default addressService;

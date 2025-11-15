import api from ".";
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
  async addBookmarkRegion(dongCode) {
    const { data } = await api.post(`/bookmark/region/${dongCode}`);
    return data;
  },

  async removeBookmarkRegion(dongCode) {
    const { data } = await api.delete(`/bookmark/region/${dongCode}`);
    return data;
  },

  getBookmarkRegions() {
    return fetchData("/bookmark/region");
  },
};

export default addressService;

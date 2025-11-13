import api from ".";
import { fetchData } from "./apiHelper";

const houseService = {
  // 매물 리스트 조회
  getHouseList(params) {
    // params: { limit: number, offset: number, sidoCode: number, gugunCode: number, dongCode: number, keyword: string}
    return fetchData("/house", { params }); // { count: number, houseList: [...], result: number }
  },

  // 특정 매물 거래 내역 조회
  getDealList(aptCode) {
    // aptCode: number
    return fetchData(`/house/${aptCode}`); // { count: number, houseList: [...], result: number }
  },

  async addBookmark(aptCode) {
    const { data } = await api.post(`/bookmark/${aptCode}`);
    return data;
  },

  async removeBookmark(aptCode) {
    const { data } = await api.delete(`/bookmark/${aptCode}`);
    return data;
  },

  getBookmarks() {
    return fetchData("/bookmark");
  },
};

export default houseService;

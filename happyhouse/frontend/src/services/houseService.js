import api from ".";
import { fetchData } from "./apiHelper";

const houseService = {
  resetSearch() {
    this.sidoCode = 0;
    this.gugunCode = 0;
    this.dongCode = 0;
    this.keyword = "";
    this.houseList = [];
    this.houseCount = 0;
    this.currentPage = 1;
    this.hasMore = true;
  },

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

  async addBookmarkHouse(aptCode) {
    const { data } = await api.post(`/bookmark/house/${aptCode}`);
    return data;
  },

  async removeBookmarkHouse(aptCode) {
    const { data } = await api.delete(`/bookmark/house/${aptCode}`);
    return data;
  },

  getBookmarkHouses() {
    return fetchData("/bookmark/house");
  },

  async getBusStopList(params) {
    return fetchData("/house/busStops", { params });
  },

  async getSubwayStationList(params) {
    return fetchData("/house/subwayStations", { params });
  },
};

export default houseService;

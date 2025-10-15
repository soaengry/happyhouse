import api from ".";

const houseService = {
  // 매물 리스트 조회
  async getHouseList({
    limit,
    offset,
    sidoCode,
    gugunCode,
    dongCode,
    keyword,
  }) {
    const params = { limit, offset, sidoCode, gugunCode, dongCode, keyword };
    const { data } = await api.get("/house", { params });
    return data; // { count: number, houseList: [...], result: number }
  },

  // 특정 매물 거래 내역 조회
  async getDealList(aptCode) {
    const { data } = await api.get(`/house/${aptCode}`);
    return data; // { count: number, houseList: [...], result: number }
  },
};

export default houseService;

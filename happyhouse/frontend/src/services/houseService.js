import api from ".";

export default {
  async getAllDealList() {
    const params = {
      limit: 10,
      offset: 0,
    };
    try {
      const res = await api.get("/house", { params });
      console.log(res.data); // 여기서 확인
      return res.data; // 반드시 data 반환
    } catch (err) {
      console.error(err);
      return []; // 에러 시 빈 배열 반환
    }
  },
};

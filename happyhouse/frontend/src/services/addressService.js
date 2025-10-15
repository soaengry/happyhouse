import api from ".";

const addressService = {
  async getSidoList() {
    const { data } = await api.get("/sido");
    return data;
  },
  async getGugunList(sidoCode) {
    const { data } = await api.get(`/gugun/${sidoCode}`);
    return data;
  },
  async getDongList(gugunCode) {
    const { data } = await api.get(`/dong/${gugunCode}`);
    return data;
  },
};

export default addressService;

import api from ".";
import { fetchData } from "./apiHelper";

const userService = {
  async register(userData) {
    const { data } = await api.post("/user", userData);
    return data;
  },

  // true = 사용 가능, false = 중복
  async checkUsername(username) {
    const { data } = await api.post("/user/exist", { username });
    return data;
  },

  async fetchUserInfo() {
    return fetchData("/user");
  },

  async updateUser(userData) {
    const { data } = await api.put("/user", userData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return data;
  },

  async exchangeToken() {
    const { data } = await api.post("/jwt/exchange", null, {
      withCredentials: true,
    });
    return data;
  },
};

export default userService;

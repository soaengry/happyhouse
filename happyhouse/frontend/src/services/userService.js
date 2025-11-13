import api from ".";
import { fetchData } from "./apiHelper";

const userService = {
  async fetchUserInfo() {
    return fetchData("/user");
  },
  async updateUser(userData) {
    const { data } = await api.put("/user", userData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return data;
  },
};

export default userService;

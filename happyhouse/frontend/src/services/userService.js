import api from ".";

const userService = {
  async fetchUserInfo() {
    const { data } = await api.get("/user");
    return data;
  },
  async updateUser(userData) {
    const { data } = await api.put("/user", userData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return data;
  },
};

export default userService;

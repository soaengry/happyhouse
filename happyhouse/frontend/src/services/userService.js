import api from ".";

const userService = {
  async register(userData) {
    const { data } = await api.post("/user", userData);
    return data;
  },
  async checkUsername(username) {
    const { data } = await api.post("/user/exist", { username });
    return data; // true면 존재함
  },
  async login({ username, password }) {
    const { data } = await api.post("/login", { username, password });
    return data; // { accessToken, refreshToken }
  },
  async fetchUserInfo() {
    const { data } = await api.get("/user");
    return data;
  },
  async updateUser(userData) {
    const { data } = await api.put("/user", userData);
    return data;
  },
};

export default userService;

import api from ".";

const boardService = {
  async getBoardList(params) {
    const { data } = await api.get("/board", { params });
    return data; // { list, count }
  },

  async getBoard(id) {
    const { data } = await api.get(`/board/${id}`);
    return data;
  },

  async createBoard(formData) {
    const { data } = await api.post("/board", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return data;
  },

  async updateBoard(id, formData) {
    await api.put(`/board/${id}`, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
  },

  async deleteBoard(id) {
    await api.delete(`/board/${id}`);
  },

  getFileUrl(fileName) {
    return `/api/board/files/${fileName}`;
  },

  /* 댓글 */
  async createReply(boardId, content) {
    const { data } = await api.post("/board/reply", { boardId, content });
    return data;
  },

  async updateReply(id, content) {
    await api.put(`/board/reply/${id}`, { content });
  },

  async deleteReply(id) {
    await api.delete(`/board/reply/${id}`);
  },
};

export default boardService;

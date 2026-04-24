import { defineStore } from "pinia";
import boardService from "@/services/boardService";

export const useBoardStore = defineStore("board", {
  state: () => ({
    boardList: [],
    totalCount: 0,
    currentBoard: null,
    isLoading: false,
  }),

  actions: {
    async fetchBoardList(params) {
      this.isLoading = true;
      try {
        const { list, count } = await boardService.getBoardList(params);
        this.boardList  = list;
        this.totalCount = count;
      } finally {
        this.isLoading = false;
      }
    },

    async fetchBoard(id) {
      this.isLoading = true;
      try {
        this.currentBoard = await boardService.getBoard(id);
      } finally {
        this.isLoading = false;
      }
    },

    async createBoard(formData) {
      return boardService.createBoard(formData);
    },

    async updateBoard(id, formData) {
      return boardService.updateBoard(id, formData);
    },

    async deleteBoard(id) {
      await boardService.deleteBoard(id);
    },

    async createReply(boardId, content) {
      const reply = await boardService.createReply(boardId, content);
      if (this.currentBoard) {
        this.currentBoard.replyList.push(reply);
      }
      return reply;
    },

    async updateReply(id, content) {
      await boardService.updateReply(id, content);
      if (this.currentBoard) {
        const r = this.currentBoard.replyList.find((r) => r.id === id);
        if (r) r.content = content;
      }
    },

    async deleteReply(id) {
      await boardService.deleteReply(id);
      if (this.currentBoard) {
        this.currentBoard.replyList = this.currentBoard.replyList.filter(
          (r) => r.id !== id,
        );
      }
    },
  },
});

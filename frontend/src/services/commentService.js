import api from './api';

export const commentService = {
  getRecipeComments: async (recipeId) => {
    const response = await api.get(`/recipes/${recipeId}/comments`);
    return response.data;
  },

  create: async (recipeId, text) => {
    const response = await api.post(`/recipes/${recipeId}/comments`, { text });
    return response.data;
  },

  delete: async (commentId) => {
    const response = await api.delete(`/comments/${commentId}`);
    return response.data;
  },
};

import api from './api';

export const ratingService = {
  getRecipeRating: async (recipeId) => {
    const response = await api.get(`/recipes/${recipeId}/rating`);
    return response.data;
  },

  rateRecipe: async (recipeId, rating) => {
    const response = await api.post(`/recipes/${recipeId}/rate`, { rating });
    return response.data;
  },
};

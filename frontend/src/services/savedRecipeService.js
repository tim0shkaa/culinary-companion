import api from './api';

export const savedRecipeService = {
  getSavedRecipes: async () => {
    const response = await api.get('/user/saved-recipes');
    return response.data;
  },

  saveRecipe: async (recipeId) => {
    const response = await api.post(`/recipes/${recipeId}/save`);
    return response.data;
  },

  unsaveRecipe: async (recipeId) => {
    const response = await api.delete(`/recipes/${recipeId}/save`);
    return response.data;
  },

  isSaved: async (recipeId) => {
    const response = await api.get(`/recipes/${recipeId}/is-saved`);
    return response.data.saved;
  },
};

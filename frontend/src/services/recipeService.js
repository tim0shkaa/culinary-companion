import api from './api';

export const recipeService = {
  getAll: async (search = '', category = '') => {
    const params = new URLSearchParams();
    if (search) params.append('search', search);
    if (category) params.append('category', category);
    
    const response = await api.get(`/recipes?${params.toString()}`);
    return response.data;
  },

  getById: async (id) => {
    const response = await api.get(`/recipes/${id}`);
    return response.data;
  },

  create: async (recipeData) => {
    const response = await api.post('/recipes', recipeData);
    return response.data;
  },

  update: async (id, recipeData) => {
    const response = await api.put(`/recipes/${id}`, recipeData);
    return response.data;
  },

  delete: async (id) => {
    const response = await api.delete(`/recipes/${id}`);
    return response.data;
  },

  getUserRecipes: async () => {
    const response = await api.get('/user/recipes');
    return response.data;
  },

  addToMyRecipes: async (id) => {
    const response = await api.post(`/recipes/${id}/add`);
    return response.data;
  },
};

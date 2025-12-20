import api from './api';

export const ingredientService = {
  getAll: async () => {
    const response = await api.get('/ingredients');
    return response.data;
  },

  search: async (query) => {
    const response = await api.get(`/ingredients/search?q=${query}`);
    return response.data;
  },
};

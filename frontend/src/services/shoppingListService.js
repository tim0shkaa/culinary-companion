import api from './api';

export const shoppingListService = {
  generateFromMealPlan: async (mealPlanId) => {
    const response = await api.post(`/mealplans/${mealPlanId}/shopping-list`);
    return response.data;
  },

  getById: async (id) => {
    const response = await api.get(`/shopping-lists/${id}`);
    return response.data;
  },

  update: async (id, items) => {
    const response = await api.put(`/shopping-lists/${id}`, { items });
    return response.data;
  },

  delete: async (id) => {
    const response = await api.delete(`/shopping-lists/${id}`);
    return response.data;
  },
};

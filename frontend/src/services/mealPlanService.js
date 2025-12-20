import api from './api';

export const mealPlanService = {
  getAll: async () => {
    const response = await api.get('/mealplans');
    return response.data;
  },

  getById: async (id) => {
    const response = await api.get(`/mealplans/${id}`);
    return response.data;
  },

  create: async (mealPlanData) => {
    const response = await api.post('/mealplans', mealPlanData);
    return response.data;
  },

  delete: async (id) => {
    const response = await api.delete(`/mealplans/${id}`);
    return response.data;
  },

  getEntries: async (mealPlanId) => {
    const response = await api.get(`/mealplans/${mealPlanId}/entries`);
    return response.data;
  },

  addEntry: async (mealPlanId, entryData) => {
    const response = await api.post(`/mealplans/${mealPlanId}/entries`, entryData);
    return response.data;
  },

  deleteEntry: async (mealPlanId, entryId) => {
    const response = await api.delete(`/mealplans/${mealPlanId}/entries/${entryId}`);
    return response.data;
  },

  getDayNutrition: async (mealPlanId, date) => {
    const response = await api.get(`/mealplans/${mealPlanId}/nutrition?date=${date}`);
    return response.data;
  },
};

import api from './api';

export const mealPlanService = {
  getWeekEntries: async (startDate) => {
    const response = await api.get(`/meal-planning/week?startDate=${startDate}`);
    return response.data;
  },

  getDayEntries: async (date) => {
    const response = await api.get(`/meal-planning/day?date=${date}`);
    return response.data;
  },

  addEntry: async (entryData) => {
    const response = await api.post('/meal-planning/entries', entryData);
    return response.data;
  },

  deleteEntry: async (entryId) => {
    const response = await api.delete(`/meal-planning/entries/${entryId}`);
    return response.data;
  },

  getDayNutrition: async (date) => {
    const response = await api.get(`/meal-planning/nutrition?date=${date}`);
    return response.data;
  },
};

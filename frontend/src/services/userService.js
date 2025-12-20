import api from './api';

export const userService = {
  getCurrentUser: async () => {
    const response = await api.get('/user/profile');
    return response.data;
  },

  getUserStats: async () => {
    const response = await api.get('/user/stats');
    return response.data;
  },

  updateProfile: async (userData) => {
    const response = await api.put('/user/profile', userData);
    return response.data;
  },

  deleteProfile: async () => {
    const response = await api.delete('/user/profile');
    return response.data;
  },
};

import { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/authService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const currentUser = authService.getCurrentUser();
    const token = localStorage.getItem('token');
    
    console.log('AuthContext initialized:');
    console.log('- User:', currentUser);
    console.log('- Token exists:', !!token);
    
    setUser(currentUser);
    setLoading(false);
  }, []);

  const login = async (credentials) => {
    const data = await authService.login(credentials);
    const userData = {
      id: data.userId,
      username: data.username,
      email: data.email,
      role: data.role,
    };
    console.log('Login successful, user:', userData);
    setUser(userData);
    return data;
  };

  const register = async (userData) => {
    const data = await authService.register(userData);
    return data;
  };

  const logout = async () => {
    await authService.logout();
    setUser(null);
    console.log('Logged out');
  };

  const isAuthenticated = !!user && !!localStorage.getItem('token');
  
  console.log('AuthContext render - isAuthenticated:', isAuthenticated, 'user:', user);

  const value = {
    user,
    login,
    register,
    logout,
    isAuthenticated,
    loading,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};

import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Layout from './components/layout/Layout';
import ProtectedRoute from './components/common/ProtectedRoute';

import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import RecipesPage from './pages/RecipesPage';
import RecipeDetailPage from './pages/RecipeDetailPage';
import RecipeCreatePage from './pages/RecipeCreatePage';
import MealPlanPage from './pages/MealPlanPage';
import ProfilePage from './pages/ProfilePage';
import MealPlanDetailPage from './pages/MealPlanDetailPage';
import MealPlanCreatePage from './pages/MealPlanCreatePage';

import './App.css';

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route element={<Layout />}>
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            
            <Route path="/recipes" element={<RecipesPage />} />
            <Route path="/recipes/:id" element={<RecipeDetailPage />} />
            
            <Route
              path="/recipes/create"
              element={
                <ProtectedRoute>
                  <RecipeCreatePage />
                </ProtectedRoute>
              }
            />
            
            <Route
              path="/mealplans"
              element={
                <ProtectedRoute>
                  <MealPlanPage />
                </ProtectedRoute>
              }
            />

            <Route
              path="/mealplans/create"
              element={
                <ProtectedRoute>
                  <MealPlanCreatePage />
                </ProtectedRoute>
              }
            />

            <Route path="/mealplans/:id" element={<ProtectedRoute><MealPlanDetailPage /></ProtectedRoute>} />
            
            <Route
              path="/profile"
              element={
                <ProtectedRoute>
                  <ProfilePage />
                </ProtectedRoute>
              }
            />
          </Route>
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;

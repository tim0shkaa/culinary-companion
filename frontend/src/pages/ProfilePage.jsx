import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';
import { userService } from '../services/userService';
import { recipeService } from '../services/recipeService';
import { savedRecipeService } from '../services/savedRecipeService';

const ProfilePage = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('profile');
  
  const [stats, setStats] = useState(null);
  const [myRecipes, setMyRecipes] = useState([]);
  const [savedRecipes, setSavedRecipes] = useState([]);
  const [loading, setLoading] = useState(false);

  const categories = {
    'ЗАВТРАК': 'Завтрак',
    'ОБЕД': 'Обед',
    'УЖИН': 'Ужин',
    'ДЕСЕРТ': 'Десерт',
    'ПЕРЕКУС': 'Перекус',
    'ЗАКУСКА': 'Закуска',
    'СУП': 'Суп',
    'САЛАТ': 'Салат',
  };

  useEffect(() => {
    if (activeTab === 'myRecipes' && myRecipes.length === 0) {
      fetchMyRecipes();
      fetchStats();
    } else if (activeTab === 'saved' && savedRecipes.length === 0) {
      fetchSavedRecipes();
    }
  }, [activeTab]);

  const fetchStats = async () => {
    try {
      const data = await userService.getUserStats();
      setStats(data);
    } catch (err) {
      console.error('Ошибка загрузки статистики');
    }
  };

  const fetchMyRecipes = async () => {
    setLoading(true);
    try {
      const data = await recipeService.getUserRecipes();
      setMyRecipes(data);
    } catch (err) {
      console.error('Ошибка загрузки рецептов');
    } finally {
      setLoading(false);
    }
  };

  const fetchSavedRecipes = async () => {
    setLoading(true);
    try {
      const data = await savedRecipeService.getSavedRecipes();
      setSavedRecipes(data);
    } catch (err) {
      console.error('Ошибка загрузки избранного');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  const handleUnsave = async (recipeId) => {
    try {
      await savedRecipeService.unsaveRecipe(recipeId);
      setSavedRecipes(savedRecipes.filter(r => r.id !== recipeId));
    } catch (err) {
      alert('Ошибка удаления из избранного');
    }
  };

  if (!user) return null;

  return (
    <div className="profile-page">
      <h1>Профиль ({user.username})</h1>

      <div className="profile-tabs">
        <button
          className={`tab ${activeTab === 'profile' ? 'active' : ''}`}
          onClick={() => setActiveTab('profile')}
        >
          Профиль
        </button>
        <button
          className={`tab ${activeTab === 'myRecipes' ? 'active' : ''}`}
          onClick={() => setActiveTab('myRecipes')}
        >
          Мои рецепты
        </button>
        <button
          className={`tab ${activeTab === 'saved' ? 'active' : ''}`}
          onClick={() => setActiveTab('saved')}
        >
          Избранное
        </button>
        <button
          className={`tab ${activeTab === 'mealPlans' ? 'active' : ''}`}
          onClick={() => setActiveTab('mealPlans')}
        >
          Планы питания
        </button>
        <button
          className={`tab ${activeTab === 'shopping' ? 'active' : ''}`}
          onClick={() => setActiveTab('shopping')}
        >
          Список покупок
        </button>
      </div>

      <div className="tab-content">
        {activeTab === 'profile' && (
          <div className="profile-info">
            <div className="info-row">
              <label>Имя пользователя:</label>
              <span>{user.username}</span>
            </div>

            <div className="info-row">
              <label>Email:</label>
              <span>{user.email}</span>
            </div>

            <div className="info-row">
              <label>Роль:</label>
              <span>{user.role === 'USER' ? 'Пользователь' : user.role}</span>
            </div>

            <div className="profile-actions">
              <button onClick={handleLogout} className="btn btn-danger">
                Выйти
              </button>
            </div>
          </div>
        )}

        {activeTab === 'myRecipes' && (
          <div className="my-recipes-tab">
            {stats && (
              <div className="stats-card">
                <h3>Статистика</h3>
                <div className="stats-grid">
                  <div className="stat">
                    <span className="stat-value">{stats.recipesCount}</span>
                    <span className="stat-label">Рецептов создано</span>
                  </div>
                  <div className="stat">
                    <span className="stat-value">
                      {stats.averageRating ? stats.averageRating.toFixed(1) : '0.0'}
                    </span>
                    <span className="stat-label">Средний рейтинг</span>
                  </div>
                  <div className="stat">
                    <span className="stat-value">{stats.savedRecipesCount}</span>
                    <span className="stat-label">В избранном</span>
                  </div>
                </div>
              </div>
            )}

            {loading ? (
              <div className="loading">Загрузка...</div>
            ) : myRecipes.length === 0 ? (
              <div className="empty-state">
                <p>У вас пока нет рецептов</p>
                <Link to="/recipes/create" className="btn btn-primary">
                  Создать первый рецепт
                </Link>
              </div>
            ) : (
              <div className="recipes-grid">
                {myRecipes.map((recipe) => (
                  <Link to={`/recipes/${recipe.id}`} key={recipe.id} className="recipe-card">
                    {recipe.imageUrl && (
                      <img src={recipe.imageUrl} alt={recipe.title} />
                    )}
                    <div className="recipe-card-content">
                      <h3>{recipe.title}</h3>
                      <p className="recipe-category">{categories[recipe.category]}</p>
                      <div className="recipe-meta">
                        <span>⏱️ {recipe.prepTime + recipe.cookTime} мин</span>
                        <span>🍽️ {recipe.servings} порций</span>
                        {recipe.averageRating && (
                          <span>⭐ {recipe.averageRating.toFixed(1)}</span>
                        )}
                      </div>
                    </div>
                  </Link>
                ))}
              </div>
            )}
          </div>
        )}

        {activeTab === 'saved' && (
          <div className="saved-recipes-tab">
            {loading ? (
              <div className="loading">Загрузка...</div>
            ) : savedRecipes.length === 0 ? (
              <div className="empty-state">
                <p>У вас пока нет избранных рецептов</p>
                <Link to="/recipes" className="btn btn-primary">
                  Найти рецепты
                </Link>
              </div>
            ) : (
              <div className="recipes-grid">
                {savedRecipes.map((recipe) => (
                  <div key={recipe.id} className="recipe-card">
                    <Link to={`/recipes/${recipe.id}`}>
                      {recipe.imageUrl && (
                        <img src={recipe.imageUrl} alt={recipe.title} />
                      )}
                      <div className="recipe-card-content">
                        <h3>{recipe.title}</h3>
                        <p className="recipe-category">{categories[recipe.category]}</p>
                        <div className="recipe-meta">
                          <span>⏱️ {recipe.prepTime + recipe.cookTime} мин</span>
                          <span>🍽️ {recipe.servings} порций</span>
                          {recipe.averageRating && (
                            <span>⭐ {recipe.averageRating.toFixed(1)}</span>
                          )}
                        </div>
                      </div>
                    </Link>
                    <button
                      onClick={() => handleUnsave(recipe.id)}
                      className="btn btn-remove"
                    >
                      Удалить из избранного
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {activeTab === 'mealPlans' && (
          <div className="mealplans-tab">
            <p>Планы питания - в разработке</p>
            <Link to="/meal-planning" className="btn btn-primary">
              Перейти к планам питания
            </Link>
          </div>
        )}

        {activeTab === 'shopping' && (
          <div className="shopping-tab">
            <p>Список покупок - в разработке</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProfilePage;

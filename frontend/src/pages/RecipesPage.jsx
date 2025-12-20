import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { recipeService } from '../services/recipeService';

const RecipesPage = () => {
  const [recipes, setRecipes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');
  const [category, setCategory] = useState('');

  const categories = [
    { value: 'ЗАВТРАК', label: 'Завтрак' },
    { value: 'ОБЕД', label: 'Обед' },
    { value: 'УЖИН', label: 'Ужин' },
    { value: 'ДЕСЕРТ', label: 'Десерт' },
    { value: 'ПЕРЕКУС', label: 'Перекус' },
    { value: 'ЗАКУСКА', label: 'Закуска' },
    { value: 'СУП', label: 'Суп' },
    { value: 'САЛАТ', label: 'Салат' },
  ];

  useEffect(() => {
    fetchRecipes();
  }, [search, category]);

  const fetchRecipes = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await recipeService.getAll(search, category);
      setRecipes(data);
    } catch (err) {
      setError('Ошибка загрузки рецептов');
    } finally {
      setLoading(false);
    }
  };

  const handleSearchChange = (e) => {
    setSearch(e.target.value);
  };

  const handleCategoryChange = (e) => {
    setCategory(e.target.value);
  };

  const getCategoryLabel = (catValue) => {
    const cat = categories.find(c => c.value === catValue);
    return cat ? cat.label : catValue;
  };

  return (
    <div className="recipes-page">
      <div className="page-header">
        <h1>Рецепты</h1>
        <Link to="/recipes/create" className="btn btn-primary">
          Создать рецепт
        </Link>
      </div>

      <div className="filters">
        <input
          type="text"
          placeholder="Поиск рецептов..."
          value={search}
          onChange={handleSearchChange}
          className="search-input"
        />
        
        <select value={category} onChange={handleCategoryChange} className="category-select">
          <option value="">Все категории</option>
          {categories.map((cat) => (
            <option key={cat.value} value={cat.value}>
              {cat.label}
            </option>
          ))}
        </select>
      </div>

      {error && <div className="error-message">{error}</div>}

      {loading ? (
        <div className="loading">Загрузка...</div>
      ) : (
        <div className="recipes-grid">
          {recipes.length === 0 ? (
            <p>Рецепты не найдены</p>
          ) : (
            recipes.map((recipe) => (
              <Link to={`/recipes/${recipe.id}`} key={recipe.id} className="recipe-card">
                {recipe.imageUrl && (
                  <img src={recipe.imageUrl} alt={recipe.title} />
                )}
                <div className="recipe-card-content">
                  <h3>{recipe.title}</h3>
                  <p className="recipe-category">{getCategoryLabel(recipe.category)}</p>
                  <div className="recipe-meta">
                    <span>⏱️ {recipe.prepTime + recipe.cookTime} мин</span>
                    <span>🍽️ {recipe.servings} порций</span>
                    {recipe.averageRating && (
                      <span>⭐ {recipe.averageRating.toFixed(1)}</span>
                    )}
                  </div>
                </div>
              </Link>
            ))
          )}
        </div>
      )}
    </div>
  );
};

export default RecipesPage;

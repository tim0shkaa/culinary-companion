import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { recipeService } from '../services/recipeService';
import { ingredientService } from '../services/ingredientService';

const RecipeCreatePage = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [availableIngredients, setAvailableIngredients] = useState([]);

  const [formData, setFormData] = useState({
    title: '',
    description: '',
    instructions: '',
    prepTime: '',
    cookTime: '',
    servings: '',
    category: 'ЗАВТРАК',
    imageUrl: '',
    ingredients: [{ ingredientName: '', quantity: '', unit: 'ГРАММ' }],
  });

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

  const units = [
    { value: 'ГРАММ', label: 'грамм' },
    { value: 'МИЛЛИЛИТР', label: 'миллилитр' },
  ];

  useEffect(() => {
    fetchIngredients();
  }, []);

  const fetchIngredients = async () => {
    try {
      const data = await ingredientService.getAll();
      setAvailableIngredients(data);
    } catch (err) {
      console.error('Ошибка загрузки ингредиентов');
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleIngredientChange = (index, field, value) => {
    const newIngredients = [...formData.ingredients];
    newIngredients[index][field] = value;
    setFormData({ ...formData, ingredients: newIngredients });
  };

  const addIngredient = () => {
    setFormData({
      ...formData,
      ingredients: [
        ...formData.ingredients,
        { ingredientName: '', quantity: '', unit: 'ГРАММ' },
      ],
    });
  };

  const removeIngredient = (index) => {
    const newIngredients = formData.ingredients.filter((_, i) => i !== index);
    setFormData({ ...formData, ingredients: newIngredients });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const recipeData = {
        ...formData,
        prepTime: parseInt(formData.prepTime),
        cookTime: parseInt(formData.cookTime),
        servings: parseInt(formData.servings),
        ingredients: formData.ingredients.map((ing) => ({
          ...ing,
          quantity: parseFloat(ing.quantity),
        })),
      };

      const response = await recipeService.create(recipeData);
      navigate(`/recipes/${response.id}`);
    } catch (err) {
      setError('Ошибка создания рецепта');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="recipe-create-page">
      <h1>Создать рецепт</h1>

      {error && <div className="error-message">{error}</div>}

      <form onSubmit={handleSubmit} className="recipe-form">
        <div className="form-group">
          <label>Название рецепта*</label>
          <input
            type="text"
            name="title"
            value={formData.title}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label>Описание*</label>
          <textarea
            name="description"
            value={formData.description}
            onChange={handleChange}
            rows="3"
            required
          />
        </div>

        <div className="form-group">
          <label>Инструкции приготовления*</label>
          <textarea
            name="instructions"
            value={formData.instructions}
            onChange={handleChange}
            rows="6"
            required
          />
        </div>

        <div className="form-row">
          <div className="form-group">
            <label>Время подготовки (мин)*</label>
            <input
              type="number"
              name="prepTime"
              value={formData.prepTime}
              onChange={handleChange}
              min="1"
              required
            />
          </div>

          <div className="form-group">
            <label>Время приготовления (мин)*</label>
            <input
              type="number"
              name="cookTime"
              value={formData.cookTime}
              onChange={handleChange}
              min="1"
              required
            />
          </div>

          <div className="form-group">
            <label>Количество порций*</label>
            <input
              type="number"
              name="servings"
              value={formData.servings}
              onChange={handleChange}
              min="1"
              required
            />
          </div>
        </div>

        <div className="form-group">
          <label>Категория*</label>
          <select name="category" value={formData.category} onChange={handleChange}>
            {categories.map((cat) => (
              <option key={cat.value} value={cat.value}>
                {cat.label}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label>URL изображения</label>
          <input
            type="url"
            name="imageUrl"
            value={formData.imageUrl}
            onChange={handleChange}
          />
        </div>

        <div className="form-group">
          <label>Ингредиенты*</label>
          {formData.ingredients.map((ingredient, index) => (
            <div key={index} className="ingredient-row">
              <select
                value={ingredient.ingredientName}
                onChange={(e) =>
                  handleIngredientChange(index, 'ingredientName', e.target.value)
                }
                required
              >
                <option value="">Выберите ингредиент</option>
                {availableIngredients.map((ing) => (
                  <option key={ing.id} value={ing.name}>
                    {ing.name}
                  </option>
                ))}
              </select>

              <input
                type="number"
                placeholder="Количество"
                value={ingredient.quantity}
                onChange={(e) =>
                  handleIngredientChange(index, 'quantity', e.target.value)
                }
                min="0.1"
                step="0.1"
                required
              />

              <select
                value={ingredient.unit}
                onChange={(e) => handleIngredientChange(index, 'unit', e.target.value)}
              >
                {units.map((unit) => (
                  <option key={unit.value} value={unit.value}>
                    {unit.label}
                  </option>
                ))}
              </select>

              {formData.ingredients.length > 1 && (
                <button
                  type="button"
                  onClick={() => removeIngredient(index)}
                  className="btn btn-remove"
                >
                  ✕
                </button>
              )}
            </div>
          ))}

          <button type="button" onClick={addIngredient} className="btn btn-secondary">
            + Добавить ингредиент
          </button>
        </div>

        <div className="form-actions">
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Создание...' : 'Создать рецепт'}
          </button>
          <button
            type="button"
            onClick={() => navigate('/recipes')}
            className="btn btn-secondary"
          >
            Отмена
          </button>
        </div>
      </form>
    </div>
  );
};

export default RecipeCreatePage;
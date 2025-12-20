import { useState, useEffect } from 'react';
import { mealPlanService } from '../services/mealPlanService';
import { recipeService } from '../services/recipeService';

const DayMealModal = ({ mealPlanId, date, entries, onClose }) => {
  const [recipes, setRecipes] = useState([]);
  const [nutrition, setNutrition] = useState(null);
  const [selectedMealType, setSelectedMealType] = useState(null);
  const [selectedRecipe, setSelectedRecipe] = useState('');
  const [loading, setLoading] = useState(false);

  const mealTypes = ['ЗАВТРАК', 'ОБЕД', 'УЖИН', 'ПЕРЕКУС'];
  const mealTypeLabels = {
    'ЗАВТРАК': 'Завтрак',
    'ОБЕД': 'Обед',
    'УЖИН': 'Ужин',
    'ПЕРЕКУС': 'Перекус',
  };

  const dateStr = date.toISOString().split('T')[0];

  useEffect(() => {
    fetchRecipes();
    fetchNutrition();
  }, []);

  const fetchRecipes = async () => {
    try {
      const data = await recipeService.getAll();
      setRecipes(data);
    } catch (err) {
      console.error('Ошибка загрузки рецептов');
    }
  };

  const fetchNutrition = async () => {
    try {
      const data = await mealPlanService.getDayNutrition(mealPlanId, dateStr);
      setNutrition(data);
    } catch (err) {
      console.error('Ошибка загрузки КБЖУ');
    }
  };

  const handleAddRecipe = async () => {
    if (!selectedMealType || !selectedRecipe) {
      alert('Выберите тип приёма пищи и рецепт');
      return;
    }

    setLoading(true);
    try {
      await mealPlanService.addEntry(mealPlanId, {
        recipeId: parseInt(selectedRecipe),
        mealDate: dateStr,
        mealType: selectedMealType,
      });
      
      setSelectedMealType(null);
      setSelectedRecipe('');
      onClose();
    } catch (err) {
      alert('Ошибка добавления рецепта');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteEntry = async (entryId) => {
    if (window.confirm('Удалить рецепт из этого приёма пищи?')) {
      try {
        await mealPlanService.deleteEntry(mealPlanId, entryId);
        onClose();
      } catch (err) {
        alert('Ошибка удаления');
      }
    }
  };

  const getMealEntries = (mealType) => {
    return entries.filter(e => e.mealType === mealType);
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>{date.toLocaleDateString('ru-RU', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}</h2>
          <button onClick={onClose} className="modal-close">✕</button>
        </div>

        <div className="modal-body">
          {nutrition && (
            <div className="nutrition-summary">
              <h3>КБЖУ за день:</h3>
              <div className="nutrition-grid">
                <div className="nutrition-item">
                  <span className="nutrition-label">Белки:</span>
                  <span className="nutrition-value">{nutrition.totalProteins?.toFixed(1)} г</span>
                </div>
                <div className="nutrition-item">
                  <span className="nutrition-label">Жиры:</span>
                  <span className="nutrition-value">{nutrition.totalFats?.toFixed(1)} г</span>
                </div>
                <div className="nutrition-item">
                  <span className="nutrition-label">Углеводы:</span>
                  <span className="nutrition-value">{nutrition.totalCarbs?.toFixed(1)} г</span>
                </div>
                <div className="nutrition-item">
                  <span className="nutrition-label">Калории:</span>
                  <span className="nutrition-value">{nutrition.totalCalories} ккал</span>
                </div>
              </div>
            </div>
          )}

          {mealTypes.map(mealType => (
            <div key={mealType} className="meal-section">
              <h3>{mealTypeLabels[mealType]}</h3>
              
              <div className="meal-recipes">
                {getMealEntries(mealType).length === 0 ? (
                  <p className="empty-meal">Рецептов нет</p>
                ) : (
                  getMealEntries(mealType).map(entry => (
                    <div key={entry.id} className="meal-recipe-item">
                      <span>{entry.recipeTitle}</span>
                      <button
                        onClick={() => handleDeleteEntry(entry.id)}
                        className="btn-small btn-danger"
                      >
                        ✕
                      </button>
                    </div>
                  ))
                )}
              </div>

              <button
                onClick={() => setSelectedMealType(mealType)}
                className="btn btn-secondary btn-small"
              >
                + Добавить рецепт
              </button>
            </div>
          ))}

          {selectedMealType && (
            <div className="add-recipe-form">
              <h3>Добавить рецепт в {mealTypeLabels[selectedMealType]}</h3>
              
              <select
                value={selectedRecipe}
                onChange={(e) => setSelectedRecipe(e.target.value)}
                className="recipe-select"
              >
                <option value="">Выберите рецепт</option>
                {recipes.map(recipe => (
                  <option key={recipe.id} value={recipe.id}>
                    {recipe.title}
                  </option>
                ))}
              </select>

              <div className="form-actions">
                <button
                  onClick={handleAddRecipe}
                  className="btn btn-primary"
                  disabled={loading || !selectedRecipe}
                >
                  {loading ? 'Добавление...' : 'Добавить'}
                </button>
                <button
                  onClick={() => {
                    setSelectedMealType(null);
                    setSelectedRecipe('');
                  }}
                  className="btn btn-secondary"
                >
                  Отмена
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default DayMealModal;

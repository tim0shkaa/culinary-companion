import { useState, useEffect } from 'react';
import { mealPlanService } from '../services/mealPlanService';
import { recipeService } from '../services/recipeService';

const DayMealModal = ({ date, onClose }) => {
  const [entries, setEntries] = useState([]);
  const [recipes, setRecipes] = useState([]);
  const [nutrition, setNutrition] = useState(null);
  const [loading, setLoading] = useState(true);

  const mealTypes = ['ЗАВТРАК', 'ОБЕД', 'УЖИН', 'ПЕРЕКУС'];

  useEffect(() => {
    fetchData();
  }, [date]);

  const fetchData = async () => {
  try {
    setLoading(true);
    const dateStr = date.toISOString().split('T')[0];
    
    const [dayEntries, allRecipes, dayNutrition] = await Promise.all([
      mealPlanService.getDayEntries(dateStr),
      recipeService.getAll(),
      mealPlanService.getDayNutrition(dateStr)
    ]);

    console.log('DEBUG: dayEntries =', dayEntries);     // ДОБАВЬ
    console.log('DEBUG: allRecipes =', allRecipes);     // ДОБАВЬ
    console.log('DEBUG: dayNutrition =', dayNutrition); // ДОБАВЬ

    setEntries(dayEntries || []);
    setRecipes(allRecipes || []);
    setNutrition(dayNutrition);
  } catch (err) {
    console.error('Ошибка загрузки данных:', err);
  } finally {
    setLoading(false);
  }
};

  const handleAddEntry = async (mealType, recipeId) => {
    try {
      const dateStr = date.toISOString().split('T')[0];
      await mealPlanService.addEntry({
        recipeId: parseInt(recipeId),
        mealDate: dateStr,
        mealType: mealType
      });
      await fetchData();
    } catch (err) {
      console.error('Ошибка добавления записи:', err);
      alert('Не удалось добавить рецепт');
    }
  };

  const handleDeleteEntry = async (entryId) => {
    try {
      await mealPlanService.deleteEntry(entryId);
      await fetchData();
    } catch (err) {
      console.error('Ошибка удаления записи:', err);
      alert('Не удалось удалить рецепт');
    }
  };

  const getEntriesByMealType = (mealType) => {
    return entries.filter(e => e.mealType === mealType);
  };

  if (loading) return <div className="modal-overlay"><div className="loading">Загрузка...</div></div>;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content day-meal-modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>{date.toLocaleDateString('ru-RU', { weekday: 'long', day: 'numeric', month: 'long' })}</h2>
          <button className="close-btn" onClick={onClose}>×</button>
        </div>

        <div className="modal-body">
          {nutrition && (
            <div className="day-nutrition">
              <h3>КБЖУ за день:</h3>
              <div className="nutrition-grid">
                <div className="nutrition-item">
                  <span className="label">Белки:</span>
                  <span className="value">{nutrition.totalProteins?.toFixed(1) || 0} г</span>
                </div>
                <div className="nutrition-item">
                  <span className="label">Жиры:</span>
                  <span className="value">{nutrition.totalFats?.toFixed(1) || 0} г</span>
                </div>
                <div className="nutrition-item">
                  <span className="label">Углеводы:</span>
                  <span className="value">{nutrition.totalCarbohydrates?.toFixed(1) || 0} г</span>
                </div>
                <div className="nutrition-item">
                  <span className="label">Калории:</span>
                  <span className="value">{Math.round(nutrition.totalCalories || 0)} ккал</span>
                </div>
              </div>
            </div>
          )}

          <div className="meals-grid">
            {mealTypes.map((mealType) => {
              const mealEntries = getEntriesByMealType(mealType);
              
              return (
                <div key={mealType} className="meal-section">
                  <h4>{mealType}</h4>
                  
                  <div className="meal-entries">
                    {mealEntries.map((entry) => (
                      <div key={entry.id} className="meal-entry">
                        <span>{entry.recipeTitle}</span>
                        <button 
                          className="delete-btn"
                          onClick={() => handleDeleteEntry(entry.id)}
                        >
                          ✕
                        </button>
                      </div>
                    ))}
                  </div>

                  <div className="add-recipe">
                    <select 
                      onChange={(e) => {
                        if (e.target.value) {
                          handleAddEntry(mealType, e.target.value);
                          e.target.value = '';
                        }
                      }}
                      className="recipe-select"
                    >
                      <option value="">+ Добавить рецепт</option>
                      {recipes.map((recipe) => (
                        <option key={recipe.id} value={recipe.id}>
                          {recipe.title}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={onClose}>Закрыть</button>
        </div>
      </div>
    </div>
  );
};

export default DayMealModal;

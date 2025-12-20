import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { mealPlanService } from '../services/mealPlanService';
import { shoppingListService } from '../services/shoppingListService';

const MealPlanPage = () => {
  const [mealPlans, setMealPlans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchMealPlans();
  }, []);

  const fetchMealPlans = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await mealPlanService.getAll();
      setMealPlans(data);
    } catch (err) {
      setError('Ошибка загрузки планов питания');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Удалить этот план питания?')) {
      try {
        await mealPlanService.delete(id);
        fetchMealPlans();
      } catch (err) {
        alert('Ошибка удаления плана питания');
      }
    }
  };

  const handleGenerateShoppingList = async (mealPlanId) => {
    try {
      const shoppingList = await shoppingListService.generateFromMealPlan(mealPlanId);
      alert('Список покупок создан!');
    } catch (err) {
      alert('Ошибка создания списка покупок');
    }
  };

  if (loading) return <div className="loading">Загрузка...</div>;

  return (
    <div className="mealplan-page">
      <div className="page-header">
        <h1>Планы питания</h1>
        <Link to="/mealplans/create" className="btn btn-primary">
          Создать план
        </Link>
      </div>

      {error && <div className="error-message">{error}</div>}

      {mealPlans.length === 0 ? (
        <div className="empty-state">
          <p>У вас пока нет планов питания</p>
        </div>
      ) : (
        <div className="mealplans-grid">
          {mealPlans.map((plan) => (
            <div key={plan.id} className="mealplan-card">
              <h3>{plan.name}</h3>
              <p className="mealplan-dates">
                {new Date(plan.startDate).toLocaleDateString()} -{' '}
                {new Date(plan.endDate).toLocaleDateString()}
              </p>
              <p className="mealplan-recipes">
                Рецептов: {plan.recipes?.length || 0}
              </p>
              <div className="mealplan-actions">
                <Link to={`/mealplans/${plan.id}`} className="btn btn-secondary">
                  Просмотр
                </Link>
                <button
                  onClick={() => handleGenerateShoppingList(plan.id)}
                  className="btn btn-primary"
                >
                  Создать список покупок
                </button>
                <button
                  onClick={() => handleDelete(plan.id)}
                  className="btn btn-danger"
                >
                  Удалить
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default MealPlanPage;
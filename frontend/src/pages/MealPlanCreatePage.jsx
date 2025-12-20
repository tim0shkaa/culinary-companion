import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { mealPlanService } from '../services/mealPlanService';

const MealPlanCreatePage = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [formData, setFormData] = useState({
    name: '',
    startDate: '',
    endDate: '',
  });

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const plan = await mealPlanService.create(formData);
      navigate(`/mealplans/${plan.id}`);
    } catch (err) {
      setError('Ошибка создания плана питания');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="mealplan-create-page">
      <h1>Создать план питания</h1>

      {error && <div className="error-message">{error}</div>}

      <form onSubmit={handleSubmit} className="mealplan-form">
        <div className="form-group">
          <label>Название плана*</label>
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
            placeholder="Например: План на неделю"
            required
          />
        </div>

        <div className="form-group">
          <label>Дата начала*</label>
          <input
            type="date"
            name="startDate"
            value={formData.startDate}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label>Дата окончания*</label>
          <input
            type="date"
            name="endDate"
            value={formData.endDate}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-actions">
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Создание...' : 'Создать план'}
          </button>
          <button
            type="button"
            onClick={() => navigate('/mealplans')}
            className="btn btn-secondary"
          >
            Отмена
          </button>
        </div>
      </form>
    </div>
  );
};

export default MealPlanCreatePage;

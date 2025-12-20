import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import { mealPlanService } from '../services/mealPlanService';
import DayMealModal from '../components/DayMealModal';

const MealPlanDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  
  const [mealPlan, setMealPlan] = useState(null);
  const [entries, setEntries] = useState([]);
  const [selectedDate, setSelectedDate] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchMealPlan();
    fetchEntries();
  }, [id]);

  const fetchMealPlan = async () => {
    try {
      const data = await mealPlanService.getById(id);
      setMealPlan(data);
    } catch (err) {
      setError('Ошибка загрузки плана питания');
    } finally {
      setLoading(false);
    }
  };

  const fetchEntries = async () => {
    try {
      const data = await mealPlanService.getEntries(id);
      setEntries(data);
    } catch (err) {
      console.error('Ошибка загрузки записей');
    }
  };

  const handleDateClick = (date) => {
    setSelectedDate(date);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedDate(null);
    fetchEntries();
  };

  const handleDeletePlan = async () => {
    if (window.confirm('Удалить этот план питания?')) {
      try {
        await mealPlanService.delete(id);
        navigate('/mealplans');
      } catch (err) {
        alert('Ошибка удаления плана');
      }
    }
  };

  const getTileContent = ({ date, view }) => {
    if (view !== 'month') return null;
    
    const dateStr = date.toISOString().split('T')[0];
    const dayEntries = entries.filter(e => e.mealDate === dateStr);
    
    if (dayEntries.length === 0) return null;
    
    return (
      <div className="calendar-tile-content">
        <div className="meal-indicators">
          {dayEntries.map((entry, idx) => (
            <span key={idx} className={`meal-dot meal-${entry.mealType.toLowerCase()}`} />
          ))}
        </div>
      </div>
    );
  };

  if (loading) return <div className="loading">Загрузка...</div>;
  if (error) return <div className="error-message">{error}</div>;
  if (!mealPlan) return <div>План питания не найден</div>;

  return (
    <div className="mealplan-detail-page">
      <div className="page-header">
        <h1>{mealPlan.name}</h1>
        <div className="header-actions">
          <button onClick={handleDeletePlan} className="btn btn-danger">
            Удалить план
          </button>
        </div>
      </div>

      <div className="mealplan-dates">
        <p>
          Период: {new Date(mealPlan.startDate).toLocaleDateString()} -{' '}
          {new Date(mealPlan.endDate).toLocaleDateString()}
        </p>
      </div>

      <div className="calendar-container">
        <Calendar
          onChange={handleDateClick}
          minDate={new Date(mealPlan.startDate)}
          maxDate={new Date(mealPlan.endDate)}
          tileContent={getTileContent}
          locale="ru-RU"
        />
      </div>

      <div className="legend">
        <h3>Легенда:</h3>
        <div className="legend-items">
          <span className="legend-item">
            <span className="meal-dot meal-завтрак" /> Завтрак
          </span>
          <span className="legend-item">
            <span className="meal-dot meal-обед" /> Обед
          </span>
          <span className="legend-item">
            <span className="meal-dot meal-ужин" /> Ужин
          </span>
          <span className="legend-item">
            <span className="meal-dot meal-перекус" /> Перекус
          </span>
        </div>
      </div>

      {showModal && selectedDate && (
        <DayMealModal
          mealPlanId={id}
          date={selectedDate}
          entries={entries.filter(
            e => e.mealDate === selectedDate.toISOString().split('T')[0]
          )}
          onClose={handleCloseModal}
        />
      )}
    </div>
  );
};

export default MealPlanDetailPage;

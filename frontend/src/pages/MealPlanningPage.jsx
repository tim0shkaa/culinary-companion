import { useState, useEffect } from 'react';
import { mealPlanService } from '../services/mealPlanService';
import DayMealModal from '../components/DayMealModal';
import '../styles/mealplan.css';

const MealPlanningPage = () => {
  const [currentWeekStart, setCurrentWeekStart] = useState(getMonday(new Date()));
  const [entries, setEntries] = useState([]);
  const [selectedDate, setSelectedDate] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchWeekEntries();
  }, [currentWeekStart]);

  const fetchWeekEntries = async () => {
    try {
      setLoading(true);
      setError(null);
      const dateStr = currentWeekStart.toISOString().split('T')[0];
      const data = await mealPlanService.getWeekEntries(dateStr);
      setEntries(data || []);
    } catch (err) {
      console.error('Ошибка загрузки записей:', err);
      setError('Не удалось загрузить план питания');
    } finally {
      setLoading(false);
    }
  };

  const handleDateClick = (date) => {
    setSelectedDate(date);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedDate(null);
    fetchWeekEntries();
  };

  const handlePrevWeek = () => {
    const prev = new Date(currentWeekStart);
    prev.setDate(prev.getDate() - 7);
    setCurrentWeekStart(prev);
  };

  const handleNextWeek = () => {
    const next = new Date(currentWeekStart);
    next.setDate(next.getDate() + 7);
    setCurrentWeekStart(next);
  };

  const getWeekDays = () => {
    const days = [];
    for (let i = 0; i < 7; i++) {
      const date = new Date(currentWeekStart);
      date.setDate(date.getDate() + i);
      days.push(date);
    }
    return days;
  };

  const getDayEntries = (date) => {
    const dateStr = date.toISOString().split('T')[0];
    return entries.filter(e => e.mealDate === dateStr);
  };

  const getMealTypeColor = (mealType) => {
    const colors = {
      'ЗАВТРАК': '#FFD93D',
      'ОБЕД': '#6BCF7F',
      'УЖИН': '#4D96FF',
      'ПЕРЕКУС': '#FF6B9D'
    };
    return colors[mealType] || '#ccc';
  };

  if (loading) return <div className="loading">Загрузка...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="meal-planning-page">
      <div className="page-header">
        <h1>Планирование рациона</h1>
      </div>

      <div className="week-navigation">
        <button onClick={handlePrevWeek} className="btn btn-secondary">
          ← Предыдущая неделя
        </button>
        <span className="current-week">
          {currentWeekStart.toLocaleDateString('ru-RU')} - {getWeekDays()[6].toLocaleDateString('ru-RU')}
        </span>
        <button onClick={handleNextWeek} className="btn btn-secondary">
          Следующая неделя →
        </button>
      </div>

      <div className="week-calendar">
        {getWeekDays().map((date, idx) => {
          const dayEntries = getDayEntries(date);
          const isToday = date.toDateString() === new Date().toDateString();

          return (
            <div
              key={idx}
              className={`day-card ${isToday ? 'today' : ''}`}
              onClick={() => handleDateClick(date)}
            >
              <div className="day-header">
                <div className="day-name">
                  {date.toLocaleDateString('ru-RU', { weekday: 'short' })}
                </div>
                <div className="day-date">
                  {date.getDate()}
                </div>
              </div>

              <div className="day-indicators">
                {dayEntries.length === 0 ? (
                  <span className="no-meals">Нет блюд</span>
                ) : (
                  <div className="meal-dots">
                    {[...new Set(dayEntries.map(e => e.mealType))].map((mealType, i) => (
                      <span
                        key={i}
                        className="meal-dot"
                        style={{ backgroundColor: getMealTypeColor(mealType) }}
                        title={mealType}
                      />
                    ))}
                  </div>
                )}
              </div>
            </div>
          );
        })}
      </div>

      <div className="legend">
        <h3>Легенда:</h3>
        <div className="legend-items">
          <span className="legend-item">
            <span className="meal-dot" style={{ backgroundColor: '#FFD93D' }} /> Завтрак
          </span>
          <span className="legend-item">
            <span className="meal-dot" style={{ backgroundColor: '#6BCF7F' }} /> Обед
          </span>
          <span className="legend-item">
            <span className="meal-dot" style={{ backgroundColor: '#4D96FF' }} /> Ужин
          </span>
          <span className="legend-item">
            <span className="meal-dot" style={{ backgroundColor: '#FF6B9D' }} /> Перекус
          </span>
        </div>
      </div>

      {showModal && selectedDate && (
        <DayMealModal
          date={selectedDate}
          onClose={handleCloseModal}
        />
      )}
    </div>
  );
};

function getMonday(date) {
  const d = new Date(date);
  const day = d.getDay();
  const diff = d.getDate() - day + (day === 0 ? -6 : 1);
  return new Date(d.setDate(diff));
}

export default MealPlanningPage;

import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const HomePage = () => {
  const { isAuthenticated, user } = useAuth();

  return (
    <div className="home-page">
      <div className="hero-section">
        <h1>CulinaryCompanion</h1>
        <p>Управляйте рецептами, планируйте меню и создавайте списки покупок</p>
        
        {!isAuthenticated ? (
          <div className="cta-buttons">
            <Link to="/register" className="btn btn-primary">Начать</Link>
            <Link to="/login" className="btn btn-secondary">Войти</Link>
          </div>
        ) : (
          <div className="welcome-section">
            <h2>Добро пожаловать, {user?.username}!</h2>
            <div className="quick-links">
              <Link to="/recipes" className="btn">Рецепты</Link>
              <Link to="/mealplans" className="btn">Планы питания</Link>
              <Link to="/profile" className="btn">Профиль</Link>
            </div>
          </div>
        )}
      </div>

      <div className="features">
        <div className="feature">
          <h3>📖 Управление рецептами</h3>
          <p>Создавайте, сохраняйте и делитесь своими любимыми рецептами</p>
        </div>
        <div className="feature">
          <h3>📅 Планирование меню</h3>
          <p>Планируйте питание на неделю вперёд</p>
        </div>
        <div className="feature">
          <h3>🛒 Списки покупок</h3>
          <p>Автоматическая генерация списка продуктов из плана питания</p>
        </div>
      </div>
    </div>
  );
};

export default HomePage;

import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const Header = () => {
  const { isAuthenticated, user } = useAuth();

  return (
    <header className="header">
      <div className="header-container">
        <Link to="/" className="logo">
          CulinaryCompanion
        </Link>

        <nav className="nav">
          {isAuthenticated ? (
            <>
              <Link to="/recipes">Рецепты</Link>
              <Link to="/meal-planning">Планирование рациона</Link>
              <Link to="/profile">Профиль ({user?.username})</Link>
            </>
          ) : (
            <>
              <Link to="/recipes">Рецепты</Link>
              <Link to="/login">Вход</Link>
              <Link to="/register">Регистрация</Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
};

export default Header;

import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { recipeService } from '../services/recipeService';
import { commentService } from '../services/commentService';
import { ratingService } from '../services/ratingService';
import { savedRecipeService } from '../services/savedRecipeService';
import { useAuth } from '../context/AuthContext';

const RecipeDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  
  const [recipe, setRecipe] = useState(null);
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [commentText, setCommentText] = useState('');
  const [userRating, setUserRating] = useState(0);
  const [hoverRating, setHoverRating] = useState(0);
  const [isSaved, setIsSaved] = useState(false);

  const categories = {
    'ЗАВТРАК': 'Завтрак',
    'ОБЕД': 'Обед',
    'УЖИН': 'Ужин',
    'ДЕСЕРТ': 'Десерт',
    'ПЕРЕКУС': 'Перекус',
    'ЗАКУСКА': 'Закуска',
    'СУП': 'Суп',
    'САЛАТ': 'Салат',
  };

  const units = {
    'ГРАММ': 'г',
    'МИЛЛИЛИТР': 'мл',
  };

  useEffect(() => {
    fetchRecipe();
    fetchComments();
    checkIfSaved();
  }, [id]);

  const fetchRecipe = async () => {
    try {
      const data = await recipeService.getById(id);
      setRecipe(data);
    } catch (err) {
      setError('Ошибка загрузки рецепта');
    } finally {
      setLoading(false);
    }
  };

  const fetchComments = async () => {
    try {
      const data = await commentService.getRecipeComments(id);
      setComments(data);
    } catch (err) {
      console.error('Ошибка загрузки комментариев');
    }
  };

  const checkIfSaved = async () => {
    if (!user) return;
    try {
      const saved = await savedRecipeService.isSaved(id);
      setIsSaved(saved);
    } catch (err) {
      console.error('Ошибка проверки избранного');
    }
  };

  const handleDelete = async () => {
    if (window.confirm('Удалить этот рецепт?')) {
      try {
        await recipeService.delete(id);
        navigate('/recipes');
      } catch (err) {
        alert('Ошибка удаления рецепта');
      }
    }
  };

  const handleAddComment = async (e) => {
    e.preventDefault();
    if (!commentText.trim()) return;

    try {
      await commentService.create(id, commentText);
      setCommentText('');
      fetchComments();
    } catch (err) {
      alert('Ошибка добавления комментария');
    }
  };

  const handleRate = async (rating) => {
    try {
      await ratingService.rateRecipe(id, rating);
      setUserRating(rating);
      fetchRecipe();
    } catch (err) {
      alert('Ошибка оценки рецепта');
    }
  };

  const handleToggleSave = async () => {
    try {
      if (isSaved) {
        await savedRecipeService.unsaveRecipe(id);
        setIsSaved(false);
      } else {
        await savedRecipeService.saveRecipe(id);
        setIsSaved(true);
      }
    } catch (err) {
      alert('Ошибка при добавлении в избранное');
    }
  };

  if (loading) return <div className="loading">Загрузка...</div>;
  if (error) return <div className="error-message">{error}</div>;
  if (!recipe) return <div>Рецепт не найден</div>;

  const isOwner = user?.id === recipe.userId;

  return (
    <div className="recipe-detail-page">
      <div className="recipe-header">
        <h1>{recipe.title}</h1>
        <div className="recipe-actions">
          {user && !isOwner && (
            <button
              onClick={handleToggleSave}
              className={`btn ${isSaved ? 'btn-secondary' : 'btn-primary'}`}
            >
              {isSaved ? '❤️ В избранном' : '🤍 Добавить в избранное'}
            </button>
          )}
          {isOwner && (
            <>
              <Link to={`/recipes/${id}/edit`} className="btn btn-secondary">
                Редактировать
              </Link>
              <button onClick={handleDelete} className="btn btn-danger">
                Удалить
              </button>
            </>
          )}
        </div>
      </div>

      {recipe.imageUrl && (
        <img src={recipe.imageUrl} alt={recipe.title} className="recipe-image" />
      )}

      <div className="recipe-info">
        <p className="recipe-author">Автор: {recipe.username}</p>
        <p className="recipe-category">{categories[recipe.category] || recipe.category}</p>
        <div className="recipe-meta">
          <span>⏱️ Подготовка: {recipe.prepTime} мин</span>
          <span>🔥 Приготовление: {recipe.cookTime} мин</span>
          <span>🍽️ Порций: {recipe.servings}</span>
        </div>
        
        {recipe.averageRating && (
          <div className="rating-display">
            ⭐ {recipe.averageRating.toFixed(1)} ({recipe.ratingCount} оценок)
          </div>
        )}
      </div>

      <div className="recipe-section">
        <h2>Описание</h2>
        <p>{recipe.description}</p>
      </div>

      <div className="recipe-section">
        <h2>Ингредиенты</h2>
        <ul className="ingredients-list">
          {recipe.ingredients?.map((ingredient, index) => (
            <li key={index}>
              {ingredient.name} - {ingredient.quantity} {units[ingredient.unit] || ingredient.unit}
            </li>
          ))}
        </ul>
      </div>

      <div className="recipe-section">
        <h2>Инструкции</h2>
        <p className="instructions">{recipe.instructions}</p>
      </div>

      {user && (
        <div className="recipe-section">
          <h2>Оценить рецепт</h2>
          <div className="rating-stars">
            {[1, 2, 3, 4, 5].map((star) => (
              <button
                key={star}
                onClick={() => handleRate(star)}
                onMouseEnter={() => setHoverRating(star)}
                onMouseLeave={() => setHoverRating(0)}
                className={`star ${(hoverRating || userRating) >= star ? 'active' : ''}`}
              >
                ⭐
              </button>
            ))}
          </div>
        </div>
      )}

      <div className="recipe-section">
        <h2>Комментарии ({comments.length})</h2>
        
        {user && (
          <form onSubmit={handleAddComment} className="comment-form">
            <textarea
              value={commentText}
              onChange={(e) => setCommentText(e.target.value)}
              placeholder="Добавить комментарий..."
              rows="3"
            />
            <button type="submit" className="btn btn-primary">
              Отправить
            </button>
          </form>
        )}

        <div className="comments-list">
          {comments.map((comment) => (
            <div key={comment.id} className="comment">
              <div className="comment-header">
                <strong>{comment.username}</strong>
                <span className="comment-date">
                  {new Date(comment.createdAt).toLocaleDateString()}
                </span>
              </div>
              <p>{comment.text}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default RecipeDetailPage;
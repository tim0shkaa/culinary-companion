import { useState } from 'react';
import { useLocation } from 'react-router-dom';
import '../styles/shoppinglist.css';

const ShoppingListPage = () => {
  const location = useLocation();
  const { items, date } = location.state || { items: [], date: '' };
  const [checkedItems, setCheckedItems] = useState(new Set());

  const handleToggle = (ingredientId) => {
    const newChecked = new Set(checkedItems);
    if (newChecked.has(ingredientId)) {
      newChecked.delete(ingredientId);
    } else {
      newChecked.add(ingredientId);
    }
    setCheckedItems(newChecked);
  };

  const handleCopyList = () => {
    const uncheckedItems = items.filter(item => !checkedItems.has(item.ingredientId));
    const text = uncheckedItems
      .map(item => `${item.ingredientName} - ${item.quantity.toFixed(0)} ${item.unit}`)
      .join('\n');
    
    navigator.clipboard.writeText(text);
    alert('Список скопирован в буфер обмена!');
  };

  const uncheckedCount = items.filter(item => !checkedItems.has(item.ingredientId)).length;

  return (
    <div className="shopping-list-page">
      <div className="page-header">
        <h1>Список покупок</h1>
        <p className="date-info">
          Продукты на {new Date(date).toLocaleDateString('ru-RU', { 
            day: 'numeric', 
            month: 'long',
            year: 'numeric'
          })}
        </p>
      </div>

      <div className="shopping-actions">
        <button 
          className="btn btn-primary" 
          onClick={handleCopyList}
          disabled={uncheckedCount === 0}
        >
          Скопировать список ({uncheckedCount})
        </button>
      </div>

      {items.length === 0 ? (
        <div className="empty-state">
          <p>На выбранный день нет рецептов</p>
        </div>
      ) : (
        <div className="shopping-items">
          {items.map((item) => (
            <div 
              key={item.ingredientId} 
              className={`shopping-item ${checkedItems.has(item.ingredientId) ? 'checked' : ''}`}
            >
              <label className="item-content">
                <input
                  type="checkbox"
                  checked={checkedItems.has(item.ingredientId)}
                  onChange={() => handleToggle(item.ingredientId)}
                />
                <span className="item-name">{item.ingredientName}</span>
                <span className="item-quantity">
                  {item.quantity.toFixed(0)} {item.unit.toLowerCase()}
                </span>
              </label>
            </div>
          ))}
        </div>
      )}

      <div className="shopping-summary">
        <p>Всего продуктов: {items.length}</p>
        <p>Осталось купить: {uncheckedCount}</p>
      </div>
    </div>
  );
};

export default ShoppingListPage;

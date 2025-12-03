INSERT INTO users (email, password_hash, username, role, status) VALUES
('admin@culinary.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIiIkRZqCe', 'admin', 'ADMIN', 'ACTIVE'),
('user@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIiIkRZqCe', 'john_doe', 'USER', 'ACTIVE'),
('chef@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYIiIkRZqCe', 'chef_maria', 'USER', 'ACTIVE');

INSERT INTO ingredients (name) VALUES
('Мука'),
('Сахар'),
('Яйца'),
('Молоко'),
('Масло сливочное'),
('Соль'),
('Курица'),
('Картофель'),
('Лук'),
('Морковь'),
('Помидоры'),
('Огурцы'),
('Рис'),
('Макароны'),
('Сыр'),
('Чеснок'),
('Перец черный'),
('Оливковое масло'),
('Базилик'),
('Петрушка');

INSERT INTO recipes (user_id, title, description, instructions, prep_time, cook_time, servings, calories, category, image_url) VALUES
(2, 'Блины классические', 'Тонкие воздушные блины на молоке', 'Смешать муку, яйца, молоко и соль. Взбить до однородности. Жарить на раскаленной сковороде с каплей масла по 1-2 минуты с каждой стороны.', 10, 20, 4, 150, 'BREAKFAST', NULL),
(2, 'Куриный суп с лапшой', 'Домашний куриный бульон с овощами и лапшой', 'Отварить курицу. Добавить нарезанные овощи (морковь, лук, картофель). Варить 20 минут. Добавить макароны за 7 минут до готовности. Посолить, поперчить.', 15, 40, 6, 180, 'LUNCH', NULL),
(3, 'Салат Цезарь', 'Классический салат с курицей и сыром пармезан', 'Обжарить курицу. Нарезать салат айсберг. Добавить сухарики, пармезан. Заправить соусом Цезарь. Перемешать.', 20, 10, 2, 320, 'SALAD', NULL),
(3, 'Паста Карбонара', 'Итальянская паста с беконом и сливочным соусом', 'Отварить спагетти. Обжарить бекон. Смешать яйца, сыр и сливки. Добавить пасту к бекону, влить яичную смесь. Перемешать на медленном огне.', 10, 15, 2, 450, 'DINNER', NULL);

INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit) VALUES
(1, 1, 200, 'GRAM'),
(1, 3, 2, 'PIECE'),
(1, 4, 500, 'MILLILITER'),
(1, 6, 1, 'PINCH'),
(2, 7, 300, 'GRAM'),
(2, 8, 3, 'PIECE'),
(2, 9, 1, 'PIECE'),
(2, 10, 1, 'PIECE'),
(2, 14, 100, 'GRAM'),
(3, 7, 200, 'GRAM'),
(3, 15, 50, 'GRAM'),
(3, 6, 1, 'PINCH'),
(4, 14, 200, 'GRAM'),
(4, 3, 2, 'PIECE'),
(4, 15, 100, 'GRAM'),
(4, 5, 50, 'GRAM');

INSERT INTO comments (recipe_id, user_id, text) VALUES
(1, 3, 'Отличный рецепт! Блины получились очень вкусными!'),
(2, 2, 'Готовлю этот суп каждую неделю, семья в восторге.'),
(3, 2, 'Очень вкусный салат, спасибо за рецепт!');

INSERT INTO ratings (recipe_id, user_id, rating) VALUES
(1, 3, 5),
(2, 2, 5),
(2, 3, 4),
(3, 2, 5),
(4, 2, 4);

INSERT INTO meal_plans (user_id, name, start_date, end_date) VALUES
(2, 'План на неделю', '2025-12-02', '2025-12-08');

INSERT INTO meal_plan_recipes (meal_plan_id, recipe_id, date, meal_type) VALUES
(1, 1, '2025-12-02', 'BREAKFAST'),
(1, 2, '2025-12-02', 'LUNCH'),
(1, 4, '2025-12-02', 'DINNER'),
(1, 1, '2025-12-03', 'BREAKFAST'),
(1, 3, '2025-12-03', 'LUNCH');

INSERT INTO pantry (user_id, ingredient_id) VALUES
(2, 1),
(2, 6),
(2, 17);

-- Добавление базовых специализаций
INSERT INTO consultant_specialization (id, name) VALUES 
(gen_random_uuid(), 'Маркетинг'),
(gen_random_uuid(), 'Программирование'),
(gen_random_uuid(), 'Недвижимость'),
(gen_random_uuid(), 'Юриспруденция'),
(gen_random_uuid(), 'Бухгалтерия'),
(gen_random_uuid(), 'Дизайн'),
(gen_random_uuid(), 'Медицина'),
(gen_random_uuid(), 'Психология'),
(gen_random_uuid(), 'Образование'),
(gen_random_uuid(), 'Финансы'),
(gen_random_uuid(), 'Логистика'),
(gen_random_uuid(), 'HR'),
(gen_random_uuid(), 'Продажи'),
(gen_random_uuid(), 'Консалтинг'),
(gen_random_uuid(), 'IT-консалтинг')
ON CONFLICT (name) DO NOTHING; 
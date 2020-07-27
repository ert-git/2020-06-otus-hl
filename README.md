# 2020-06-otus-hl

По кнопке Register можно зарегистрироваться. После этого происходит перенаправление на страницу логина, где надо ввести логин и пароль. Для шифрования паролей используется org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.


# Домашнее задание  
## Заготовка для социальной сети  
Цель: В результате выполнения ДЗ вы создадите базовый скелет социальной сети, который будет развиваться в дальнейших ДЗ. В данном задании тренируются навыки: - декомпозиции предметной области; - построения элементарной архитектуры проекта
Требуется разработать создание и просмотр анект в социальной сети.  

## Функциональные требования:
- Авторизация по паролю.
- Страница регистрации, где указывается следующая информация:
1) Имя
2) Фамилия
3) Возраст
4) Пол
5) Интересы
6) Город
- Страницы с анкетой.

## Нефункциональные требования:
- Любой язык программирования
- В качестве базы данных использовать MySQL
- Не использовать ORM
- Программа должна представлять из себя монолитное приложение.
- Не рекомендуется использовать следующие технологии:
1) Репликация
2) Шардинг
3) Индексы
4) Кэширование


Верстка не важна. Подойдет самая примитивная.

Разместить приложение на любом хостинге. Например, heroku.

ДЗ принимается в виде исходного кода на github и демонстрации проекта на хостинге.
Критерии оценки: Оценка происходит по принципу зачет/незачет.

Требования:
Есть возможность регистрации, создавать персональные страницы, возможность подружиться, список друзей.
Отсутствуют SQL-инъекции.
Пароль хранится безопасно. 
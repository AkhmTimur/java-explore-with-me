# java-explore-with-me

# Функциональность:
Проект состоит из двух основных частей:
1) Основной сервис. С его помощью можно планировать организацию различных событий, а также приглашать туда людей. Присуствует функционал для модерирования событий и пользователей. 
2) Сервис статистики. Собирает и хранит статистику просмотров основого сервиса, позволяет делать выборки для анализа работы приложения.

Стек технологий: 
 - Spring boot (11 версия java)
 - Maven
 - Lombok
 - Hibernate
 - CriteriaAPI
 - PostgreSQL

# Инстуркция по развертыванию:
После клонирования репозитория в корневой директории необходимо через консоль вызвать команду docker compose up. Для тестирования функционала приложения в папке postman представлена postman-коллекция.

# Статус проекта
Сервис находится в стадии разработки.
В планах добавить комментарии к событиям и поиск близлежайших мероприятий по локации пользователя.
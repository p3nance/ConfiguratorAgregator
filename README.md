# ConfiguratorAgregator

Десктопное приложение на JavaFX для агрегации цен на компьютерные комплектующие.
Позволяет сравнивать цены из разных магазинов, собирать конфигурации ПК и сохранять избранное.

## Возможности

- 📦 **Каталог компонентов** — просмотр процессоров, видеокарт, материнских плат и других комплектующих
- 🔍 **Поиск и фильтрация** — по категории и названию в реальном времени
- 💰 **Сравнение цен** — агрегация предложений из нескольких магазинов
- 🖥️ **Конфигуратор сборок** — подбор совместимых компонентов с подсчётом итоговой стоимости
- ⭐ **Избранное** — сохранение понравившихся компонентов

## Стек технологий

| Технология | Назначение |
|---|---|
| Java 17+ | Основной язык |
| JavaFX | UI-фреймворк |
| FXML + CSS | Разметка и стили интерфейса |
| Supabase (PostgreSQL) | База данных |
| REST API | Подключение к Supabase |
| Gradle | Сборка проекта |

## Структура проекта

```
src/main/java/
├── com/example/authapp/
│   ├── models/          # Модели данных (Component, Store, ComponentPrice)
│   ├── repositories/    # Работа с Supabase API
│   ├── services/        # PriceAggregatorService
│   └── dto/             # DTO-объекты
├── controllers/         # JavaFX-контроллеры
└── config/              # Конфигурация подключения

src/main/resources/
├── views/               # FXML-файлы интерфейса
└── styles/              # CSS-стили
```

## Установка и запуск

### Требования
- JDK 17+
- Gradle 8+
- Аккаунт Supabase с развёрнутой схемой БД

### Настройка

1. Клонируй репозиторий:
   ```bash
   git clone https://github.com/p3nance/ConfiguratorAgregator.git
   cd ConfiguratorAgregator
   ```

2. Примени схему БД:
   ```bash
   # Выполни sql/schema.sql в Supabase SQL Editor
   ```

3. Запусти приложение:
   ```bash
   ./gradlew run
   ```

## Архитектура

Приложение построено по паттерну **MVC**:
- **Model** — классы в `models/` и `repositories/`
- **View** — FXML-файлы в `resources/views/`
- **Controller** — контроллеры JavaFX в `controllers/`

Данные загружаются из Supabase через REST API в фоновом потоке, после чего отображаются в UI через `Platform.runLater()`.

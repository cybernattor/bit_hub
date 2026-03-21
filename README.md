<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp" width="128" height="128" alt="bit Hub Logo">
</p>

<h1 align="center">bit Hub</h1>

<p align="center">
  <strong>Высокотехнологичная платформа для дистрибуции и управления Android-приложениями.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/Android-API%2023%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android">
  <img src="https://img.shields.io/badge/Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Compose">
  <img src="https://img.shields.io/badge/Backend-Supabase-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white" alt="Supabase">
</p>

---

## 🌟 Обзор
**bit Hub** — это современное решение, построенное на стеке **Jetpack Compose** и **Material 3**, обеспечивающее прямую доставку контента пользователям без посредников. Дизайн выполнен в фирменном стиле **bit Blue (#2C6CFF)** с полной поддержкой динамических цветов Material You.

## ✨ Ключевые возможности
- 🚀 **Динамическая витрина**: Обновление списка приложений в реальном времени через Supabase.
- 📥 **Фоновая дистрибуция**: Нативная загрузка APK напрямую из GitHub Releases через системный `DownloadManager`.
- ⚡ **Смарт-инсталлятор**: Автоматический перехват завершенных загрузок и запуск установки.
- 🎨 **Адаптивный интерфейс**: Полная поддержка темной темы и динамических цветов.
- 🔒 **Безопасная архитектура**: Модульное разделение (Data/UI/Logic) и защищенное хранение ключей.
- 🌍 **Локализация**: Поддержка русского и английского языков.

---

## 🛠 Технологический стек
- **UI**: Jetpack Compose, Material 3, Adaptive Navigation Suite.
- **Networking**: Ktor Client, Kotlinx Serialization.
- **Image Loading**: Coil.
- **Backend**: Supabase (Postgrest).
- **Architecture**: MVVM, Clean Architecture principles.
- **Target SDK**: 36 (Android 16).

---

## 🚀 Инструкция для разработчиков

### 1. Настройка окружения
Создайте файл `secrets.properties` в корневом каталоге проекта:
```properties
SUPABASE_URL=https://ваш-проект.supabase.co
SUPABASE_KEY=ваш-анонимный-ключ
```

### 2. Схема данных (Supabase)
Выполните SQL-скрипт для настройки базы данных:
```sql
create table apps (
  id bigint primary key generated always as identity,
  title text not null,
  developer text,
  rating float8,
  reviews text,
  size text,
  description text,
  icon_url text,
  icon_color text, -- HEX-код цвета
  is_game boolean default false,
  download_url text -- прямая ссылка на APK релиз в GitHub
);

-- Настройка публичного доступа на чтение
alter table apps enable row level security;
create policy "Allow public read access" on apps for select using (true);
```

### 3. Сборка и развертывание
1. Выполните `Sync Project with Gradle Files`.
2. Проверьте валидность ссылок в Supabase.
3. Соберите проект: `Build -> Rebuild Project`.

---

## 📏 Стандарты проекта
- **Бренд**: bit Hub (регистр «bit» всегда строчный).
- **Package**: `com.bit.bithub`
- **Entry Point**: `BitHubApplication`

---

## 📸 Скриншоты
<p align="center">
  <i>Место для ваших скриншотов. Добавьте их в папку assets и укажите ссылки здесь.</i>
</p>

---
<p align="center">
  Разработано с ❤️ для Android сообщества.
</p>

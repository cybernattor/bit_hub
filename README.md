# bit Hub

**bit Hub** — это высокотехнологичная платформа для дистрибуции и управления Android-приложениями. Полнофункциональное решение, построенное на стеке Jetpack Compose и Material 3, обеспечивающее прямую доставку контента пользователям без посредников.

## Ключевые возможности
- 🚀 **Динамическая витрина**: Автоматическое обновление списка приложений и игр в реальном времени через интеграцию с Supabase Cloud.
- 📥 **Система фоновой дистрибуции**: Нативная загрузка APK-файлов напрямую из GitHub Releases через системный DownloadManager с поддержкой докачки.
- ⚡ **Смарт-инсталлятор**: Интеллектуальный перехват завершенных загрузок и автоматический запуск процесса установки пакетов.
- 🎨 **Адаптивный интерфейс**: Фирменный визуальный стиль в цвете **bit Blue (#2C6CFF)** с полной поддержкой динамических цветов Material You и темной темы.
- 🔒 **Безопасная архитектура**: Модульное разделение кода (Data/UI/Logic) и защищенное хранение API-ключей.
- 🌍 **Локализация**: Глобальная поддержка русского и английского языков.

---

## Инструкция для разработчиков

### 1. Настройка окружения
Для работы с базой данных создайте файл `secrets.properties` в корневом каталоге проекта:
```properties
SUPABASE_URL=https://ваш-проект.supabase.co
SUPABASE_KEY=ваш-анонимный-ключ
```

### 2. Схема данных (Supabase)
Для развертывания бэкенда выполните SQL-скрипт:
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

### 3. Технические характеристики
- **Min SDK**: 23 (Android 6.0+)
- **Target SDK**: 36 (Android 16)
- **Язык**: Kotlin 2.0+
- **Стек**: Compose, Coroutines, Serialization, Supabase, Coil, DownloadManager API.

### 4. Стандарты именования
- Бренд: **bit Hub** (регистр «bit» всегда строчный).
- Package: `com.bit.bithub`.
- Entry Point: `BitHubApplication` (системный класс).

### 5. Развертывание
1. Выполните `Sync Project with Gradle Files`.
2. Проверьте валидность ссылок в Supabase (ссылка должна вести напрямую на `.apk`).
3. Соберите проект через `Build -> Rebuild Project`.

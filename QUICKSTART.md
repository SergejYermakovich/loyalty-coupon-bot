# 🚀 Quick Start Guide — Loyalty Coupon Bot

Полное руководство по запуску и тестированию проекта.

---

## 📋 Оглавление

1. [Требования](#-требования)
2. [Быстрый старт с Docker](#-быстрый-старт-с-docker)
3. [Ручная настройка](#-ручная-настройка)
4. [Тестирование](#-тестирование)
5. [Полезные команды](#-полезные-команды)
6. [Troubleshooting](#-troubleshooting)

---

## 📦 Требования

### Обязательные

| Компонент | Версия | Зачем |
|-----------|--------|-------|
| **Docker** | 20+ | Контейнеризация |
| **Docker Compose** | 2.0+ | Оркестрация сервисов |
| **Telegram Bot Token** | — | От @BotFather |

### Опциональные (для разработки)

| Компонент | Версия | Зачем |
|-----------|--------|-------|
| **Java** | 17+ | Локальная сборка |
| **Maven** | 3.8+ | Сборка проекта |
| **PostgreSQL** | 14+ | Локальная БД (если не Docker) |

---

## 🎯 Быстрый старт с Docker

### Шаг 1: Клонировать репозиторий

```bash
git clone https://github.com/SergejYermakovich/loyalty-coupon-bot.git
cd loyalty-coupon-bot
```

### Шаг 2: Создать файл окружения

```bash
cat > .env << EOF
TELEGRAM_BOT_TOKEN=your-bot-token-here
TELEGRAM_BOT_USERNAME=YourBotName
TELEGRAM_MINI_APP_URL=http://localhost:8080/app
EOF
```

**Где взять токен:**
1. Открыть Telegram → @BotFather
2. `/newbot` → придумать имя
3. Скопировать токен (выглядит как `123456:ABC-DEF1234...`)
4. Вставить в `.env` вместо `your-bot-token-here`

### Шаг 3: Запустить все сервисы

```bash
docker-compose up -d
```

**Что запустится:**
- 🐘 **PostgreSQL** (port 5432) — база данных
- ☕ **Spring Boot App** (port 8080) — основное приложение
- 🌐 **Nginx** (port 80) — раздача Mini Apps

### Шаг 4: Проверить статус

```bash
docker-compose ps
```

**Ожидаемый результат:**
```
NAME                 STATUS         PORTS
loyaltybot-app       Up (healthy)   8080/tcp
loyaltybot-db        Up (healthy)   5432/tcp
loyaltybot-nginx     Up             0.0.0.0:80->80/tcp
```

### Шаг 5: Посмотреть логи

```bash
# Все логи
docker-compose logs -f

# Только приложение
docker-compose logs -f app

# Только база данных
docker-compose logs -f postgres
```

### Шаг 6: Проверить работоспособность

**1. Health check:**
```bash
curl http://localhost:8080/actuator/health
```

**Ожидаемый ответ:**
```json
{"status":"UP"}
```

**2. Mini Apps:**
```bash
curl http://localhost:80/app/
```

**3. API:**
```bash
curl http://localhost:80/api/business/stats
```

### Шаг 7: Остановить сервисы

```bash
# Остановить
docker-compose down

# Остановить + удалить volumes (БД!)
docker-compose down -v
```

---

## 🛠️ Ручная настройка (без Docker)

### Шаг 1: Поднять PostgreSQL

**Вариант A: Docker (рекомендуется)**
```bash
docker run --name loyaltybot-db \
  -e POSTGRES_DB=loyaltybot \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:15
```

**Вариант B: Локальный PostgreSQL**
```bash
# Ubuntu/Debian
sudo apt install postgresql postgresql-contrib

sudo -u postgres psql
CREATE DATABASE loyaltybot;
CREATE USER loyaltybot_user WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE loyaltybot TO loyaltybot_user;
\q
```

### Шаг 2: Установить переменные окружения

```bash
export TELEGRAM_BOT_TOKEN=your-bot-token-here
export TELEGRAM_BOT_USERNAME=YourBotName
export TELEGRAM_MINI_APP_URL=http://localhost:8080/app
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/loyaltybot
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
```

### Шаг 3: Собрать проект

```bash
cd /home/admin/.openclaw/workspace/projects/loyalty-coupon-bot
mvn clean package -DskipTests
```

### Шаг 4: Запустить приложение

```bash
java -jar target/loyalty-coupon-bot-0.1.0-SNAPSHOT.jar
```

Или через Maven:
```bash
mvn spring-boot:run
```

### Шаг 5: Проверить

```bash
curl http://localhost:8080/actuator/health
```

---

## 🧪 Тестирование

### Чеклист тестирования

#### 1. Базовые команды бота

| Команда | Ожидаемый результат |
|---------|---------------------|
| `/start` | Приветственное сообщение |
| `/help` | Список команд |
| `/mycoupons` | Список купонов (или "нет купонов") |
| `/scan` | Кнопка "Открыть сканер" |
| `/business` | Кнопка "Открыть панель" |

#### 2. Создание бизнеса

```
/comand: /createbusiness Test Cafe
```

**Ожидаемый результат:**
```
✅ Бизнес создан!
🏢 Название: Test Cafe
🆔 ID бизнеса: 1
📦 План: starter

🎫 Тестовый купон создан:
📍 Название: Бесплатный кофе
🎁 Награда: Получи бесплатный кофе после 9 покупок
📊 Печатей нужно: 9
🆔 ID купона: 1
```

#### 3. Активация купона

```
Команда: /activatecoupon 1
```

**Ожидаемый результат:**
```
✅ Купон активирован!
📍 Бесплатный кофе
🎁 Награда: Получи бесплатный кофе после 9 покупок
📊 Прогресс: 0/9 печатей
```

#### 4. Просмотр купонов

```
Команда: /mycoupons
```

**Ожидаемый результат:**
```
🎫 Твои купоны:

📍 Бесплатный кофе
📊 Прогресс: 0/9 печатей
🎁 Награда: Получи бесплатный кофе после 9 покупок
ID: 1
```

#### 5. QR-код

```
Команда: /showqr 1
```

**Ожидаемый результат:**
- Бот отправляет изображение с QR-кодом
- Подпись: "🎫 Бесплатный кофе, Прогресс: 0/9"

#### 6. Mini App Scanner

```
Команда: /scan
```

**Ожидаемый результат:**
- Кнопка "🚀 Открыть сканер"
- При нажатии открывается Mini App с камерой

#### 7. Добавление печати (через API)

```bash
curl -X POST http://localhost:8080/api/stamps/add \
  -H "Content-Type: application/json" \
  -d '{"userCouponId": 1, "staffMemberId": 1, "location": "Test Cafe"}'
```

**Ожидаемый ответ:**
```json
{
  "success": true,
  "userCouponId": 1,
  "newStamps": 1,
  "stampTarget": 9,
  "completed": false
}
```

#### 8. Статистика бизнеса (API)

```bash
curl http://localhost:8080/api/business/stats
```

**Ожидаемый ответ:**
```json
{
  "businessId": 1,
  "businessName": "Test Cafe",
  "totalCustomers": 1,
  "activeCoupons": 1,
  "rewardsClaimed": 0,
  "totalVisits": 1,
  "recentActivity": [...]
}
```

#### 9. Завершение купона

Добавить 8 печатей (всего будет 9):

```bash
for i in {1..8}; do
  curl -X POST http://localhost:8080/api/stamps/add \
    -H "Content-Type: application/json" \
    -d '{"userCouponId": 1}'
done
```

**Проверить:**
```
Команда: /mycoupons
```

**Ожидаемый результат:**
```
🎉 Готово! 9/9 — забирай награду: /claimreward 1
```

#### 10. Получение награды

```
Команда: /claimreward 1
```

**Ожидаемый результат:**
```
🎉 Награда получена!
✅ Бесплатный кофе
🎁 Получи бесплатный кофе после 9 покупок

Спасибо, что вы с нами!
```

---

## 🔧 Полезные команды

### Docker

```bash
# Запустить все сервисы
docker-compose up -d

# Остановить все сервисы
docker-compose down

# Перезапустить приложение
docker-compose restart app

# Посмотреть логи
docker-compose logs -f app

# Посмотреть статус
docker-compose ps

# Удалить всё (включая БД!)
docker-compose down -v

# Пересобрать образы
docker-compose build --no-cache
```

### База данных

```bash
# Подключиться к PostgreSQL
docker exec -it loyaltybot-db psql -U postgres -d loyaltybot

# Посмотреть таблицы
\dt

# Посмотреть данные
SELECT * FROM businesses;
SELECT * FROM user_coupons;
SELECT * FROM visits;

# Выйти
\q
```

### Логи приложения

```bash
# Последние 100 строк
docker-compose logs --tail=100 app

# Только ошибки
docker-compose logs app | grep ERROR

# Real-time мониторинг
docker-compose logs -f app | grep -i "stamp\|coupon\|business"
```

### API тесты

```bash
# Health check
curl http://localhost:8080/actuator/health

# Статистика
curl http://localhost:8080/api/business/stats

# Информация о купоне
curl http://localhost:8080/api/coupons/1

# Добавить печать
curl -X POST http://localhost:8080/api/stamps/add \
  -H "Content-Type: application/json" \
  -d '{"userCouponId": 1}'
```

---

## 🐛 Troubleshooting

### Приложение не запускается

**Ошибка:** `Connection refused to postgres:5432`

**Решение:**
```bash
# Подождать пока БД запустится
docker-compose logs -f postgres

# Перезапустить приложение
docker-compose restart app
```

---

### Ошибка: `Invalid bot token`

**Решение:**
1. Проверить токен в @BotFather
2. Убедиться, что в `.env` нет лишних пробелов
3. Перезапустить:
   ```bash
   docker-compose down
   docker-compose up -d
   ```

---

### Mini App не открывается

**Ошибка:** `404 Not Found`

**Решение:**
1. Проверить nginx конфиг:
   ```bash
   docker exec loyaltybot-nginx nginx -t
   ```
2. Перезапустить nginx:
   ```bash
   docker-compose restart nginx
   ```

---

### База данных пустая

**Проблема:** Таблицы не созданы

**Решение:**
```bash
# Посмотреть логи Liquibase
docker-compose logs app | grep liquibase

# Принудительно перезапустить миграции
docker-compose down -v
docker-compose up -d
```

---

### API возвращает 500

**Решение:**
1. Проверить логи:
   ```bash
   docker-compose logs app | grep ERROR
   ```
2. Проверить подключение к БД:
   ```bash
   docker exec loyaltybot-app wget -qO- http://localhost:8080/actuator/health
   ```

---

## 📞 Контакты

**Вопросы и проблемы:**
- GitHub Issues: https://github.com/SergejYermakovich/loyalty-coupon-bot/issues
- Telegram: @xmlreader

---

**Версия:** 1.0  
**Обновлено:** 2026-04-14

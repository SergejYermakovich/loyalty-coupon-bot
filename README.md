# 🎟️ Loyalty Coupon Bot

**Digital loyalty platform for small businesses** — Replace paper stamp cards with Telegram-based electronic coupons.

## 📋 Overview

Small businesses (coffee shops, barber shops, salons) struggle with:
- Paper stamp cards get lost, forgotten, look cheap
- No customer data or analytics
- Hard to track retention and re-engage customers

**Solution:** Telegram bot where customers collect electronic stamps and businesses track analytics via web dashboard.

## 🎯 Target Audience

1. **Coffee Shops** (primary) - high frequency, simple rewards
2. **Barber Shops / Beauty Salons** - regular customers, higher ticket
3. **Restaurants / Fitness Clubs** - loyalty programs already common

## 💰 Monetization

| Plan | Price | Features |
|------|-------|----------|
| Starter | $9-15/mo | 1 coupon, 100 customers, basic stats |
| Pro | $29-49/mo | 5 coupons, 1000 customers, analytics |
| Business | $99+/mo | Unlimited, API, white-label |

## 🛠️ Tech Stack

- **Backend:** Spring Boot 3.2.3
- **Database:** PostgreSQL + Liquibase
- **Bot:** Telegram Bot API 9.x
- **Build:** Maven

## 📁 Project Structure

```
src/main/java/com/loyaltybot/
├── LoyaltyCouponBotApplication.java
├── bot/
│   └── LoyaltyBot.java              # Telegram bot handler
├── config/
│   └── TelegramBotConfig.java       # Bot configuration
├── entity/
│   ├── BaseEntity.java              # Base entity with timestamps
│   ├── Business.java                # Business entity
│   ├── Coupon.java                  # Coupon entity
│   ├── User.java                    # User entity
│   ├── UserCoupon.java              # User-Coupon progress
│   ├── Visit.java                   # Visit history
│   └── StaffMember.java             # Staff entity
├── repository/
│   ├── BusinessRepository.java
│   ├── CouponRepository.java
│   ├── UserRepository.java
│   ├── UserCouponRepository.java
│   └── VisitRepository.java
└── service/
    ├── BusinessService.java
    ├── CouponService.java
    ├── UserService.java
    ├── UserCouponService.java       # Stamp management, rewards
    └── QrCodeService.java           # QR code generation (ZXing)
```

## 📅 MVP Timeline

- **Week 1-2:** Core infrastructure ✅ (Spring Boot, DB, Bot, Entities, Repositories, Services)
- **Week 3-4:** Core features + Mini Apps 🚧 (coupons ✅, QR generation ✅, stamps ✅, **Telegram Mini Apps**)
- **Week 5:** Integration + Polish
- **Week 6:** Launch with pilot businesses

### ✅ Completed Features

- Database schema with Liquibase migrations
- 7 JPA entities with relationships
- Telegram Bot with commands: `/start`, `/help`, `/mycoupons`, `/createbusiness`, `/activatecoupon`, `/showqr`
- UserCoupon management (activate, add stamp, claim reward)
- QR code generation for coupons (ZXing)
- Business & Coupon CRUD services
- **Telegram Mini Apps:**
  - QR Scanner (html5-qrcode + camera)
  - Business Dashboard (stats, activity)
  - Native Telegram theme support

## 🚀 Getting Started

### Prerequisites
- Java 17+
- PostgreSQL 14+
- Telegram Bot Token (from @BotFather)

### Configuration

Create `.env` or set environment variables:

```bash
export TELEGRAM_BOT_TOKEN=your-bot-token-here
export TELEGRAM_BOT_USERNAME=YourBotName
```

### Run with Docker (PostgreSQL)

```bash
docker run --name loyaltybot-db -e POSTGRES_DB=loyaltybot \
  -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 -d postgres:15
```

### Build & Run

```bash
mvn clean install
mvn spring-boot:run
```

### Bot Commands

**For Customers:**
- `/start` — Welcome message
- `/help` — Help information
- `/mycoupons` — View your active coupons with progress
- `/activatecoupon <ID>` — Activate a coupon by ID
- `/showqr <ID>` — Show QR code for a coupon (image)
- `/claimreward <ID>` — Claim reward for completed coupon

**For Business:**
- `/createbusiness <Name>` — Create a business (for owners)
- `/scan` — Open QR Scanner Mini App
- `/business` — Open Business Dashboard Mini App
- `/stats` — View business statistics

## 📄 Documentation

- **PRD:** [PRD.md](./PRD.md)
- **Notion:** [Project Page](https://www.notion.so/Loyalty-Coupon-Bot-PRD-3419aed68921819086dff4e321263b85)
- **Telegram WebApp Docs:** https://core.telegram.org/bots/webapps
- **html5-qrcode:** https://github.com/mebjas/html5-qrcode

## 📱 Telegram Mini Apps

The project includes Telegram Mini Apps for business users:

- **QR Scanner** (`/app/scan.html`) — Camera-based QR scanning
- **Dashboard** (`/app/index.html`) — Business statistics and management
- **Coupons** (`/app/coupons.html`) — Coupon CRUD operations

Mini Apps use native Telegram theme, camera access, and haptic feedback.

## 🔌 REST API

Mini Apps communicate with the backend via REST API:

### Business

- `GET /api/business/stats` — Get business statistics
- `GET /api/business/coupons` — List business coupons
- `GET /api/business/visits` — Recent visits

### Coupons

- `GET /api/coupons/{userCouponId}` — Get coupon info (for QR scanner)
- `POST /api/coupons/{userCouponId}/claim` — Claim reward

### Stamps

- `POST /api/stamps/add` — Add stamp to coupon

### Example Request

```bash
curl -X POST https://your-domain.com/api/stamps/add \
  -H "Content-Type: application/json" \
  -d '{"userCouponId": 1, "staffMemberId": 1, "location": "Coffee House"}'
```

### Response

```json
{
  "success": true,
  "userCouponId": 1,
  "newStamps": 4,
  "stampTarget": 9,
  "completed": false
}
```

## 📝 License

MIT

---

**Created:** 2026-04-13  
**Owner:** Siarhei Yermakovich (@xmlreader)  
**Status:** 🚧 Development in progress

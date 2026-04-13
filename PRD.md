# 🎟️ Loyalty Coupon Bot — PRD

**Product Requirements Document**

| Meta | Value |
|------|-------|
| **Version** | 2.0 |
| **Status** | Development |
| **Created** | 2026-04-13 |
| **Updated** | 2026-04-14 |
| **Owner** | Siarhei Yermakovich |
| **Notion** | https://www.notion.so/Loyalty-Coupon-Bot-3419aed68921819086dff4e321263b85 |
| **GitHub** | https://github.com/SergejYermakovich/loyalty-coupon-bot |

---

## 📋 Overview

**Digital loyalty platform for small businesses.** Replace paper stamp cards with Telegram-based electronic coupons.

> 🎯 **MVP Timeline:** 4-6 weeks  
> 🎯 **Target:** Coffee shops first

---

## 🎯 Problem Statement

### Current Pain Points

1. **Paper stamp cards get lost, forgotten, look cheap**
   - Customers forget cards at home
   - Cards get damaged/worn
   - Perceived as low-value

2. **No customer data or analytics for businesses**
   - Who are their best customers?
   - When do they visit?
   - What's the retention rate?

3. **Hard to track real conversion and retention**
   - No way to measure campaign effectiveness
   - Can't identify at-risk customers

4. **No re-engagement channel**
   - Can't notify customers about promotions
   - Lost customers stay lost

---

## 💡 Solution

### Core Features

1. **Telegram-based electronic coupons**
   - Always with user in their phone
   - No app download required
   - Instant activation via bot
   - **Telegram Mini Apps for business UI**

2. **Flexible configuration**
   - "9 stamps = free coffee"
   - "5 visits = 20% discount"
   - "3 purchases = free dessert"
   - Custom rewards per business

3. **Analytics dashboard (Telegram Mini App)**
   - Customer visits over time
   - Retention/churn rates
   - Peak hours analysis
   - Simple text + emoji reports in bot

4. **QR code scanning (Telegram Mini App)**
   - Staff scans customer's QR via camera
   - Instant stamp marking
   - Fraud prevention (time limits, staff auth)
   - Haptic feedback on scan

---

## 👥 Target Audience

### Primary: Coffee Shops ☕
- High frequency visits (daily/weekly)
- Simple reward structure
- Tech-savvy owners
- **TAM in Belarus:** ~500+ coffee shops

### Secondary: Barber Shops / Beauty Salons 💇
- Regular customers (monthly)
- Appointment-based
- Higher ticket value ($20-50+)
- **TAM in Belarus:** ~300+ salons

### Tertiary: Restaurants / Pizzerias / Fitness Clubs 🍕💪
- Loyalty programs already common
- Subscription tracking for gyms
- Family/group visits
- **TAM in Belarus:** ~1000+ establishments

---

## 💰 Monetization Model

**B2B2C:** Businesses pay, customers use free

### Pricing Tiers

| Plan | Price | Coupons | Customers | Features |
|------|-------|---------|-----------|----------|
| **Starter** | $9-15/mo | 1 | 100 | Basic stats, email support |
| **Pro** | $29-49/mo | 5 | 1000 | Analytics, export, priority support |
| **Business** | $99+/mo | Unlimited | Unlimited | API, white-label, dedicated support |

### Additional Revenue

- **Setup fee:** $50-100 (onboarding + training)
- **SMS/push notifications:** $0.01-0.05 per message
- **Custom integrations:** $200-500 one-time

### Revenue Projection (Year 1)

| Month | Customers | MRR |
|-------|-----------|-----|
| 3 | 10 | $250 |
| 6 | 30 | $750 |
| 9 | 60 | $1,500 |
| 12 | 100 | $2,500 |

---

## 🛠️ Technical Architecture

### Tech Stack

| Component | Technology | Rationale |
|-----------|------------|-----------|
| **Backend** | Spring Boot 3.x | Java expertise, fast development |
| **Database** | PostgreSQL | Reliable, scalable, free tier available |
| **Bot** | Telegram Bot API + telegrambots-client | No app install, instant access |
| **Frontend** | **Telegram Mini Apps** (HTML/CSS/JS) | Native Telegram UX, camera access |
| **QR Library** | html5-qrcode | Fast, reliable QR scanning |
| **Hosting** | Hetzner/DigitalOcean | Cost-effective ($5-10/mo start) |

### System Components

```
┌─────────────────┐     ┌──────────────────┐     ┌─────────────────┐
│  Telegram Bot   │────▶│   Spring Boot    │────▶│  PostgreSQL DB  │
│  (All users)    │     │   Backend API    │     │                 │
└─────────────────┘     └──────────────────┘     └─────────────────┘
                               ▲
                               │
                        ┌──────────────────┐
                        │ Telegram Mini App│
                        │ (QR Scanner +    │
                        │  Dashboard)      │
                        └──────────────────┘
```

### Why Telegram Mini Apps?

| Benefit | Description |
|---------|-------------|
| **No separate web panel** | Everything in Telegram |
| **Camera access** | Native QR scanning |
| **Auto theme** | Dark/light mode from Telegram |
| **Native buttons** | MainButton, BackButton |
| **User data** | Telegram.initData for auth |
| **Faster MVP** | 1 week vs 2-3 weeks for web panel |

### Database Schema (MVP)

```sql
-- Businesses
businesses (id, name, owner_telegram_id, plan, created_at)

-- Coupons
coupons (id, business_id, name, stamp_target, reward_description, active)

-- Users
users (id, telegram_id, name, username, created_at)

-- User Coupons (progress tracking)
user_coupons (id, user_id, coupon_id, stamps, completed_at, claimed_at)

-- Visits (analytics)
visits (id, user_coupon_id, timestamp, location, staff_id)

-- Payments
payments (id, business_id, amount, currency, status, period_start, period_end)
```

---

## 📅 MVP Scope (4-6 Weeks)

### Week 1-2: Core Infrastructure ✅
- [x] Spring Boot project setup
- [x] Database schema + Liquibase migrations
- [x] Telegram Bot registration + basic commands
- [x] User authentication (Telegram ID)
- [x] Entities: Business, Coupon, User, UserCoupon, Visit, StaffMember
- [x] Services: BusinessService, CouponService, UserService, UserCouponService, QrCodeService

### Week 3-4: Core Features + Mini Apps 🚧
- [x] Business registration flow (/createbusiness)
- [x] Coupon creation (auto + service)
- [x] User coupon activation (/activatecoupon)
- [x] QR code generation (QrCodeService)
- [x] Stamp marking logic (UserCouponService.addStamp)
- [ ] **Telegram Mini App: QR Scanner** (camera + html5-qrcode)
- [ ] **Telegram Mini App: Business Dashboard** (stats, coupons)
- [ ] Bot commands: /claimreward, /stats, /business

### Week 5: Polish + Integration
- [ ] Mini App ↔ Bot integration (sendData)
- [ ] Notifications (stamp added, coupon completed)
- [ ] Fraud prevention (time limits, staff auth)
- [ ] Error handling + logging

### Week 6: Launch Prep
- [ ] Deploy to staging
- [ ] Test with 2-3 friendly businesses
- [ ] Bug fixes
- [ ] Launch prep

---

## 🚀 Go-to-Market Strategy

### Phase 1: Local Validation (Month 1-2)
- **Goal:** 5-10 coffee shops in Brest (free pilot)
- **Tactic:** Personal outreach, demo in-person
- **Success metric:** 80% retention after 30 days

### Phase 2: Word of Mouth (Month 3-4)
- **Goal:** 30+ paying customers
- **Tactic:** Referral program ($10 credit per referral)
- **Success metric:** 30% organic growth

### Phase 3: Scale (Month 5-12)
- **Goal:** 100+ customers, $2.5K MRR
- **Tactic:** Instagram ads, partnerships with supplier companies
- **Success metric:** CAC < $50, LTV > $300

---

## ⚠️ Risks & Mitigation

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| **Competition** (existing solutions) | Medium | Medium | Focus on simplicity + local support + Telegram-native |
| **Low adoption** (businesses resist change) | High | High | Free pilot, case studies, ROI calculator |
| **Technical issues** (downtime, bugs) | Medium | High | Monitoring, backups, SLA for Pro+ plans |
| **Fraud** (fake stamps) | Low | Medium | Geolocation, time limits, staff authentication |

---

## 📊 Success Metrics (KPIs)

### Product Metrics
- **Activation rate:** % of businesses that create 1st coupon
- **Engagement:** Avg stamps per user per month
- **Retention:** % of businesses active after 90 days

### Business Metrics
- **MRR:** Monthly recurring revenue
- **CAC:** Customer acquisition cost
- **LTV:** Lifetime value per customer
- **Churn:** % of customers cancelling per month

---

## 🔗 Related Links

- **Notion Project:** https://www.notion.so/Loyalty-Coupon-Bot-3419aed68921819086dff4e321263b85
- **GitHub Repo:** https://github.com/SergejYermakovich/loyalty-coupon-bot
- **Telegram Docs:** https://core.telegram.org/bots/webapps
- **html5-qrcode:** https://github.com/mebjas/html5-qrcode

---

## 📝 Next Steps

### Completed ✅

1. [x] **GitHub repository created** (public, MIT license)
2. [x] **Database schema designed** (7 entities + Liquibase)
3. [x] **Spring Boot project initialized**
4. [x] **Telegram Bot basic commands** (/start, /help, /mycoupons, /activatecoupon, /showqr, /createbusiness)
5. [x] **Services implemented** (Business, Coupon, User, UserCoupon, QrCode)

### In Progress 🚧

6. [ ] **Telegram Mini App: QR Scanner** (html5-qrcode + camera)
7. [ ] **Telegram Mini App: Business Dashboard** (stats, coupons CRUD)
8. [ ] **Bot commands:** /claimreward, /stats, /business
9. [ ] **Mini App ↔ Bot integration** (sendData handling)

### TODO

10. [ ] **Register Telegram bot** (@BotFather) — get token
11. [ ] **Deploy Mini App** (static files on server)
12. [ ] **Find 3 pilot businesses** (friends/network in Brest)
13. [ ] **Test end-to-end flow**

---

## 👤 User Flow Diagrams

### Customer Flow

```
┌─────────────┐
│  Find Bot   │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  /start     │ → Welcome message + commands
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ /activatecoupon 1 │ → Coupon activated
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ /mycoupons  │ → List of coupons with progress
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  /showqr 1  │ → QR code image sent
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Show QR at  │
│   counter   │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Staff scans │
│   QR code   │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  +1 stamp!  │ → Notification: "4/9 stamps"
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  Repeat 5x  │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  9/9 Done!  │ → "🎉 Claim reward: /claimreward 1"
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ /claimreward 1 │ → Reward claimed, coupon reset
└─────────────┘
```

### Business Owner Flow

```
┌─────────────┐
│ /createbusiness Coffee House │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Business created + test coupon │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Share coupon ID with customers │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Customers activate coupons │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ /scan → Open Mini App │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Scan customer QR code │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Confirm stamp → +1 stamp added │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ /stats → View statistics │
└─────────────┘
```

---

## 📱 Telegram Mini Apps Specification

### 1. QR Scanner Mini App

**URL:** `/app/scan.html`

**Features:**
- Camera access via html5-qrcode
- Real-time QR scanning
- MainButton: "✅ Add Stamp"
- Haptic feedback on scan

**Data flow:**
```
Scan QR → Parse userCouponId → Show confirmation → 
MainButton click → tg.sendData({action: "add_stamp", userCouponId}) → 
Bot receives → Add stamp → Send notification
```

### 2. Business Dashboard Mini App

**URL:** `/app/dashboard.html`

**Features:**
- Statistics (visits, active coupons, rewards)
- Coupon list with progress
- Create new coupon (wizard)
- Export data

**Tabs:**
- 📊 Stats
- 🎫 Coupons
- 👥 Customers
- ⚙️ Settings

---

## 🎯 Success Metrics (Updated)

### Product Metrics
- **Activation rate:** % of businesses that create 1st coupon
- **Engagement:** Avg stamps per user per month
- **Retention:** % of businesses active after 90 days
- **Mini App usage:** % of businesses using QR scanner weekly

### Business Metrics
- **MRR:** Monthly recurring revenue
- **CAC:** Customer acquisition cost
- **LTV:** Lifetime value per customer
- **Churn:** % of customers cancelling per month

---

*Last updated: 2026-04-14*

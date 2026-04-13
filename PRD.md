# 🎟️ Loyalty Coupon Bot — PRD

**Product Requirements Document**

| Meta | Value |
|------|-------|
| **Version** | 1.0 |
| **Status** | Planning |
| **Created** | 2026-04-13 |
| **Owner** | Siarhei Yermakovich |
| **Notion** | https://www.notion.so/Loyalty-Coupon-Bot-3419aed68921819086dff4e321263b85 |

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

2. **Flexible configuration**
   - "9 stamps = free coffee"
   - "5 visits = 20% discount"
   - "3 purchases = free dessert"
   - Custom rewards per business

3. **Analytics dashboard**
   - Customer visits over time
   - Retention/churn rates
   - Peak hours analysis
   - Revenue attribution

4. **QR code scanning**
   - Staff scans customer's QR
   - Instant stamp marking
   - Fraud prevention (geolocation, time limits)

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
| **Frontend** | React/Next.js or Thymeleaf | Admin dashboard for businesses |
| **Hosting** | Hetzner/DigitalOcean | Cost-effective ($5-10/mo start) |
| **QR Scanning** | Web-based scanner | No native app needed |

### System Components

```
┌─────────────────┐     ┌──────────────────┐     ┌─────────────────┐
│   Telegram Bot  │────▶│   Spring Boot    │────▶│  PostgreSQL DB  │
│   (Users)       │     │   Backend API    │     │                 │
└─────────────────┘     └──────────────────┘     └─────────────────┘
                               │
                               ▼
                        ┌──────────────────┐
                        │   Admin Dashboard │
                        │   (Web Panel)     │
                        └──────────────────┘
```

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

### Week 1-2: Core Infrastructure
- [ ] Spring Boot project setup
- [ ] Database schema + Liquibase migrations
- [ ] Telegram Bot registration + basic commands
- [ ] User authentication (Telegram ID)

### Week 3-4: Core Features
- [ ] Business registration flow
- [ ] Coupon creation (name, stamps target, reward)
- [ ] User coupon activation
- [ ] QR code generation + scanning
- [ ] Stamp marking logic

### Week 5: Admin Dashboard
- [ ] Business web panel (login via Telegram)
- [ ] Coupon management (CRUD)
- [ ] Basic statistics (total users, stamps, redemptions)

### Week 6: Polish + Launch
- [ ] Error handling + logging
- [ ] Deploy to staging
- [ ] Test with 2-3 friendly businesses
- [ ] Bug fixes + launch prep

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
- **GitHub Repo:** _(to be created)_
- **Figma Designs:** _(to be created)_

---

## 📝 Next Steps

1. [ ] **Create GitHub repository** (public, MIT license)
2. [ ] **Set up project board** (GitHub Projects or Jira)
3. [ ] **Design database schema** (detailed ERD)
4. [ ] **Create Figma mockups** (bot flows + admin dashboard)
5. [ ] **Initialize Spring Boot project** (start.spring.io)
6. [ ] **Register Telegram bot** (@BotFather)
7. [ ] **Find 3 pilot businesses** (friends/network in Brest)

---

*Last updated: 2026-04-13*

# SECURITY.md — Tool-75 AI Assistant with RAG
## FINAL VERSION — Day 15

**Project:** Tool-75 — AI Assistant with RAG  
**Author:** Kushal V R (AI Developer 3)  
**Team:** 7 Members  
**Sprint:** 20 April 2026 – 16 May 2026  
**Demo Day:** 16 May 2026  
**Document Version:** Final — Day 15  

---

## Executive Summary

This document covers the complete security assessment of Tool-75 — AI Assistant with RAG, conducted during the sprint from 20 April 2026 to 16 May 2026.

Our team built an AI powered web application using Spring Boot (Java backend), Flask (Python AI service), React (frontend), PostgreSQL, Redis, and ChromaDB with the Groq LLaMA-3.3-70b model. As AI Developer 3, I was responsible for all security aspects of the AI service.

**Key Security Achievements:**
- Documented 10 security threats (5 OWASP + 5 AI specific)
- Built input sanitisation middleware blocking SQL injection, prompt injection and XSS
- Added rate limiting — 30 req/min default, 10 req/min on /generate-report
- Conducted OWASP ZAP baseline scan, active scan and final re-scan
- Fixed all Medium and High ZAP findings using flask-talisman
- Completed PII audit — 7 PII types detected and masked automatically
- All security tests passed — 0 Critical, 0 High findings remaining

**Overall Security Status: SECURE ✅**

---

## Part 1 — 5 OWASP Risks (Day 1)

### 1. Broken Access Control (OWASP A01)
**Attack Scenario:** VIEWER user calls restricted PUT endpoint — updates data they shouldn't access.  
**Mitigation:** `@PreAuthorize("hasRole('ADMIN')")` on sensitive endpoints. VIEWER gets GET only.  
**Status:** Handled by Java backend ✅

### 2. Injection — SQL and Prompt Injection (OWASP A03)
**Attack Scenario:** `'; DROP TABLE items; --` in search box. `"Ignore instructions"` in AI chat.  
**Mitigation:** JPA parameterised queries. Flask sanitiser.py with 28 injection patterns.  
**Status:** Completed ✅

### 3. Security Misconfiguration (OWASP A05)
**Attack Scenario:** Flask port 5000 hammered → Groq credits exhausted.  
**Mitigation:** flask-limiter, flask-talisman, DEBUG=False in production.  
**Status:** Completed ✅

### 4. Authentication Failures (OWASP A07)
**Attack Scenario:** Intercepted JWT token reused forever.  
**Mitigation:** JWT 1 hour expiry, JwtAuthFilter validates every request.  
**Status:** Handled by Java backend ✅

### 5. Outdated and Vulnerable Components (OWASP A06)
**Attack Scenario:** Old library with known CVE exploited by attacker.  
**Mitigation:** Latest versions, pinned requirements.txt, pip audit run.  
**Status:** Implemented ✅

---

## Part 2 — 5 AI Specific Threats (Day 2)

### Threat 1 — RAG Pipeline Poisoning
**Damage:** High | **Mitigation:** Only ADMIN adds to ChromaDB | **Status:** Planned ✅

### Threat 2 — Groq API Key Exposure
**Damage:** High | **Mitigation:** .env only, .gitignore, os.getenv() | **Status:** Implemented ✅

### Threat 3 — ChromaDB Unauthorised Access
**Damage:** Very High | **Mitigation:** Internal Docker network only | **Status:** Planned ✅

### Threat 4 — AI Response Manipulation
**Damage:** Medium-High | **Mitigation:** sanitiser.py blocks injection | **Status:** Completed ✅

### Threat 5 — PII in Logs and Prompts
**Damage:** Medium | **Mitigation:** detect_pii() masks 7 PII types | **Status:** Completed ✅

---

## Part 3 — All Tests Conducted

### Week 1 — Basic Security Tests (Day 5)
| Test | Result |
|------|--------|
| Empty input | ✅ PASS — 400 returned |
| SQL injection | ✅ PASS — 400 returned |
| Prompt injection | ✅ PASS — 400 returned |
| Empty /generate-report | ✅ PASS — 400 returned |

### Week 2 — Security Verification (Day 10)
| Test | Result |
|------|--------|
| JWT enforcement | ✅ VERIFIED — Java handles 401 |
| Rate limiting | ✅ VERIFIED — 429 at request 31 |
| Injection rejection | ✅ VERIFIED — 400 returned |

### Week 2 — PII Audit (Day 9)
| Test | Result |
|------|--------|
| Email detection | ✅ PASS — masked as [REDACTED-EMAIL] |
| Phone detection | ✅ PASS — masked as [REDACTED-PHONE_INDIA] |
| Multiple PII | ✅ PASS — all types masked |

### Week 3 — Full Stack Security Test (Day 13)
| Test | Result |
|------|--------|
| XSS in input | ✅ PASS — script tags stripped |
| 429 rate limit | ✅ PASS — 429 at request 31 |
| 401 without token | ✅ VERIFIED — Java returns 401 |
| Injection blocked | ✅ PASS — 400 returned |

---

## Part 4 — ZAP Scan Results

| Scan | Date | Findings | Status |
|------|------|----------|--------|
| Baseline Scan | 28 Apr 2026 | 3 (1 Medium, 2 Low) | Fixed ✅ |
| Active Scan | 05 May 2026 | 0 new findings | Secure ✅ |
| Final Re-scan | 05 May 2026 | 0 Critical/High | Confirmed ✅ |

---

## Part 5 — Findings Fixed

| Finding | Fix Applied | Day |
|---------|-------------|-----|
| CSP Header Missing | flask-talisman CSP policy | Day 8 |
| X-Content-Type-Options Missing | flask-talisman | Day 8 |
| X-Frame-Options Missing | after_request handler | Day 8 |
| Server version leak | Hidden server header | Day 8 |
| No input sanitisation | sanitiser.py middleware | Day 3 |
| No rate limiting | flask-limiter | Day 4 |
| PII in prompts | detect_pii() masking | Day 9 |

---

## Part 6 — Residual Risks

| Risk | Severity | Decision | Justification |
|------|----------|---------|---------------|
| CSP Fallback Warning | 🟢 Low | Accepted | Non-critical ZAP warning. CSP is correctly set. |
| Server Version in Debug | 🟢 Low | Accepted | Resolves with debug=False in production. |
| RAG Pipeline Poisoning | 🟡 Medium | Planned | ADMIN restriction planned for Docker setup. |
| ChromaDB Port Exposure | 🟡 Medium | Planned | Will be fixed in docker-compose.yml. |
| JWT on Flask endpoints | 🟢 Low | Accepted | JWT enforced at Java backend layer. |

---

## Part 7 — Final Security Checklist

### AI Service Security (Kushal V R — AI Developer 3)

- [x] Input sanitisation middleware implemented and tested
- [x] HTML stripping working — removes all `<script>` and HTML tags
- [x] SQL injection patterns detected and blocked — 400 returned
- [x] Prompt injection patterns detected and blocked — 400 returned
- [x] Rate limiting configured — 30 req/min default
- [x] Stricter rate limiting on /generate-report — 10 req/min
- [x] 429 response with retry_after confirmed working
- [x] flask-talisman installed and configured
- [x] CSP header added and verified
- [x] X-Content-Type-Options header added and verified
- [x] X-Frame-Options: DENY added and verified
- [x] Server version hidden in response headers
- [x] PII detection implemented — 7 PII types
- [x] PII masking working — [REDACTED-TYPE] format
- [x] No PII in logs confirmed
- [x] .env not committed to GitHub
- [x] GROQ_API_KEY stored safely in .env only
- [x] AiServiceClient.java written with 10s timeout and graceful null return
- [x] OWASP ZAP baseline scan completed — findings documented
- [x] OWASP ZAP active scan completed — 0 new findings
- [x] Final ZAP re-scan — 0 Critical/High confirmed
- [x] Full stack security test — all 4 scenarios passed
- [x] Week 1 security sign-off completed
- [x] Week 2 security sign-off completed
- [x] SECURITY.md final version completed

### Java Backend Security (Java Developers)
- [ ] JWT token generation and validation implemented
- [ ] JWT expiry set to 1 hour
- [ ] Spring Security configured — all routes protected
- [ ] RBAC implemented — ADMIN/MANAGER/VIEWER roles
- [ ] @PreAuthorize annotations on sensitive endpoints
- [ ] 401 returned for missing token verified
- [ ] 403 returned for wrong role verified
- [ ] Passwords hashed using BCrypt
- [ ] No secrets hardcoded in Java code

### Frontend Security (Java Developer 3)
- [ ] JWT stored securely — not in localStorage
- [ ] All API calls include Authorization header
- [ ] No sensitive data in browser console logs

### Infrastructure Security
- [ ] .env file not committed to GitHub
- [ ] .gitignore configured correctly
- [ ] ChromaDB not exposed on public port
- [ ] Docker network configured as internal

---

## Part 8 — Security Controls Summary

| Control | Implemented | Tested | Status |
|---------|-------------|--------|--------|
| Input sanitisation | ✅ | ✅ | Working |
| SQL injection detection | ✅ | ✅ | Working |
| Prompt injection detection | ✅ | ✅ | Working |
| XSS protection | ✅ | ✅ | Working |
| PII detection and masking | ✅ | ✅ | Working |
| Rate limiting (30/min) | ✅ | ✅ | Working |
| Rate limiting (10/min) | ✅ | ✅ | Working |
| CSP header | ✅ | ✅ | Working |
| X-Content-Type-Options | ✅ | ✅ | Working |
| X-Frame-Options | ✅ | ✅ | Working |
| Server version hidden | ✅ | ✅ | Working |
| JWT enforcement | ✅ | ✅ | Java backend |
| .env not committed | ✅ | ✅ | Working |

---

## Part 9 — Weekly Test Log

| Week | Test | Result |
|------|------|--------|
| Week 1 | OWASP + AI threat model | ✅ Done |
| Week 1 | Basic security tests (4 tests) | ✅ All PASS |
| Week 2 | ZAP baseline scan | ✅ 3 findings fixed |
| Week 2 | JWT, rate limiting, injection verified | ✅ All VERIFIED |
| Week 2 | PII audit | ✅ All clear |
| Week 3 | ZAP active scan | ✅ 0 new findings |
| Week 3 | Final ZAP re-scan | ✅ 0 Critical/High |
| Week 3 | Full stack security test | ✅ 4/4 PASS |
| Week 4 | Final security checklist | ✅ Completed |

---

## Team Sign-Off

| Name | Role | Sign-off |
|------|------|---------|
| Kushal V R | AI Developer 3 | ✅ Signed — 05 May 2026 |
| Kavana S | Java Developer 1 | Pending |
| Ganashree B S | Java Developer 2 | Pending |
| Syed Abdul Rahaman | Java Developer 3 | Pending |
| Poshitha A Kundar | AI Developer 1 | ✅ Signed — 10 May 2026 |
| SHRIVAS SHRIPAD NADIGER | AI Developer 2 | Pending |
| Vinod R | Security Reviewer | Pending |

*Note: Other team members have been requested to sign off. Kushal V R has completed and signed off all AI Developer 3 security responsibilities.*

---

*Document Status: FINAL*  
*Last Updated: Day 15 — 05 May 2026 | Kushal V R (AI Developer 3)*
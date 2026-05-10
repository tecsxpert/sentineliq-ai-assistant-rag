# SentinelIQ AI Service — API Documentation
**Author:** Poshitha A Kundar (AI Developer 1)  
**Day 16:** API Documentation

## Endpoints

### 1. Health Check
Checks if the AI service is online.
- **URL:** `/health`
- **Method:** `GET`
- **Response:**
  ```json
  {
      "success": true,
      "status": "healthy",
      "version": "1.0.0"
  }
  ```

### 2. Describe Risk
Analyzes an operational risk event using RAG and Groq.
- **URL:** `/describe`
- **Method:** `POST`
- **Rate Limit:** 30 per minute
- **Body:**
  ```json
  {
      "input": "User data was exposed via an unsecured S3 bucket."
  }
  ```
- **Response:**
  ```json
  {
      "success": true,
      "description": "...",
      "tokens_used": 150
  }
  ```

### 3. Recommend Mitigations
Provides actionable recommendations based on risk description.
- **URL:** `/recommend`
- **Method:** `POST`
- **Rate Limit:** 30 per minute
- **Body:**
  ```json
  {
      "input": "S3 bucket exposure incident"
  }
  ```

### 4. Generate Full Report
Generates a comprehensive risk report.
- **URL:** `/generate-report`
- **Method:** `POST`
- **Rate Limit:** 10 per minute (Strict)
- **Body:**
  ```json
  {
      "input": "Full details of the incident..."
  }
  ```

### 5. Index Document
Adds a document to the ChromaDB vector store.
- **URL:** `/index`
- **Method:** `POST`
- **Rate Limit:** 30 per minute
- **Body:**
  ```json
  {
      "id": "policy_01",
      "text": "All S3 buckets must be private.",
      "metadata": {"category": "security"}
  }
  ```

## Security & Error Handling
All endpoints enforce:
1. **XSS Protection:** HTML is stripped.
2. **Injection Protection:** Prompt/SQL injection returns `400 Bad Request`.
3. **PII Masking:** Emails, phones, cards are masked.
4. **Rate Limiting:** Exceeding limits returns `429 Too Many Requests`.

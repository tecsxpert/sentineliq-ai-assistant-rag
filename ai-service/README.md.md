# AI Service — Tool-75 AI Assistant with RAG

**Author:** Kushal V R (AI Developer 3)  
**Port:** 5000  
**Health Check:** http://localhost:5000/health  

---

## What is this?

This is the Python Flask AI microservice for Tool-75. It handles all AI related functionality including input sanitisation, rate limiting, and communication with the Groq API and ChromaDB RAG pipeline.

---

## Prerequisites

- Python 3.11
- pip

---

## Setup Steps

### 1. Clone the repository
```bash
git clone https://github.com/KushVRK/sentineliq-ai-assistant-rag.git
cd sentineliq-ai-assistant-rag/ai-service
```

### 2. Create .env file
```bash
cp ../.env.example .env
```
Fill in your values in the .env file.

### 3. Install dependencies
```bash
pip install -r requirements.txt
```

### 4. Run the service
```bash
python app.py
```

Service will start on `http://localhost:5000`

---

## Run with Docker

```bash
docker build -t ai-service .
docker run -p 5000:5000 ai-service
```

---

## Environment Variables

| Variable | Description | Required |
|----------|-------------|---------|
| GROQ_API_KEY | Groq API key from console.groq.com | Yes |
| FLASK_ENV | Set to production in prod | No |

---

## API Endpoints

### GET /health
Check if the service is running.

**Response:**
```json
{
  "success": true,
  "message": "AI service is running",
  "status": "healthy"
}
```

---

### POST /describe
Get an AI description of the input.

**Request:**
```json
{
  "input": "Your text here"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Description here"
}
```

---

### POST /recommend
Get 3 AI recommendations based on input.

**Request:**
```json
{
  "input": "Your text here"
}
```

---

### POST /generate-report
Generate a full AI report. Rate limited to 10 requests/minute.

**Request:**
```json
{
  "input": "Your text here"
}
```

---

## Rate Limiting

| Endpoint | Limit |
|----------|-------|
| All endpoints | 30 requests/minute |
| /generate-report | 10 requests/minute |

If limit exceeded → 429 Too Many Requests with retry_after field.

---

## Security Features

- Input sanitisation — strips HTML, blocks SQL and prompt injection
- PII detection — masks emails, phones, Aadhar, PAN before reaching AI
- Rate limiting — prevents API abuse
- Security headers — CSP, X-Content-Type-Options, X-Frame-Options

---

## Folder Structure

```
ai-service/
├── middleware/
│   └── sanitiser.py      ← Input sanitisation + PII detection
├── app.py                ← Main Flask app + rate limiting
├── requirements.txt      ← Python dependencies
├── Dockerfile            ← Docker configuration
└── README.md             ← This file
```

---

## GitHub Link
`https://github.com/KushVRK/sentineliq-ai-assistant-rag`

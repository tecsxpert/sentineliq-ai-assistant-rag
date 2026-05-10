# SentinelIQ AI Service

**Project:** Tool-75 — AI Assistant with RAG  
**Author:** Poshitha A Kundar (AI Developer 1)  
**Sprint:** 20 April 2026 – 16 May 2026

## Overview
Flask-based AI microservice that provides:
- **Risk Description** (`/describe`) — AI-generated risk analysis
- **Recommendations** (`/recommend`) — AI-powered risk recommendations
- **Report Generation** (`/generate-report`) — Full risk assessment reports

## Tech Stack
- **Framework:** Flask (Python 3.11)
- **AI Model:** Groq LLaMA-3.3-70b
- **Vector DB:** ChromaDB
- **Embeddings:** Sentence Transformers
- **Container:** Docker

## Setup

```bash
# 1. Create virtual environment
python -m venv venv
source venv/bin/activate  # Linux/Mac
venv\Scripts\activate     # Windows

# 2. Install dependencies
pip install -r requirements.txt

# 3. Configure environment
cp .env.example .env
# Edit .env with your GROQ_API_KEY

# 4. Run the service
python app.py
```

## API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/health` | GET | Health check |
| `/describe` | POST | Risk description (AI) |
| `/recommend` | POST | Risk recommendations (AI + RAG) |
| `/generate-report` | POST | Full report generation |

## Security Features
- Input sanitisation (HTML, SQL injection, prompt injection)
- Rate limiting (30 req/min default, 10 req/min on reports)
- PII detection and masking (7 PII types)
- Security headers (CSP, X-Frame-Options, X-Content-Type-Options)
- OWASP ZAP tested — 0 Critical/High findings

## Project Structure
```
ai-service/
├── app.py                 # Flask entry point
├── requirements.txt       # Python dependencies
├── Dockerfile            # Container configuration
├── .env.example          # Environment template
├── .gitignore            # Git exclusions
├── middleware/
│   └── sanitiser.py      # Input sanitisation & PII detection
├── services/
│   ├── groq_client.py    # Groq API wrapper
│   ├── vector_store.py   # ChromaDB client
│   ├── embeddings.py     # Text embedding generation
│   ├── rag_pipeline.py   # RAG orchestration
│   └── prompt_loader.py  # Prompt template loading
├── prompts/
│   ├── describe_prompt.txt
│   ├── recommend_prompt.txt
│   └── report_prompt.txt
└── tests/
    └── test_security.py  # Security tests
```

# app.py — Flask AI Service Entry Point
# Author: Poshitha A Kundar (AI Developer 1)
# Project: Tool-75 — SentinelIQ AI Assistant with RAG
# Day 4 — Added Rate Limiting & Security Headers

from flask import Flask, jsonify, request
from dotenv import load_dotenv
from middleware.sanitiser import sanitise_request
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
from flask_talisman import Talisman

# Load environment variables
load_dotenv()

# --- Create Flask app ---
app = Flask(__name__)

# --- Setup Security Headers using flask-talisman ---
# CSP header with proper fallback directives
# X-Content-Type-Options
Talisman(
    app,
    force_https=False, # Set to True in production with HTTPS
    strict_transport_security=False,
    content_security_policy={
        'default-src': ["'self'"],
        'script-src': ["'self'"],
        'style-src': ["'self'"],
        'img-src': ["'self'"],
        'font-src': ["'self'"],
        'connect-src': ["'self'"],
        'frame-ancestors': ["'none'"],
    },
    x_content_type_options=True,
    referrer_policy='strict-origin-when-cross-origin'
)

# --- Hide server version + add X-Frame-Options ---
@app.after_request
def add_security_headers(response):
    response.headers['X-Frame-Options'] = 'DENY'
    response.headers['Server'] = 'AI-Service'  # hides Werkzeug/Python version
    return response

# --- Setup Rate Limiter ---
limiter = Limiter(
    get_remote_address,
    app=app,
    default_limits=["30 per minute"],
    headers_enabled=True
)

# --- Custom error handler for 429 ---
@app.errorhandler(429)
def rate_limit_exceeded(e):
    return jsonify({
        "success": False,
        "error": "Too many requests. Please slow down.",
        "retry_after": str(e.description)
    }), 429


# --- Health check route ---
@app.route("/health", methods=["GET"])
def health():
    """Health check endpoint."""
    return jsonify({
        "success": True,
        "message": "AI service is running",
        "status": "healthy",
        "version": "1.0.0",
        "author": "Poshitha A Kundar (AI Developer 1)"
    }), 200


# --- Root route ---
@app.route("/", methods=["GET"])
def root():
    """Root endpoint — returns service info."""
    return jsonify({
        "service": "SentinelIQ AI Assistant",
        "project": "Tool-75 — AI Assistant with RAG",
        "endpoints": ["/health", "/describe", "/recommend", "/generate-report", "/index"],
        "status": "active"
    }), 200


# --- /describe route — RAG Pipeline ---
@app.route("/describe", methods=["POST"])
@sanitise_request
def describe():
    """
    Describe endpoint — uses RAG pipeline to generate context-aware risk description.
    """
    from services.rag_pipeline import get_rag_pipeline

    data = request.get_json()
    user_input = data.get("input") or data.get("query") or data.get("message")

    if not user_input:
        return jsonify({"success": False, "error": "No input provided"}), 400

    try:
        pipeline = get_rag_pipeline()
        result = pipeline.generate_response(
            prompt_name="describe_prompt",
            user_input=user_input
        )

        if result["success"]:
            return jsonify({
                "success": True,
                "description": result["response"],
                "tokens_used": result["tokens_used"]
            }), 200
        else:
            return jsonify({"success": False, "error": result["error"]}), 500

    except Exception as e:
        return jsonify({"success": False, "error": f"Service error: {str(e)}"}), 500


# --- /recommend route — RAG Pipeline ---
@app.route("/recommend", methods=["POST"])
@sanitise_request
def recommend():
    """
    Recommend endpoint — uses RAG pipeline to generate context-aware risk recommendations.
    """
    from services.rag_pipeline import get_rag_pipeline

    data = request.get_json()
    user_input = data.get("input") or data.get("query") or data.get("message")

    if not user_input:
        return jsonify({"success": False, "error": "No input provided"}), 400

    try:
        pipeline = get_rag_pipeline()
        result = pipeline.generate_response(
            prompt_name="recommend_prompt",
            user_input=user_input
        )

        if result["success"]:
            return jsonify({
                "success": True,
                "recommendations": result["response"],
                "tokens_used": result["tokens_used"]
            }), 200
        else:
            return jsonify({"success": False, "error": result["error"]}), 500

    except Exception as e:
        return jsonify({"success": False, "error": f"Service error: {str(e)}"}), 500


# --- /generate-report route (placeholder) ---
@app.route("/generate-report", methods=["POST"])
@limiter.limit("10 per minute")
@sanitise_request
def generate_report():
    """Report generation endpoint — coming in Day 12. Strict rate limit 10/min."""
    return jsonify({
        "success": True,
        "message": "Generate report endpoint — coming soon (Day 12)"
    }), 200


# --- /index route — ChromaDB Document Addition ---
@app.route("/index", methods=["POST"])
@sanitise_request
def index_document():
    """
    Index endpoint — adds a document to the vector store.
    Request body: {"id": "doc1", "text": "Risk policy...", "metadata": {"category": "policy"}}
    """
    from services.vector_store import get_vector_store
    
    data = request.get_json()
    doc_id = data.get("id")
    text = data.get("text") or data.get("input")  # fallback to sanitised input
    metadata = data.get("metadata", {})
    
    if not doc_id or not text:
        return jsonify({"success": False, "error": "Both 'id' and 'text' are required"}), 400
        
    try:
        vector_store = get_vector_store()
        vector_store.add_document(doc_id=doc_id, text=text, metadata=metadata)
        
        return jsonify({
            "success": True,
            "message": f"Document '{doc_id}' successfully indexed in ChromaDB"
        }), 200
        
    except Exception as e:
        return jsonify({"success": False, "error": f"Indexing error: {str(e)}"}), 500


# --- Run the app ---
if __name__ == "__main__":
    print("=" * 60)
    print("SentinelIQ AI Service — Starting...")
    print("Author: Poshitha A Kundar (AI Developer 1)")
    print("Day 10: Security Verification & Testing active")
    print("Port: 5000")
    print("=" * 60)
    app.run(host="0.0.0.0", port=5000, debug=True)
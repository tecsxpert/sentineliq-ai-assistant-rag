# app.py — Flask AI Service Entry Point
# Author: Kushal V R (AI Developer 3)
# Day 4 — Tool-75 AI Assistant with RAG
# Updated Day 5 — Connected sanitiser middleware to all routes

from flask import Flask, jsonify
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
from middleware.sanitiser import sanitise_request

# --- Create Flask app ---
app = Flask(__name__)

# --- Setup Rate Limiter ---
limiter = Limiter(
    get_remote_address,
    app=app,
    default_limits=["30 per minute"],
    headers_enabled=True
)


# --- Custom error handler for 429 Too Many Requests ---
@app.errorhandler(429)
def rate_limit_exceeded(e):
    return jsonify({
        "success": False,
        "error": "Too many requests. Please slow down.",
        "retry_after": str(e.description)
    }), 429


# --- Health check route — no sanitiser needed here ---
@app.route("/health", methods=["GET"])
def health():
    return jsonify({
        "success": True,
        "message": "AI service is running",
        "status": "healthy"
    }), 200


# --- /describe route — sanitiser connected ---
@app.route("/describe", methods=["POST"])
@sanitise_request
def describe():
    return jsonify({
        "success": True,
        "message": "Describe endpoint — coming soon"
    }), 200


# --- /recommend route — sanitiser connected ---
@app.route("/recommend", methods=["POST"])
@sanitise_request
def recommend():
    return jsonify({
        "success": True,
        "message": "Recommend endpoint — coming soon"
    }), 200


# --- /generate-report route — sanitiser + stricter rate limit ---
@app.route("/generate-report", methods=["POST"])
@limiter.limit("10 per minute")
@sanitise_request
def generate_report():
    return jsonify({
        "success": True,
        "message": "Generate report endpoint — coming soon"
    }), 200


# --- Run the app ---
if __name__ == "__main__":
    print("Starting AI Service on port 5000...")
    print("Rate limiting: 30 req/min default, 10 req/min on /generate-report")
    app.run(host="0.0.0.0", port=5000, debug=True)
# app.py — Flask AI Service Entry Point
# Author: Kushal V R (AI Developer 3)
# Day 4 — Tool-75 AI Assistant with RAG
# This is the main Flask app with rate limiting added.
# 30 requests/min for all routes, 10 requests/min for /generate-report

from flask import Flask, jsonify
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address

# --- Create Flask app ---
app = Flask(__name__)

# --- Setup Rate Limiter ---
# get_remote_address means we track limits per IP address
# default_limits means every route gets 30 requests per minute max
limiter = Limiter(
    get_remote_address,
    app=app,
    default_limits=["30 per minute"],
    headers_enabled=True  # this adds rate limit info in response headers
)


# --- Custom error handler for 429 Too Many Requests ---
# When someone exceeds the rate limit, this runs and returns a nice error message
@app.errorhandler(429)
def rate_limit_exceeded(e):
    # e.description contains retry_after info automatically
    return jsonify({
        "success": False,
        "error": "Too many requests. Please slow down.",
        "retry_after": str(e.description)
    }), 429


# --- Test route — just to check the app is running ---
@app.route("/health", methods=["GET"])
def health():
    return jsonify({
        "success": True,
        "message": "AI service is running",
        "status": "healthy"
    }), 200


# --- Sample /describe route with default limit (30/min) ---
@app.route("/describe", methods=["POST"])
def describe():
    # This will be fully built by AI Developer 1
    # For now just returning a placeholder response
    return jsonify({
        "success": True,
        "message": "Describe endpoint — coming soon"
    }), 200


# --- Sample /recommend route with default limit (30/min) ---
@app.route("/recommend", methods=["POST"])
def recommend():
    # This will be fully built by AI Developer 1
    return jsonify({
        "success": True,
        "message": "Recommend endpoint — coming soon"
    }), 200


# --- /generate-report route with STRICTER limit (10/min) ---
# This route is more expensive so we limit it more strictly
@app.route("/generate-report", methods=["POST"])
@limiter.limit("10 per minute")  # overrides the default 30/min
def generate_report():
    # This will be fully built by AI Developer 2
    return jsonify({
        "success": True,
        "message": "Generate report endpoint — coming soon"
    }), 200


# --- Run the app ---
if __name__ == "__main__":
    # DEBUG must always be False in production!
    # We use it as True only for local testing
    print("Starting AI Service on port 5000...")
    print("Rate limiting: 30 req/min default, 10 req/min on /generate-report")
    app.run(host="0.0.0.0", port=5000, debug=True)
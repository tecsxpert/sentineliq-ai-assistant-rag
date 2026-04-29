# app.py — Flask AI Service Entry Point
# Author: Kushal V R (AI Developer 3)
# Day 8 — Fixed ALL ZAP findings completely

from flask import Flask, jsonify
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
from flask_talisman import Talisman
from middleware.sanitiser import sanitise_request

# --- Create Flask app ---
app = Flask(__name__)

# --- Setup Security Headers using flask-talisman ---
# Fix 1: CSP header with proper fallback directives
# Fix 2: X-Content-Type-Options
Talisman(
    app,
    force_https=False,
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

# --- Fix 3: Hide server version + add X-Frame-Options ---
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
    return jsonify({
        "success": True,
        "message": "AI service is running",
        "status": "healthy"
    }), 200

# --- /describe route ---
@app.route("/describe", methods=["POST"])
@sanitise_request
def describe():
    return jsonify({
        "success": True,
        "message": "Describe endpoint — coming soon"
    }), 200

# --- /recommend route ---
@app.route("/recommend", methods=["POST"])
@sanitise_request
def recommend():
    return jsonify({
        "success": True,
        "message": "Recommend endpoint — coming soon"
    }), 200

# --- /generate-report route ---
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
    print("Security headers: ALL ZAP findings fixed!")
    app.run(host="0.0.0.0", port=5000, debug=True)
# app.py — Flask AI Service Entry Point
# Author: Poshitha A Kundar (AI Developer 1)
# Project: Tool-75 — SentinelIQ AI Assistant with RAG
# Day 1 — Project Setup & Flask Skeleton

from flask import Flask, jsonify

# --- Create Flask app ---
app = Flask(__name__)


# --- Health check route ---
@app.route("/health", methods=["GET"])
def health():
    """
    Health check endpoint.
    Returns 200 if the AI service is running properly.
    Used by Docker, load balancers, and monitoring tools.
    """
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
    """
    Root endpoint — returns service info.
    """
    return jsonify({
        "service": "SentinelIQ AI Assistant",
        "project": "Tool-75 — AI Assistant with RAG",
        "endpoints": ["/health", "/describe", "/recommend", "/generate-report"],
        "status": "active"
    }), 200


# --- Placeholder: /describe route ---
@app.route("/describe", methods=["POST"])
def describe():
    """
    Describe endpoint — will be connected to Groq API in Day 2.
    Takes user input and returns AI-generated risk description.
    """
    return jsonify({
        "success": True,
        "message": "Describe endpoint — coming soon (Day 2)"
    }), 200


# --- Placeholder: /recommend route ---
@app.route("/recommend", methods=["POST"])
def recommend():
    """
    Recommend endpoint — will use RAG pipeline in Day 7.
    Takes user input and returns AI-generated recommendations.
    """
    return jsonify({
        "success": True,
        "message": "Recommend endpoint — coming soon (Day 7)"
    }), 200


# --- Placeholder: /generate-report route ---
@app.route("/generate-report", methods=["POST"])
def generate_report():
    """
    Report generation endpoint — will be implemented in Day 12.
    Takes user input and generates a full risk report.
    """
    return jsonify({
        "success": True,
        "message": "Generate report endpoint — coming soon (Day 12)"
    }), 200


# --- Run the app ---
if __name__ == "__main__":
    print("=" * 60)
    print("SentinelIQ AI Service — Starting...")
    print("Author: Poshitha A Kundar (AI Developer 1)")
    print("Project: Tool-75 — AI Assistant with RAG")
    print("Port: 5000")
    print("=" * 60)
    app.run(host="0.0.0.0", port=5000, debug=True)
# app.py — Flask AI Service Entry Point
# Author: Poshitha A Kundar (AI Developer 1)
# Project: Tool-75 — SentinelIQ AI Assistant with RAG
# Day 2 — Added Groq API integration for /describe endpoint

from flask import Flask, jsonify, request
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

# --- Create Flask app ---
app = Flask(__name__)


# --- Health check route ---
@app.route("/health", methods=["GET"])
def health():
    """
    Health check endpoint.
    Returns 200 if the AI service is running properly.
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
    """Root endpoint — returns service info."""
    return jsonify({
        "service": "SentinelIQ AI Assistant",
        "project": "Tool-75 — AI Assistant with RAG",
        "endpoints": ["/health", "/describe", "/recommend", "/generate-report"],
        "status": "active"
    }), 200


# --- /describe route — Now connected to Groq API ---
@app.route("/describe", methods=["POST"])
def describe():
    """
    Describe endpoint — sends user input to Groq LLaMA-3.3-70b
    and returns AI-generated risk description.

    Request body: {"input": "describe the risk..."}
    """
    from services.groq_client import get_groq_client

    data = request.get_json()
    if not data:
        return jsonify({"success": False, "error": "No JSON data provided"}), 400

    user_input = data.get("input") or data.get("query") or data.get("message")
    if not user_input:
        return jsonify({"success": False, "error": "No input provided"}), 400

    system_prompt = """You are SentinelIQ, an AI assistant specialized in operational risk analysis.
When given a risk event or scenario, provide:
1. A clear description of the risk
2. The potential impact (financial, operational, reputational)
3. The likelihood assessment (High/Medium/Low)
4. Which risk category it falls under (Credit, Market, Operational, Compliance)
Be concise but thorough. Use professional language suitable for risk reports."""

    try:
        client = get_groq_client()
        result = client.generate_response(
            system_prompt=system_prompt,
            user_input=user_input
        )

        if result["success"]:
            return jsonify({
                "success": True,
                "description": result["response"],
                "tokens_used": result["tokens_used"]
            }), 200
        else:
            return jsonify({
                "success": False,
                "error": result["error"]
            }), 500

    except Exception as e:
        return jsonify({
            "success": False,
            "error": f"Service error: {str(e)}"
        }), 500


# --- Placeholder: /recommend route ---
@app.route("/recommend", methods=["POST"])
def recommend():
    """Recommend endpoint — will use RAG pipeline in Day 7."""
    return jsonify({
        "success": True,
        "message": "Recommend endpoint — coming soon (Day 7)"
    }), 200


# --- Placeholder: /generate-report route ---
@app.route("/generate-report", methods=["POST"])
def generate_report():
    """Report generation endpoint — will be implemented in Day 12."""
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
    print("Day 2: Groq LLaMA-3.3-70b integration active")
    print("Port: 5000")
    print("=" * 60)
    app.run(host="0.0.0.0", port=5000, debug=True)
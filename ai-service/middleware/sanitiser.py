# middleware/sanitiser.py — Input Sanitisation & Security Middleware
# Author: Poshitha A Kundar (AI Developer 1)
# Day 9 Update — Added PII detection to audit personal data in inputs

import re
from flask import request, jsonify
from functools import wraps

# --- List of dangerous prompt injection phrases ---
INJECTION_PATTERNS = [
    "ignore previous instructions",
    "ignore all instructions",
    "forget everything",
    "forget all previous",
    "you are now",
    "pretend you are",
    "act as if",
    "your new instruction",
    "disregard your",
    "override your",
    "jailbreak",
    "do anything now",
    "dan mode",
    "developer mode",
    "system prompt",
    "reveal your prompt",
    "print your instructions",
    "what are your instructions",
    "bypass",
    "ignore the above",
    # SQL Injection patterns
    "drop table",
    "drop database",
    "'; --",
    "or 1=1",
    "union select",
    "insert into",
    "delete from",
    "update set",
]

PII_PATTERNS = {
    "email": r'[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}',
    "phone_india": r'(\+91|0)?[6-9][0-9]{9}',
    "phone_general": r'\b\d{10}\b',
    "aadhar": r'\b[2-9]{1}[0-9]{3}\s?[0-9]{4}\s?[0-9]{4}\b',
    "pan_card": r'\b[A-Z]{5}[0-9]{4}[A-Z]{1}\b',
    "credit_card": r'\b(?:\d[ -]?){13,16}\b',
    "ip_address": r'\b(?:[0-9]{1,3}\.){3}[0-9]{1,3}\b',
}


def strip_html(text):
    """
    Removes all HTML tags from the input text.
    Example: <script>alert('xss')</script> becomes alert('xss')
    """
    clean_text = re.sub(r'<[^>]+>', '', text)
    return clean_text.strip()


def detect_injection(text):
    """
    Checks if the input text contains any prompt injection or SQL injection patterns.
    Returns True if injection is detected, False if input is clean.
    """
    text_lower = text.lower()
    for pattern in INJECTION_PATTERNS:
        if pattern in text_lower:
            return True, pattern
    return False, None


def detect_pii(text):
    """
    Checks if the input text contains any PII (Personally Identifiable Information).
    This is used for the PII audit — we log when PII is found but don't block the request.
    Instead we mask the PII before it goes further.
    Returns a dict with:
    - has_pii: True or False
    - pii_types: list of what PII was found
    - masked_text: text with PII replaced by [REDACTED]
    """
    found_pii_types = []
    masked_text = text

    for pii_type, pattern in PII_PATTERNS.items():
        matches = re.findall(pattern, masked_text)
        if matches:
            found_pii_types.append(pii_type)
            # Replace PII with [REDACTED] so it never reaches Groq or logs
            masked_text = re.sub(pattern, f'[REDACTED-{pii_type.upper()}]', masked_text)
            # Log that PII was found — but never log the actual PII value!
            print(f"[PII AUDIT] PII detected and masked: type={pii_type}, count={len(matches)}")

    return {
        "has_pii": len(found_pii_types) > 0,
        "pii_types": found_pii_types,
        "masked_text": masked_text
    }


def sanitise_input(text):
    """
    Main sanitisation function:
    Step 1 - Strip HTML tags
    Step 2 - Check for prompt/SQL injection
    Step 3 - Detect and mask PII
    """
    if not text:
        return {
            "is_safe": False,
            "cleaned_text": None,
            "error": "Input cannot be empty",
            "pii_found": False
        }

    if not isinstance(text, str):
        return {
            "is_safe": False,
            "cleaned_text": None,
            "error": "Input must be a string",
            "pii_found": False
        }

    # Step 1 — Strip HTML
    cleaned_text = strip_html(text)

    # Step 2 — Check for injection
    is_injected, matched_pattern = detect_injection(cleaned_text)
    if is_injected:
        return {
            "is_safe": False,
            "cleaned_text": None,
            "error": "Invalid input detected. Suspicious pattern found.",
            "pii_found": False
        }

    # Step 3 — Detect and mask PII
    pii_result = detect_pii(cleaned_text)
    if pii_result["has_pii"]:
        # We don't block PII — we mask it and continue
        # This way the AI still gets the question but without personal data
        cleaned_text = pii_result["masked_text"]

    return {
        "is_safe": True,
        "cleaned_text": cleaned_text,
        "error": None,
        "pii_found": pii_result["has_pii"],
        "pii_types": pii_result["pii_types"]
    }


def sanitise_request(f):
    """
    Flask decorator that sanitises every request automatically.
    Returns 400 if injection detected.
    Masks PII and continues if personal data found.
    """
    @wraps(f)
    def decorated_function(*args, **kwargs):
        data = request.get_json()

        if not data:
            return jsonify({
                "success": False,
                "error": "No JSON data provided"
            }), 400

        user_input = data.get("input") or data.get("query") or data.get("message")

        if user_input:
            result = sanitise_input(user_input)

            if not result["is_safe"]:
                print(f"[SECURITY] Input rejected: {result['error']} | Raw input: {user_input[:100]}")
                return jsonify({
                    "success": False,
                    "error": result["error"]
                }), 400

            # Replace original input with cleaned+masked version
            if data.get("input"):
                data["input"] = result["cleaned_text"]
            elif data.get("query"):
                data["query"] = result["cleaned_text"]
            elif data.get("message"):
                data["message"] = result["cleaned_text"]

        return f(*args, **kwargs)

    return decorated_function
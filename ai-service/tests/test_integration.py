# tests/test_integration.py
# Author: Poshitha A Kundar (AI Developer 1)
# Day 18 — Integration Testing

from unittest.mock import patch

def test_full_pipeline_describe(client):
    """
    Test the full /describe pipeline:
    Request -> Sanitisation -> RAG Pipeline -> Groq (Mocked) -> Response
    """
    mock_response = {
        "success": True,
        "response": "This is a mocked risk description based on context.",
        "tokens_used": 100,
        "error": None
    }
    
    # We mock Groq API to avoid hitting the live API during CI tests
    with patch('services.groq_client.GroqClient.generate_with_context', return_value=mock_response):
        response = client.post('/describe', json={
            "input": "User data leak from S3 bucket"
        })
        
        assert response.status_code == 200
        data = response.json
        assert data['success'] is True
        assert "mocked risk description" in data['description']

def test_full_pipeline_with_pii(client):
    """
    Test the full pipeline when PII is present in the input.
    """
    mock_response = {
        "success": True,
        "response": "Risk report for redacted email user.",
        "tokens_used": 50,
        "error": None
    }
    
    with patch('services.groq_client.GroqClient.generate_with_context', return_value=mock_response) as mock_groq:
        response = client.post('/recommend', json={
            "input": "User john@example.com was phished."
        })
        
        assert response.status_code == 200
        
        # Verify that the input sent to Groq was masked
        args, kwargs = mock_groq.call_args
        assert "john@example.com" not in kwargs['user_input']
        assert "[REDACTED-EMAIL]" in kwargs['user_input']

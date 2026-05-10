# tests/test_security_headers.py
# Author: Poshitha A Kundar (AI Developer 1)
# Day 8 — OWASP ZAP Baseline Scan Fixes Verification

def test_security_headers_present(client):
    """
    Test that all required security headers (added in Day 4 to fix Day 8 ZAP findings)
    are present in the response.
    """
    response = client.get('/health')
    
    headers = response.headers
    
    # Check Content-Security-Policy
    assert 'Content-Security-Policy' in headers
    assert "default-src 'self'" in headers['Content-Security-Policy']
    assert "frame-ancestors 'none'" in headers['Content-Security-Policy']
    
    # Check X-Frame-Options
    assert 'X-Frame-Options' in headers
    assert headers['X-Frame-Options'] == 'DENY'
    
    # Check X-Content-Type-Options
    assert 'X-Content-Type-Options' in headers
    assert headers['X-Content-Type-Options'] == 'nosniff'
    
    # Check Referrer-Policy
    assert 'Referrer-Policy' in headers
    assert headers['Referrer-Policy'] == 'strict-origin-when-cross-origin'
    
    # Check Server header is hidden/modified
    assert 'Server' in headers
    assert headers['Server'] == 'AI-Service'

def test_health_endpoint_success(client):
    """Test health endpoint works while having security headers."""
    response = client.get('/health')
    assert response.status_code == 200
    assert response.json['success'] is True

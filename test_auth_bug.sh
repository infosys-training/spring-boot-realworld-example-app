#!/bin/bash

echo "🔍 Testing Authentication Bug - Scenario A"
echo "=========================================="
echo

# This token was generated with the original JWT secret before the rotation
ORIGINAL_TOKEN="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyLTEiLCJleHAiOjE3NjExNTczMTF9.xjOJcHcM2oEZxlwhAuZhJvbj9cCprSAs9aS-RiCBipMdl5PCZiUy94siuEpi5bRGoQFgSCG1qAfz0z05hTdV_A"

# Start the application in background
echo "Starting application with rotated JWT secret..."
./gradlew bootRun > /dev/null 2>&1 &
APP_PID=$!

# Wait for application to start
echo "Waiting for application to start..."
sleep 15

# Test basic endpoint to ensure app is running
echo "Testing basic endpoint..."
curl -s http://localhost:8080/tags > /dev/null
if [ $? -eq 0 ]; then
    echo "✅ Application is running on port 8080"
else
    echo "❌ Application failed to start"
    kill $APP_PID 2>/dev/null
    exit 1
fi

echo
echo "🧪 Testing Authentication Bug..."
echo "Scenario: User had a valid session token before the JWT secret rotation"
echo "Testing if existing token still works after secret rotation..."
echo

# Test existing token (should fail due to secret rotation)
echo "Testing existing user token from before secret rotation:"
TOKEN_RESPONSE=$(curl -s -X GET http://localhost:8080/user \
  -H "Authorization: Token $ORIGINAL_TOKEN")

echo "Token validation response:"
echo "$TOKEN_RESPONSE"
echo

# Check if token validation failed
if echo "$TOKEN_RESPONSE" | grep -q '"email"'; then
    echo "✅ Token still valid - No bug detected"
else
    echo "❌ AUTHENTICATION BUG CONFIRMED!"
    echo
    echo "🔍 Expected behavior: Valid tokens should continue working"
    echo "🐛 Actual behavior: Previously valid tokens are now rejected"
    echo "🔧 Root cause: JWT secret key rotation invalidated existing tokens"
    echo
fi

echo
echo "🧪 Testing new login (should work with new secret):"
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "user": {
      "email": "john@example.com",
      "password": "password123"
    }
  }')

if echo "$LOGIN_RESPONSE" | grep -q '"token"'; then
    echo "✅ New login works (generates token with new secret)"
    echo "📝 This confirms the issue: old tokens invalid, new tokens work"
else
    echo "❌ Even new login fails - bigger problem!"
fi

echo
echo "Cleaning up..."
kill $APP_PID 2>/dev/null
echo "Test complete!"

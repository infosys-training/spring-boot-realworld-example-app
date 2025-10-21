#!/bin/bash

echo "🔍 Testing Missing Articles Bug - Scenario B"
echo "============================================"
echo

# Start the application in background
echo "Starting application..."
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
echo "🧪 Testing Missing Articles Bug..."
echo

# Step 1: Login to get a token
echo "Step 1: Logging in to get authentication token..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "user": {
      "email": "john@example.com",
      "password": "password123"
    }
  }')

TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "❌ Failed to get authentication token"
    kill $APP_PID 2>/dev/null
    exit 1
fi

echo "✅ Got authentication token"

# Step 2: Create an article WITHOUT tags (this should trigger the bug)
echo
echo "Step 2: Creating an article without tags..."
ARTICLE_RESPONSE=$(curl -s -X POST http://localhost:8080/articles \
  -H "Content-Type: application/json" \
  -H "Authorization: Token $TOKEN" \
  -d '{
    "article": {
      "title": "Test Article Without Tags",
      "description": "This article has no tags and should demonstrate the bug",
      "body": "This article was created to test the missing articles bug in Scenario B.",
      "tagList": []
    }
  }')

ARTICLE_SLUG=$(echo "$ARTICLE_RESPONSE" | grep -o '"slug":"[^"]*"' | cut -d'"' -f4)

if [ -z "$ARTICLE_SLUG" ]; then
    echo "❌ Failed to create article"
    echo "Response: $ARTICLE_RESPONSE"
    kill $APP_PID 2>/dev/null
    exit 1
fi

echo "✅ Article created successfully with slug: $ARTICLE_SLUG"

# Step 3: Verify the article exists by accessing it directly
echo
echo "Step 3: Verifying article exists by direct access..."
DIRECT_ACCESS=$(curl -s http://localhost:8080/articles/$ARTICLE_SLUG)

if echo "$DIRECT_ACCESS" | grep -q "Test Article Without Tags"; then
    echo "✅ Article accessible via direct URL - article was saved correctly"
else
    echo "❌ Article not accessible via direct URL"
    kill $APP_PID 2>/dev/null
    exit 1
fi

# Step 4: Check if article appears in the main articles listing
echo
echo "Step 4: Checking if article appears in main articles listing..."
ARTICLES_LIST=$(curl -s http://localhost:8080/articles)

if echo "$ARTICLES_LIST" | grep -q "Test Article Without Tags"; then
    echo "✅ Article appears in listings - No bug detected"
else
    echo "❌ MISSING ARTICLES BUG CONFIRMED!"
    echo
    echo "🔍 Expected behavior: Article should appear in main articles listing"
    echo "🐛 Actual behavior: Article exists but doesn't show up in listings"
    echo "🔧 Root cause: Database query issue preventing articles without tags from appearing"
    echo
    echo "📊 Articles listing response (first 200 chars):"
    echo "$ARTICLES_LIST" | head -c 200
    echo "..."
fi

# Step 5: Test with an article that HAS tags (should work)
echo
echo "Step 5: Testing article WITH tags (should appear in listings)..."
TAGGED_ARTICLE_RESPONSE=$(curl -s -X POST http://localhost:8080/articles \
  -H "Content-Type: application/json" \
  -H "Authorization: Token $TOKEN" \
  -d '{
    "article": {
      "title": "Test Article With Tags",
      "description": "This article has tags and should appear in listings",
      "body": "This article was created to verify that tagged articles still work.",
      "tagList": ["test", "debugging"]
    }
  }')

TAGGED_SLUG=$(echo "$TAGGED_ARTICLE_RESPONSE" | grep -o '"slug":"[^"]*"' | cut -d'"' -f4)

if [ ! -z "$TAGGED_SLUG" ]; then
    echo "✅ Tagged article created: $TAGGED_SLUG"
    
    # Check if tagged article appears in listings
    sleep 2
    UPDATED_LIST=$(curl -s http://localhost:8080/articles)
    
    if echo "$UPDATED_LIST" | grep -q "Test Article With Tags"; then
        echo "✅ Tagged article appears in listings"
        echo "📝 This confirms the bug: only articles WITH tags appear in listings"
    else
        echo "❌ Even tagged article doesn't appear - bigger issue!"
    fi
fi

echo
echo "Cleaning up..."
kill $APP_PID 2>/dev/null
echo "Test complete!"

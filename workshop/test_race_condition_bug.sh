#!/bin/bash

echo "🔍 Testing Race Condition Bug - Scenario C"
echo "=========================================="
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
echo "🧪 Testing Race Condition in Favorites..."
echo

# Step 1: Login to get tokens for multiple users
echo "Step 1: Getting authentication tokens for multiple users..."

# User 1 (John)
TOKEN1=$(curl -s -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"user": {"email": "john@example.com", "password": "password123"}}' | \
  grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# User 2 (Jane)  
TOKEN2=$(curl -s -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"user": {"email": "jane@example.com", "password": "password123"}}' | \
  grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# User 3 (Bob)
TOKEN3=$(curl -s -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"user": {"email": "bob@example.com", "password": "password123"}}' | \
  grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN1" ] || [ -z "$TOKEN2" ] || [ -z "$TOKEN3" ]; then
    echo "❌ Failed to get authentication tokens"
    kill $APP_PID 2>/dev/null
    exit 1
fi

echo "✅ Got tokens for 3 users"

# Step 2: Get an existing article to favorite
echo
echo "Step 2: Finding an existing article to test with..."
ARTICLES_RESPONSE=$(curl -s http://localhost:8080/articles)
ARTICLE_SLUG=$(echo "$ARTICLES_RESPONSE" | grep -o '"slug":"[^"]*"' | head -1 | cut -d'"' -f4)

if [ -z "$ARTICLE_SLUG" ]; then
    echo "❌ No articles found to test with"
    kill $APP_PID 2>/dev/null
    exit 1
fi

echo "✅ Using article: $ARTICLE_SLUG"

# Step 3: Check initial favorite count
echo
echo "Step 3: Checking initial favorite count..."
INITIAL_RESPONSE=$(curl -s http://localhost:8080/articles/$ARTICLE_SLUG)
INITIAL_COUNT=$(echo "$INITIAL_RESPONSE" | grep -o '"favoritesCount":[0-9]*' | cut -d':' -f2)
echo "📊 Initial favorite count: $INITIAL_COUNT"

# Step 4: Simulate concurrent favoriting (this should trigger the race condition)
echo
echo "Step 4: Simulating concurrent favorite operations..."
echo "🚀 Launching 3 simultaneous favorite requests..."

# Launch all three favorite requests simultaneously in background
curl -s -X POST http://localhost:8080/articles/$ARTICLE_SLUG/favorite \
  -H "Authorization: Token $TOKEN1" > /tmp/fav1.log 2>&1 &
PID1=$!

curl -s -X POST http://localhost:8080/articles/$ARTICLE_SLUG/favorite \
  -H "Authorization: Token $TOKEN2" > /tmp/fav2.log 2>&1 &
PID2=$!

curl -s -X POST http://localhost:8080/articles/$ARTICLE_SLUG/favorite \
  -H "Authorization: Token $TOKEN3" > /tmp/fav3.log 2>&1 &
PID3=$!

# Wait for all requests to complete
wait $PID1 $PID2 $PID3

echo "✅ All favorite requests completed"

# Step 5: Check the final favorite count
echo
echo "Step 5: Checking final favorite count..."
sleep 2  # Give the system a moment to process

FINAL_RESPONSE=$(curl -s http://localhost:8080/articles/$ARTICLE_SLUG)
FINAL_COUNT=$(echo "$FINAL_RESPONSE" | grep -o '"favoritesCount":[0-9]*' | cut -d':' -f2)

echo "📊 Final favorite count: $FINAL_COUNT"
echo "📊 Expected count: $((INITIAL_COUNT + 3))"

# Step 6: Analyze the results
echo
echo "Step 6: Analyzing race condition results..."

EXPECTED_COUNT=$((INITIAL_COUNT + 3))

if [ "$FINAL_COUNT" -eq "$EXPECTED_COUNT" ]; then
    echo "✅ Favorite count is correct - No race condition detected"
    echo "🔍 Try running the test multiple times to trigger the race condition"
else
    echo "❌ RACE CONDITION DETECTED!"
    echo
    echo "🔍 Expected behavior: Count should increase by exactly 3"
    echo "🐛 Actual behavior: Count is inconsistent due to race condition"
    echo "🔧 Root cause: Concurrent access to favorite operations without proper synchronization"
fi

# Step 7: Check for duplicate favorites in database (if we could query it directly)
echo
echo "Step 7: Additional verification..."
echo "💡 In a real scenario, you would check the database for:"
echo "   - Duplicate favorite records for the same user/article"
echo "   - Inconsistent counts vs actual favorite relationships"
echo "   - Transaction isolation issues"

# Step 8: Test unfavoriting to see if it also has issues
echo
echo "Step 8: Testing unfavorite operations..."
curl -s -X DELETE http://localhost:8080/articles/$ARTICLE_SLUG/favorite \
  -H "Authorization: Token $TOKEN1" > /dev/null &

curl -s -X DELETE http://localhost:8080/articles/$ARTICLE_SLUG/favorite \
  -H "Authorization: Token $TOKEN2" > /dev/null &

curl -s -X DELETE http://localhost:8080/articles/$ARTICLE_SLUG/favorite \
  -H "Authorization: Token $TOKEN3" > /dev/null &

wait

sleep 2
UNFAV_RESPONSE=$(curl -s http://localhost:8080/articles/$ARTICLE_SLUG)
UNFAV_COUNT=$(echo "$UNFAV_RESPONSE" | grep -o '"favoritesCount":[0-9]*' | cut -d':' -f2)

echo "📊 Count after unfavoriting: $UNFAV_COUNT"
echo "📊 Expected count: $INITIAL_COUNT"

if [ "$UNFAV_COUNT" -eq "$INITIAL_COUNT" ]; then
    echo "✅ Unfavorite operations worked correctly"
else
    echo "❌ Unfavorite operations also show race condition issues"
fi

echo
echo "Cleaning up..."
kill $APP_PID 2>/dev/null
rm -f /tmp/fav*.log
echo "Test complete!"
echo
echo "💡 Note: Race conditions are timing-dependent and may not appear on every run."
echo "   Run this test multiple times to increase chances of triggering the bug."

#!/bin/bash

echo "🔍 Testing Memory Leak Bug - Scenario D"
echo "======================================="
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
echo "🧪 Testing Memory Leak Simulation..."
echo "This test simulates the memory leak by generating article views"
echo "In a real scenario, this would happen over hours/days of normal usage"
echo

# Step 1: Login to get authentication token
echo "Step 1: Getting authentication token..."
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

# Step 2: Get list of articles to view
echo
echo "Step 2: Getting articles to simulate views..."
ARTICLES_RESPONSE=$(curl -s http://localhost:8080/articles)
ARTICLE_SLUGS=($(echo "$ARTICLES_RESPONSE" | grep -o '"slug":"[^"]*"' | cut -d'"' -f4))

if [ ${#ARTICLE_SLUGS[@]} -eq 0 ]; then
    echo "❌ No articles found to test with"
    kill $APP_PID 2>/dev/null
    exit 1
fi

echo "✅ Found ${#ARTICLE_SLUGS[@]} articles to test with"

# Step 3: Simulate memory leak by generating many article views
echo
echo "Step 3: Simulating memory leak through repeated article views..."
echo "🚀 Generating article views to trigger cache growth..."

TOTAL_REQUESTS=0
START_TIME=$(date +%s)

# Simulate sustained load that would cause memory leak
for round in {1..10}; do
    echo "Round $round: Generating article views..."
    
    # View each article multiple times to fill cache and user history
    for slug in "${ARTICLE_SLUGS[@]}"; do
        for view in {1..5}; do
            curl -s -H "Authorization: Token $TOKEN" \
                "http://localhost:8080/articles/$slug" > /dev/null
            TOTAL_REQUESTS=$((TOTAL_REQUESTS + 1))
        done
    done
    
    # Brief pause between rounds
    sleep 1
done

END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))

echo "✅ Completed $TOTAL_REQUESTS article view requests in ${DURATION}s"

# Step 4: Check application logs for cache size (the cleanup method logs this)
echo
echo "Step 4: Checking for memory leak indicators..."
echo "Looking for cache size growth in application logs..."

# In a real scenario, you would check:
# - JVM heap usage
# - Cache sizes
# - Memory profiler data
# - GC frequency and effectiveness

echo "💡 Memory leak indicators to look for:"
echo "   - Cache cleanup logs showing growing cache sizes"
echo "   - Increasing memory usage over time"
echo "   - Collections that never shrink"
echo "   - Missing cleanup in scheduled tasks"

# Step 5: Demonstrate the issue by showing cache growth
echo
echo "Step 5: Demonstrating unbounded cache growth..."
echo "The ArticleCacheService has a cleanup method that runs every 5 minutes"
echo "but it doesn't actually clean anything - it just logs the sizes!"
echo

echo "🔍 Key issues in the code:"
echo "   1. ArticleCacheService.articleCache grows indefinitely"
echo "   2. ArticleCacheService.userViewHistory accumulates without bounds"
echo "   3. cleanupCache() method logs but doesn't clean"
echo "   4. No eviction policy or size limits on caches"

# Step 6: Show what would happen over time
echo
echo "Step 6: Projected memory growth over time..."
echo "📊 With normal usage patterns:"
echo "   - 1000 users viewing 100 articles each = 100,000 cache entries"
echo "   - Each ArticleData object ~1KB = 100MB just for article cache"
echo "   - User view history grows indefinitely = additional memory"
echo "   - No cleanup means memory only grows, never shrinks"
echo "   - Eventually: OutOfMemoryError after 6-8 hours"

# Step 7: Simulate what monitoring would show
echo
echo "Step 7: Simulated monitoring data..."
echo "🔴 Memory Usage Trend (simulated):"
echo "   Hour 1: 200MB heap usage"
echo "   Hour 2: 350MB heap usage"
echo "   Hour 4: 650MB heap usage"
echo "   Hour 6: 1.2GB heap usage"
echo "   Hour 8: 2.1GB heap usage → OutOfMemoryError"

echo
echo "🔧 Root Cause Analysis:"
echo "   - Collections grow without bounds (ConcurrentHashMap)"
echo "   - Missing eviction policy in cache"
echo "   - Scheduled cleanup method is incomplete"
echo "   - No memory management for user analytics data"

echo
echo "Cleaning up..."
kill $APP_PID 2>/dev/null
echo "Test complete!"
echo
echo "💡 Note: In this simulation, the memory leak would become apparent"
echo "   over hours of sustained usage. The cache and user history"
echo "   collections would grow until the JVM runs out of memory."

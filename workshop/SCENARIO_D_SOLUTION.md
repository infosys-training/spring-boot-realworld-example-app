# 🟣 Scenario D: Memory Leak Crash - Solution Guide

## Bug Summary
**Issue**: Application crashes with OutOfMemoryError after extended runtime  
**Root Cause**: Unbounded cache and user analytics collections without cleanup  
**Impact**: Memory exhaustion, application crashes, service unavailability  
**Difficulty**: Expert  

---

## The Problem

### What Happened:
1. New `ArticleCacheService` was added for performance optimization
2. The service uses unbounded `ConcurrentHashMap` collections for caching
3. User view history is tracked but never cleaned up
4. Scheduled cleanup method logs but doesn't actually clean anything
5. Memory usage grows continuously until OutOfMemoryError occurs

### Technical Details:
- **File**: `ArticleCacheService.java`
- **Issue**: Unbounded collections without eviction policies
- **Memory Growth**: `articleCache` and `userViewHistory` maps grow indefinitely
- **Failed Cleanup**: `cleanupCache()` method is incomplete

---

## Investigation Process

### Step 1: Identify Memory Leak Patterns
**Key Devin Prompts**:
- "Help me understand what could cause memory leaks in a Spring Boot application"
- "Show me any caching or collection management code that might grow indefinitely"
- "What are the best practices for preventing memory leaks in Java applications?"

**Investigation Path**:
1. Examine recent changes related to caching and analytics
2. Look for collections that only grow, never shrink
3. Check for missing cleanup in scheduled tasks
4. Analyze object lifecycle and reference management

### Step 2: Analyze the Problematic Code
**Memory Leak Sources**:

```java
// PROBLEM 1: Unbounded article cache
private final Map<String, ArticleData> articleCache = new ConcurrentHashMap<>();

// PROBLEM 2: Unbounded user view history
private final Map<String, List<String>> userViewHistory = new ConcurrentHashMap<>();

// PROBLEM 3: Incomplete cleanup method
@Scheduled(fixedRate = 300000)
public void cleanupCache() {
    // BUG: This cleanup method doesn't actually clean anything!
    System.out.println("Cache cleanup running. Article cache size: " + articleCache.size());
    System.out.println("User view history size: " + userViewHistory.size());
    // TODO: Implement actual cleanup logic - NEVER IMPLEMENTED!
}
```

### Step 3: Understand Memory Growth Pattern
- Every article view adds to cache (if not already cached)
- Every user view adds to user history (unbounded list growth)
- No eviction policy or size limits
- Cleanup method runs but does nothing
- Memory only grows, never shrinks

---

## The Fix

### Solution Options:

#### Option 1: Implement Proper Cache Cleanup (Recommended)
```java
@Scheduled(fixedRate = 300000) // Every 5 minutes
public void cleanupCache() {
    // Remove old cache entries (older than 1 hour)
    long oneHourAgo = System.currentTimeMillis() - (60 * 60 * 1000);
    
    // Clean article cache (add timestamp tracking)
    articleCache.entrySet().removeIf(entry -> 
        entry.getValue().getLastAccessed() < oneHourAgo);
    
    // Clean user view history (keep only last 100 views per user)
    userViewHistory.forEach((userId, views) -> {
        if (views.size() > 100) {
            views.subList(0, views.size() - 100).clear();
        }
    });
    
    System.out.println("Cache cleanup completed. Article cache size: " + articleCache.size());
    System.out.println("User view history size: " + userViewHistory.size());
}
```

#### Option 2: Use Bounded Collections
```java
// Use Guava Cache with size and time-based eviction
private final Cache<String, ArticleData> articleCache = CacheBuilder.newBuilder()
    .maximumSize(1000)
    .expireAfterAccess(1, TimeUnit.HOURS)
    .build();

// Use bounded concurrent map for user history
private final Map<String, Queue<String>> userViewHistory = new ConcurrentHashMap<>();

public void recordUserView(String userId, String articleId) {
    userViewHistory.computeIfAbsent(userId, k -> new ConcurrentLinkedQueue<>())
        .offer(articleId);
    
    // Keep only last 100 views per user
    Queue<String> views = userViewHistory.get(userId);
    while (views.size() > 100) {
        views.poll();
    }
}
```

#### Option 3: Use Spring Cache with Eviction
```java
@Service
@EnableCaching
public class ArticleCacheService {
    
    @Cacheable(value = "articles", key = "#articleId")
    public ArticleData getCachedArticle(String articleId) {
        return null; // Will trigger cache miss and delegate to actual service
    }
}

// In application.properties:
// spring.cache.cache-names=articles
// spring.cache.caffeine.spec=maximumSize=1000,expireAfterAccess=1h
```

#### Option 4: Remove Unbounded Analytics (Simple Fix)
```java
public void recordUserView(String userId, String articleId) {
    // Option: Don't track user history at all if not essential
    // Or use external analytics service instead of in-memory tracking
    // This eliminates the memory leak entirely
}
```

### Recommended Implementation:
```java
@Service
public class ArticleCacheService {
    
    // Use Guava Cache for automatic eviction
    private final Cache<String, ArticleData> articleCache = CacheBuilder.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(30, TimeUnit.MINUTES)
        .recordStats()
        .build();
    
    // Bounded user history with automatic cleanup
    private final Map<String, Queue<String>> userViewHistory = new ConcurrentHashMap<>();
    private static final int MAX_VIEWS_PER_USER = 50;
    
    public void cacheArticle(String articleId, ArticleData articleData) {
        articleCache.put(articleId, articleData);
    }
    
    public ArticleData getCachedArticle(String articleId) {
        return articleCache.getIfPresent(articleId);
    }
    
    public void recordUserView(String userId, String articleId) {
        Queue<String> views = userViewHistory.computeIfAbsent(userId, 
            k -> new ConcurrentLinkedQueue<>());
        
        views.offer(articleId);
        
        // Keep only recent views
        while (views.size() > MAX_VIEWS_PER_USER) {
            views.poll();
        }
    }
    
    @Scheduled(fixedRate = 300000)
    public void cleanupCache() {
        // Clean up empty user histories
        userViewHistory.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        
        // Log cache statistics
        CacheStats stats = articleCache.stats();
        System.out.println("Cache stats - Size: " + articleCache.size() + 
            ", Hit rate: " + stats.hitRate());
        System.out.println("User histories: " + userViewHistory.size());
    }
}
```

---

## Verification

### Test the Fix:
1. **Implement bounded collections** with eviction policies
2. **Add proper cleanup logic** to scheduled method
3. **Set maximum sizes** for caches and user histories
4. **Monitor memory usage** over extended periods

### Memory Testing:
```bash
# Run application with memory monitoring
java -Xmx512m -XX:+PrintGCDetails -jar app.jar

# Generate sustained load
for i in {1..1000}; do
  curl -H "Authorization: Token $TOKEN" http://localhost:8080/articles/article-1
done

# Check memory usage remains stable
jstat -gc <pid> 5s
```

---

## Learning Outcomes

### Key Takeaways:
1. **Memory Management**: Understanding object lifecycle and garbage collection
2. **Cache Design**: Importance of eviction policies and size limits
3. **Resource Cleanup**: Proper implementation of cleanup mechanisms
4. **Production Monitoring**: Memory leak detection and prevention

### Memory Leak Prevention:
- **Bounded Collections**: Always set size limits on caches and collections
- **Eviction Policies**: Implement time-based and size-based eviction
- **Cleanup Tasks**: Ensure scheduled cleanup actually cleans up
- **Resource Lifecycle**: Understand when objects should be eligible for GC

### Common Devin Collaboration Patterns:
- **Pattern Recognition**: "Show me collections that might grow indefinitely"
- **Lifecycle Analysis**: "Help me understand when these objects should be cleaned up"
- **Best Practices**: "What are the recommended patterns for cache management?"
- **Testing Strategies**: "How can I test for memory leaks in development?"

---

## Advanced Concepts

### Memory Management Strategies:
- **Weak References**: Allow objects to be GC'd when memory is needed
- **Soft References**: Cache that survives until memory pressure
- **Cache Libraries**: Guava, Caffeine, EHCache with built-in eviction
- **Off-Heap Storage**: Redis, Hazelcast for distributed caching

### Production Monitoring:
- **JVM Metrics**: Heap usage, GC frequency, memory pools
- **Application Metrics**: Cache hit rates, collection sizes
- **Alerting**: Memory usage thresholds, GC time alerts
- **Profiling**: Regular heap dumps, memory profiler analysis

---

## Postmortem Template

**Incident**: Application OutOfMemoryError crashes after 6-8 hours  
**Duration**: [Time from bug introduction to fix]  
**Root Cause**: Unbounded cache collections without eviction policies  
**Resolution**: Implemented bounded caches with automatic cleanup  
**Prevention**: Add memory usage monitoring and cache size alerts  

---

## Facilitator Notes

### Common Participant Approaches:
- ✅ **Good**: Identify unbounded collections as memory leak source
- ❌ **Inefficient**: Focus on database connections or external resources
- ✅ **Excellent**: Recognize incomplete cleanup method as root cause

### Time Expectations:
- **Fast learners**: 20-30 minutes
- **Average**: 25-35 minutes  
- **Need help**: 35+ minutes

### Intervention Points:
- If stuck after 25 minutes: Guide toward cache service implementation
- If focused on wrong area: Suggest examining recent caching changes
- If found but can't fix: Help with cache eviction strategies

### Advanced Discussion Points:
- Different cache eviction algorithms (LRU, LFU, TTL)
- Memory profiling tools and techniques
- Production memory monitoring strategies
- Trade-offs between memory usage and performance
- Distributed caching vs in-memory caching

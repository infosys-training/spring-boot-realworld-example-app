package io.spring.infrastructure.cache;

import io.spring.application.data.ArticleData;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

@Service
public class ArticleCacheService {
    
    // Cache that grows indefinitely - this will cause memory leak
    private final Map<String, ArticleData> articleCache = new ConcurrentHashMap<>();
    private final Map<String, List<String>> userViewHistory = new ConcurrentHashMap<>();
    
    public void cacheArticle(String articleId, ArticleData articleData) {
        // Cache article data for performance
        articleCache.put(articleId, articleData);
    }
    
    public ArticleData getCachedArticle(String articleId) {
        return articleCache.get(articleId);
    }
    
    public void recordUserView(String userId, String articleId) {
        // Track user view history for analytics - this grows indefinitely
        userViewHistory.computeIfAbsent(userId, k -> new ArrayList<>()).add(articleId);
    }
    
    public List<String> getUserViewHistory(String userId) {
        return userViewHistory.getOrDefault(userId, new ArrayList<>());
    }
    
    // This method is supposed to clean up old cache entries but has a bug
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    public void cleanupCache() {
        // BUG: This cleanup method doesn't actually clean anything!
        // It just logs the cache size but never removes old entries
        System.out.println("Cache cleanup running. Article cache size: " + articleCache.size());
        System.out.println("User view history size: " + userViewHistory.size());
        
        // TODO: Implement actual cleanup logic
        // The developer forgot to implement the cleanup, causing memory leak
    }
    
    public int getCacheSize() {
        return articleCache.size();
    }
    
    public int getViewHistorySize() {
        return userViewHistory.size();
    }
}

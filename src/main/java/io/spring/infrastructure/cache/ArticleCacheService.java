package io.spring.infrastructure.cache;

import io.spring.application.data.ArticleData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

  public void invalidateArticle(String articleId) {
    articleCache.remove(articleId);
  }

  public void recordUserView(String userId, String articleId) {
    // Track user view history for analytics - this grows indefinitely
    userViewHistory.computeIfAbsent(userId, k -> new ArrayList<>()).add(articleId);
  }

  public List<String> getUserViewHistory(String userId) {
    return userViewHistory.getOrDefault(userId, new ArrayList<>());
  }

  @Scheduled(fixedRate = 300000)
  public void cleanupCache() {
    System.out.println("Cache cleanup running. Article cache size: " + articleCache.size());
    System.out.println("User view history size: " + userViewHistory.size());

    articleCache.clear();
    userViewHistory.clear();

    System.out.println(
        "Cache cleared. New sizes - Articles: "
            + articleCache.size()
            + ", Views: "
            + userViewHistory.size());
  }

  public int getCacheSize() {
    return articleCache.size();
  }

  public int getViewHistorySize() {
    return userViewHistory.size();
  }
}

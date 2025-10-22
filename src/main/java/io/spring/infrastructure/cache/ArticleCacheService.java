package io.spring.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.spring.application.data.ArticleData;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ArticleCacheService {

  private final Cache<String, ArticleData> articleCache;
  private final Cache<String, List<String>> userViewHistory;

  public ArticleCacheService() {
    this.articleCache =
        Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .recordStats()
            .build();

    this.userViewHistory =
        Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .recordStats()
            .build();
  }

  public void cacheArticle(String articleId, ArticleData articleData) {
    articleCache.put(articleId, articleData);
  }

  public ArticleData getCachedArticle(String articleId) {
    return articleCache.getIfPresent(articleId);
  }

  public void recordUserView(String userId, String articleId) {
    userViewHistory.asMap().computeIfAbsent(userId, k -> new ArrayList<>()).add(articleId);
  }

  public List<String> getUserViewHistory(String userId) {
    List<String> history = userViewHistory.getIfPresent(userId);
    return history != null ? history : new ArrayList<>();
  }

  @Scheduled(fixedRate = 300000)
  public void cleanupCache() {
    articleCache.cleanUp();
    userViewHistory.cleanUp();

    System.out.println(
        "Cache cleanup completed. Article cache size: "
            + articleCache.estimatedSize()
            + ", Hit rate: "
            + String.format("%.2f%%", articleCache.stats().hitRate() * 100)
            + ", Evictions: "
            + articleCache.stats().evictionCount());

    System.out.println(
        "User view history size: "
            + userViewHistory.estimatedSize()
            + ", Hit rate: "
            + String.format("%.2f%%", userViewHistory.stats().hitRate() * 100)
            + ", Evictions: "
            + userViewHistory.stats().evictionCount());
  }

  public int getCacheSize() {
    return (int) articleCache.estimatedSize();
  }

  public int getViewHistorySize() {
    return (int) userViewHistory.estimatedSize();
  }
}

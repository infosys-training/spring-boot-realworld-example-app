package io.spring.infrastructure.cache;

import io.spring.application.data.ArticleData;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ArticleCacheService {

  private static final long CACHE_EXPIRATION_MINUTES = 30;

  private static class CachedArticle {
    final ArticleData data;
    final Instant timestamp;

    CachedArticle(ArticleData data) {
      this.data = data;
      this.timestamp = Instant.now();
    }
  }

  private static class ViewRecord {
    final String articleId;
    final Instant timestamp;

    ViewRecord(String articleId) {
      this.articleId = articleId;
      this.timestamp = Instant.now();
    }
  }

  private final Map<String, CachedArticle> articleCache = new ConcurrentHashMap<>();
  private final Map<String, List<ViewRecord>> userViewHistory = new ConcurrentHashMap<>();

  public void cacheArticle(String articleId, ArticleData articleData) {
    articleCache.put(articleId, new CachedArticle(articleData));
  }

  public ArticleData getCachedArticle(String articleId) {
    CachedArticle cached = articleCache.get(articleId);
    return cached != null ? cached.data : null;
  }

  public void recordUserView(String userId, String articleId) {
    userViewHistory.computeIfAbsent(userId, k -> new ArrayList<>()).add(new ViewRecord(articleId));
  }

  public List<String> getUserViewHistory(String userId) {
    List<ViewRecord> records = userViewHistory.getOrDefault(userId, new ArrayList<>());
    List<String> articleIds = new ArrayList<>();
    for (ViewRecord record : records) {
      articleIds.add(record.articleId);
    }
    return articleIds;
  }

  @Scheduled(fixedRate = 300000)
  public void cleanupCache() {
    Instant expirationThreshold = Instant.now().minusSeconds(CACHE_EXPIRATION_MINUTES * 60);

    int removedArticles = 0;
    int removedViews = 0;

    for (Map.Entry<String, CachedArticle> entry : articleCache.entrySet()) {
      if (entry.getValue().timestamp.isBefore(expirationThreshold)) {
        articleCache.remove(entry.getKey());
        removedArticles++;
      }
    }

    for (Map.Entry<String, List<ViewRecord>> entry : userViewHistory.entrySet()) {
      List<ViewRecord> records = entry.getValue();
      int originalSize = records.size();
      records.removeIf(record -> record.timestamp.isBefore(expirationThreshold));
      removedViews += originalSize - records.size();

      if (records.isEmpty()) {
        userViewHistory.remove(entry.getKey());
      }
    }

    System.out.println(
        "Cache cleanup completed. Removed "
            + removedArticles
            + " articles and "
            + removedViews
            + " view records. Article cache size: "
            + articleCache.size()
            + ", User view history size: "
            + userViewHistory.size());
  }

  public int getCacheSize() {
    return articleCache.size();
  }

  public int getViewHistorySize() {
    return userViewHistory.size();
  }
}

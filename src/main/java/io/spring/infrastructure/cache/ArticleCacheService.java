package io.spring.infrastructure.cache;

import io.spring.application.data.ArticleData;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ArticleCacheService {
  private static final Logger log = LoggerFactory.getLogger(ArticleCacheService.class);
  private static final long CACHE_TTL_SECONDS = 3600;
  private static final int MAX_USER_HISTORY_SIZE = 1000;

  private final Map<String, CacheEntry> articleCache = new ConcurrentHashMap<>();
  private final Map<String, List<String>> userViewHistory = new ConcurrentHashMap<>();

  private static class CacheEntry {
    final ArticleData data;
    final Instant timestamp;

    CacheEntry(ArticleData data) {
      this.data = data;
      this.timestamp = Instant.now();
    }
  }

  public void cacheArticle(String articleId, ArticleData articleData) {
    log.debug("Caching article: {}", articleId);
    articleCache.put(articleId, new CacheEntry(articleData));
  }

  public ArticleData getCachedArticle(String articleId) {
    CacheEntry entry = articleCache.get(articleId);
    if (entry != null && !isExpired(entry)) {
      log.debug("Cache hit for article: {}", articleId);
      return entry.data;
    }
    log.debug("Cache miss for article: {}", articleId);
    return null;
  }

  private boolean isExpired(CacheEntry entry) {
    return Instant.now().getEpochSecond() - entry.timestamp.getEpochSecond() > CACHE_TTL_SECONDS;
  }

  public void recordUserView(String userId, String articleId) {
    log.debug("Recording user view: userId={}, articleId={}", userId, articleId);
    userViewHistory.computeIfAbsent(userId, k -> new ArrayList<>()).add(articleId);

    List<String> history = userViewHistory.get(userId);
    if (history.size() > MAX_USER_HISTORY_SIZE) {
      history.remove(0);
      log.debug("Trimmed user view history for user: {}", userId);
    }
  }

  public List<String> getUserViewHistory(String userId) {
    return userViewHistory.getOrDefault(userId, new ArrayList<>());
  }

  @Scheduled(fixedRate = 300000)
  public void cleanupCache() {
    log.info("Starting cache cleanup...");

    int beforeArticleSize = articleCache.size();
    int beforeHistorySize = userViewHistory.size();

    articleCache
        .entrySet()
        .removeIf(
            entry -> {
              boolean expired = isExpired(entry.getValue());
              if (expired) {
                log.debug("Removing expired article from cache: {}", entry.getKey());
              }
              return expired;
            });

    userViewHistory.entrySet().removeIf(entry -> entry.getValue().isEmpty());

    int removedArticles = beforeArticleSize - articleCache.size();
    int removedHistory = beforeHistorySize - userViewHistory.size();

    log.info(
        "Cache cleanup completed. Removed {} expired articles, {} empty histories. "
            + "Current sizes - articles: {}, histories: {}",
        removedArticles,
        removedHistory,
        articleCache.size(),
        userViewHistory.size());
  }

  public int getCacheSize() {
    return articleCache.size();
  }

  public int getViewHistorySize() {
    return userViewHistory.size();
  }
}

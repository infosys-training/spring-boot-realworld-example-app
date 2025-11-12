package io.spring.infrastructure.cache;

import io.spring.application.data.ArticleData;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ArticleCacheService {

  private static final Logger logger = LoggerFactory.getLogger(ArticleCacheService.class);

  // Bounded cache with TTL to prevent memory leak
  private final Map<String, CacheEntry<ArticleData>> articleCache = new ConcurrentHashMap<>();

  @Value("${article.cache.max-size:5000}")
  private int maxCacheSize;

  @Value("${article.cache.ttl-minutes:30}")
  private int ttlMinutes;

  public void cacheArticle(String articleId, ArticleData articleData) {
    // Cache article data for performance with timestamp
    articleCache.put(articleId, new CacheEntry<>(articleData, System.currentTimeMillis()));
  }

  public ArticleData getCachedArticle(String articleId) {
    CacheEntry<ArticleData> entry = articleCache.get(articleId);
    if (entry == null) {
      return null;
    }

    long ttlMillis = ttlMinutes * 60 * 1000L;
    if (System.currentTimeMillis() - entry.getWriteTime() > ttlMillis) {
      articleCache.remove(articleId);
      return null;
    }

    return entry.getValue();
  }

  // Cleanup method now properly evicts old entries
  @Scheduled(fixedRate = 300000) // Run every 5 minutes
  public void cleanupCache() {
    long startTime = System.currentTimeMillis();
    int initialSize = articleCache.size();
    long ttlMillis = ttlMinutes * 60 * 1000L;
    long currentTime = System.currentTimeMillis();

    List<String> keysToRemove = new ArrayList<>();
    articleCache.forEach(
        (key, entry) -> {
          if (currentTime - entry.getWriteTime() > ttlMillis) {
            keysToRemove.add(key);
          }
        });

    keysToRemove.forEach(articleCache::remove);
    logger.info("Removed {} expired entries from article cache", keysToRemove.size());

    if (articleCache.size() > maxCacheSize) {
      int entriesToRemove = articleCache.size() - maxCacheSize;
      List<Map.Entry<String, CacheEntry<ArticleData>>> entries =
          new ArrayList<>(articleCache.entrySet());

      entries.sort(Comparator.comparingLong(e -> e.getValue().getWriteTime()));

      for (int i = 0; i < entriesToRemove && i < entries.size(); i++) {
        articleCache.remove(entries.get(i).getKey());
      }

      logger.info(
          "Removed {} oldest entries to maintain max cache size of {}",
          entriesToRemove,
          maxCacheSize);
    }

    long duration = System.currentTimeMillis() - startTime;
    logger.info(
        "Cache cleanup completed in {}ms. Size: {} -> {}",
        duration,
        initialSize,
        articleCache.size());
  }

  public int getCacheSize() {
    return articleCache.size();
  }

  private static class CacheEntry<T> {
    private final T value;
    private final long writeTime;

    public CacheEntry(T value, long writeTime) {
      this.value = value;
      this.writeTime = writeTime;
    }

    public T getValue() {
      return value;
    }

    public long getWriteTime() {
      return writeTime;
    }
  }
}

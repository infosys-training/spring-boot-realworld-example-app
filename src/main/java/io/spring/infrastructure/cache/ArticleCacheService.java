package io.spring.infrastructure.cache;

import io.spring.application.data.ArticleData;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ArticleCacheService {

  private static final Logger logger = LoggerFactory.getLogger(ArticleCacheService.class);

  private final Map<String, TimedArticle> articleCache = new ConcurrentHashMap<>();

  private final Map<String, UserViewHistory> userViewHistory = new ConcurrentHashMap<>();

  @Value("${cache.article.max-size:10000}")
  private int articleMaxSize;

  @Value("${cache.article.ttl-minutes:30}")
  private int articleTtlMinutes;

  @Value("${cache.user-history.max-users:50000}")
  private int maxUsers;

  @Value("${cache.user-history.max-views-per-user:100}")
  private int maxViewsPerUser;

  @Value("${cache.user-history.ttl-hours:24}")
  private int userHistoryTtlHours;

  public void cacheArticle(String articleId, ArticleData articleData) {
    articleCache.put(articleId, new TimedArticle(articleData, System.currentTimeMillis()));
  }

  public ArticleData getCachedArticle(String articleId) {
    TimedArticle timedArticle = articleCache.get(articleId);
    if (timedArticle == null) {
      return null;
    }

    long now = System.currentTimeMillis();
    long ttlMillis = articleTtlMinutes * 60L * 1000L;
    if (now - timedArticle.writeTimestamp > ttlMillis) {
      articleCache.remove(articleId);
      return null;
    }

    return timedArticle.articleData;
  }

  public void recordUserView(String userId, String articleId) {
    long now = System.currentTimeMillis();
    userViewHistory.compute(
        userId,
        (k, history) -> {
          if (history == null) {
            history = new UserViewHistory();
          }
          history.lastAccessTime = now;
          history.views.addFirst(articleId);
          while (history.views.size() > maxViewsPerUser) {
            history.views.pollLast();
          }
          return history;
        });
  }

  public List<String> getUserViewHistory(String userId) {
    UserViewHistory history = userViewHistory.get(userId);
    if (history == null) {
      return new ArrayList<>();
    }

    history.lastAccessTime = System.currentTimeMillis();

    return new ArrayList<>(history.views);
  }

  @Scheduled(fixedRate = 300000) // Run every 5 minutes
  public void cleanupCache() {
    long now = System.currentTimeMillis();
    int initialArticleSize = articleCache.size();
    int initialUserHistorySize = userViewHistory.size();

    long articleTtlMillis = articleTtlMinutes * 60L * 1000L;
    long expiredArticlesCount =
        articleCache.entrySet().stream()
            .filter(entry -> now - entry.getValue().writeTimestamp > articleTtlMillis)
            .count();
    articleCache
        .entrySet()
        .removeIf(entry -> now - entry.getValue().writeTimestamp > articleTtlMillis);

    int articlesEvicted = 0;
    if (articleCache.size() > articleMaxSize) {
      int toRemove = articleCache.size() - articleMaxSize;
      List<Map.Entry<String, TimedArticle>> sortedEntries =
          articleCache.entrySet().stream()
              .sorted(Comparator.comparingLong(e -> e.getValue().writeTimestamp))
              .limit(toRemove)
              .collect(java.util.stream.Collectors.toList());

      for (Map.Entry<String, TimedArticle> entry : sortedEntries) {
        articleCache.remove(entry.getKey());
        articlesEvicted++;
      }
    }

    long userHistoryTtlMillis = userHistoryTtlHours * 60L * 60L * 1000L;
    long expiredUsersCount =
        userViewHistory.entrySet().stream()
            .filter(entry -> now - entry.getValue().lastAccessTime > userHistoryTtlMillis)
            .count();
    userViewHistory
        .entrySet()
        .removeIf(entry -> now - entry.getValue().lastAccessTime > userHistoryTtlMillis);

    int usersEvicted = 0;
    if (userViewHistory.size() > maxUsers) {
      int toRemove = userViewHistory.size() - maxUsers;
      List<Map.Entry<String, UserViewHistory>> sortedEntries =
          userViewHistory.entrySet().stream()
              .sorted(Comparator.comparingLong(e -> e.getValue().lastAccessTime))
              .limit(toRemove)
              .collect(java.util.stream.Collectors.toList());

      for (Map.Entry<String, UserViewHistory> entry : sortedEntries) {
        userViewHistory.remove(entry.getKey());
        usersEvicted++;
      }
    }

    logger.info(
        "Cache cleanup completed. Articles: {} -> {} (expired: {}, evicted: {}), "
            + "User histories: {} -> {} (expired: {}, evicted: {})",
        initialArticleSize,
        articleCache.size(),
        expiredArticlesCount,
        articlesEvicted,
        initialUserHistorySize,
        userViewHistory.size(),
        expiredUsersCount,
        usersEvicted);
  }

  public int getCacheSize() {
    return articleCache.size();
  }

  public int getViewHistorySize() {
    return userViewHistory.size();
  }

  private static class TimedArticle {
    final ArticleData articleData;
    final long writeTimestamp;

    TimedArticle(ArticleData articleData, long writeTimestamp) {
      this.articleData = articleData;
      this.writeTimestamp = writeTimestamp;
    }
  }

  private static class UserViewHistory {
    final ConcurrentLinkedDeque<String> views = new ConcurrentLinkedDeque<>();
    volatile long lastAccessTime = System.currentTimeMillis();
  }
}

package io.spring.infrastructure.cache;

import io.spring.application.data.ArticleData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ArticleCacheService {

  private static final int MAX_CACHE_SIZE = 1000;
  private static final int MAX_HISTORY_PER_USER = 100;

  private final Map<String, ArticleData> articleCache =
      Collections.synchronizedMap(
          new LinkedHashMap<String, ArticleData>(MAX_CACHE_SIZE + 1, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, ArticleData> eldest) {
              return size() > MAX_CACHE_SIZE;
            }
          });

  private final Map<String, List<String>> userViewHistory = new ConcurrentHashMap<>();

  public void cacheArticle(String articleId, ArticleData articleData) {
    articleCache.put(articleId, articleData);
  }

  public ArticleData getCachedArticle(String articleId) {
    return articleCache.get(articleId);
  }

  public void recordUserView(String userId, String articleId) {
    userViewHistory.computeIfAbsent(userId, k -> new ArrayList<>()).add(articleId);
  }

  public List<String> getUserViewHistory(String userId) {
    return userViewHistory.getOrDefault(userId, new ArrayList<>());
  }

  @Scheduled(fixedRate = 300000)
  public void cleanupCache() {
    userViewHistory.entrySet().removeIf(entry -> entry.getValue().size() > MAX_HISTORY_PER_USER);
    System.out.println("Cache cleanup completed. Article cache size: " + articleCache.size());
    System.out.println("User view history entries: " + userViewHistory.size());
  }

  public int getCacheSize() {
    return articleCache.size();
  }

  public int getViewHistorySize() {
    return userViewHistory.size();
  }
}

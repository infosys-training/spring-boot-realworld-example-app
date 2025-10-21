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

  private static final int MAX_ARTICLE_CACHE_SIZE = 1000;
  private static final int MAX_USER_VIEW_HISTORY_SIZE = 100;
  private static final int TRIM_USER_VIEW_HISTORY_TO = 10;

  private final Map<String, ArticleData> articleCache =
      Collections.synchronizedMap(
          new LinkedHashMap<String, ArticleData>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, ArticleData> eldest) {
              return size() > MAX_ARTICLE_CACHE_SIZE;
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
    userViewHistory.compute(
        userId,
        (k, views) -> {
          if (views == null) {
            views = new ArrayList<>();
          }
          views.add(articleId);
          if (views.size() > MAX_USER_VIEW_HISTORY_SIZE) {
            views =
                new ArrayList<>(
                    views.subList(views.size() - TRIM_USER_VIEW_HISTORY_TO, views.size()));
          }
          return views;
        });
  }

  public List<String> getUserViewHistory(String userId) {
    return userViewHistory.getOrDefault(userId, new ArrayList<>());
  }

  @Scheduled(fixedRate = 300000)
  public void cleanupCache() {
    userViewHistory.entrySet().removeIf(entry -> entry.getValue().isEmpty());

    System.out.println("Cache cleanup completed. Article cache size: " + articleCache.size());
    System.out.println("User view history size: " + userViewHistory.size());
  }

  public int getCacheSize() {
    return articleCache.size();
  }

  public int getViewHistorySize() {
    return userViewHistory.size();
  }
}

package io.spring.infrastructure.cache;

import io.spring.application.data.ArticleData;
import io.spring.application.data.ProfileData;
import java.util.ArrayList;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArticleCacheServiceTest {

  @Test
  public void should_evict_old_articles_based_on_size_limit() {
    ArticleCacheService cacheService = new ArticleCacheService();

    for (int i = 0; i < 1500; i++) {
      ArticleData article = createTestArticle("article-" + i);
      cacheService.cacheArticle("article-" + i, article);
    }

    cacheService.cleanupCache();

    int cacheSize = cacheService.getCacheSize();
    Assertions.assertTrue(
        cacheSize <= 1000, "Cache size should be bounded to 1000, but was: " + cacheSize);
  }

  @Test
  public void should_evict_old_user_views_based_on_size_limit() {
    ArticleCacheService cacheService = new ArticleCacheService();

    for (int i = 0; i < 12000; i++) {
      cacheService.recordUserView("user-" + (i % 5000), "article-" + i);
    }

    cacheService.cleanupCache();

    int viewHistorySize = cacheService.getViewHistorySize();
    Assertions.assertTrue(
        viewHistorySize <= 10000,
        "View history size should be bounded to 10000, but was: " + viewHistorySize);
  }

  @Test
  public void should_handle_high_load_without_memory_leak() {
    ArticleCacheService cacheService = new ArticleCacheService();

    for (int i = 0; i < 15000; i++) {
      ArticleData article = createTestArticle("article-" + i);
      cacheService.cacheArticle("article-" + i, article);
      cacheService.recordUserView("user-" + (i % 1000), "article-" + i);
    }

    cacheService.cleanupCache();

    Assertions.assertTrue(cacheService.getCacheSize() <= 1000, "Article cache should be bounded");
    Assertions.assertTrue(
        cacheService.getViewHistorySize() <= 10000, "View history should be bounded");
  }

  private ArticleData createTestArticle(String id) {
    return new ArticleData(
        id,
        "slug-" + id,
        "Title " + id,
        "Description",
        "Body",
        false,
        0,
        new DateTime(),
        new DateTime(),
        new ArrayList<>(),
        new ProfileData("user-1", "username", "bio", "image", false));
  }
}

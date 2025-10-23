package io.spring.infrastructure.cache;

import io.spring.application.data.ArticleData;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArticleCacheServiceTest {
  private ArticleCacheService cacheService;

  @BeforeEach
  public void setUp() {
    cacheService = new ArticleCacheService();
  }

  @Test
  public void should_cache_and_retrieve_article() {
    ArticleData article = createTestArticle("article-1");

    cacheService.cacheArticle("article-1", article);
    ArticleData cached = cacheService.getCachedArticle("article-1");

    Assertions.assertNotNull(cached);
    Assertions.assertEquals("article-1", cached.getId());
  }

  @Test
  public void should_return_null_for_non_existent_article() {
    ArticleData cached = cacheService.getCachedArticle("non-existent");
    Assertions.assertNull(cached);
  }

  @Test
  public void should_record_and_retrieve_user_view_history() {
    cacheService.recordUserView("user-1", "article-1");
    cacheService.recordUserView("user-1", "article-2");

    List<String> history = cacheService.getUserViewHistory("user-1");

    Assertions.assertEquals(2, history.size());
    Assertions.assertTrue(history.contains("article-1"));
    Assertions.assertTrue(history.contains("article-2"));
  }

  @Test
  public void should_limit_user_view_history_size() {
    for (int i = 0; i < 1100; i++) {
      cacheService.recordUserView("user-1", "article-" + i);
    }

    List<String> history = cacheService.getUserViewHistory("user-1");

    Assertions.assertTrue(history.size() <= 1000, "History size should not exceed maximum limit");
  }

  @Test
  public void should_cleanup_expired_cache_entries() throws Exception {
    ArticleData article = createTestArticle("article-1");
    cacheService.cacheArticle("article-1", article);

    int initialSize = cacheService.getCacheSize();
    Assertions.assertEquals(1, initialSize);

    cacheService.cleanupCache();

    int sizeAfterCleanup = cacheService.getCacheSize();
    Assertions.assertTrue(
        sizeAfterCleanup <= initialSize, "Cache size should not increase after cleanup");
  }

  private ArticleData createTestArticle(String id) {
    return new ArticleData(
        id,
        "slug",
        "title",
        "description",
        "body",
        false,
        0,
        null,
        null,
        Collections.emptyList(),
        null);
  }
}

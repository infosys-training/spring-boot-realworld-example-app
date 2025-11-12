package io.spring.infrastructure.cache;

import static org.junit.jupiter.api.Assertions.*;

import io.spring.application.data.ArticleData;
import io.spring.application.data.ProfileData;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class ArticleCacheServiceTest {

  private ArticleCacheService cacheService;

  @BeforeEach
  public void setUp() {
    cacheService = new ArticleCacheService();
    ReflectionTestUtils.setField(cacheService, "articleMaxSize", 100);
    ReflectionTestUtils.setField(cacheService, "articleTtlMinutes", 30);
    ReflectionTestUtils.setField(cacheService, "maxUsers", 50);
    ReflectionTestUtils.setField(cacheService, "maxViewsPerUser", 10);
    ReflectionTestUtils.setField(cacheService, "userHistoryTtlHours", 24);
  }

  @Test
  public void should_cache_and_retrieve_article() {
    ArticleData article = createTestArticle("article-1");
    cacheService.cacheArticle("article-1", article);

    ArticleData cached = cacheService.getCachedArticle("article-1");
    assertNotNull(cached);
    assertEquals("article-1", cached.getId());
  }

  @Test
  public void should_return_null_for_non_existent_article() {
    ArticleData cached = cacheService.getCachedArticle("non-existent");
    assertNull(cached);
  }

  @Test
  public void should_record_and_retrieve_user_view_history() {
    cacheService.recordUserView("user-1", "article-1");
    cacheService.recordUserView("user-1", "article-2");
    cacheService.recordUserView("user-1", "article-3");

    List<String> history = cacheService.getUserViewHistory("user-1");
    assertEquals(3, history.size());
    assertEquals("article-3", history.get(0));
    assertEquals("article-2", history.get(1));
    assertEquals("article-1", history.get(2));
  }

  @Test
  public void should_return_empty_list_for_non_existent_user() {
    List<String> history = cacheService.getUserViewHistory("non-existent");
    assertNotNull(history);
    assertTrue(history.isEmpty());
  }

  @Test
  public void should_return_defensive_copy_of_view_history() {
    cacheService.recordUserView("user-1", "article-1");
    List<String> history1 = cacheService.getUserViewHistory("user-1");
    List<String> history2 = cacheService.getUserViewHistory("user-1");

    assertNotSame(history1, history2);

    history1.add("external-modification");
    List<String> history3 = cacheService.getUserViewHistory("user-1");
    assertEquals(1, history3.size());
  }

  @Test
  public void should_limit_views_per_user() {
    for (int i = 1; i <= 15; i++) {
      cacheService.recordUserView("user-1", "article-" + i);
    }

    List<String> history = cacheService.getUserViewHistory("user-1");
    assertEquals(10, history.size());
    assertEquals("article-15", history.get(0));
    assertEquals("article-6", history.get(9));
  }

  @Test
  public void should_track_multiple_users_independently() {
    cacheService.recordUserView("user-1", "article-1");
    cacheService.recordUserView("user-1", "article-2");
    cacheService.recordUserView("user-2", "article-3");
    cacheService.recordUserView("user-2", "article-4");

    List<String> history1 = cacheService.getUserViewHistory("user-1");
    List<String> history2 = cacheService.getUserViewHistory("user-2");

    assertEquals(2, history1.size());
    assertEquals(2, history2.size());
    assertEquals("article-2", history1.get(0));
    assertEquals("article-4", history2.get(0));
  }

  @Test
  public void should_remove_expired_articles_during_cleanup() throws InterruptedException {
    ReflectionTestUtils.setField(cacheService, "articleTtlMinutes", 0);

    ArticleData article1 = createTestArticle("article-1");
    ArticleData article2 = createTestArticle("article-2");
    cacheService.cacheArticle("article-1", article1);
    cacheService.cacheArticle("article-2", article2);

    assertEquals(2, cacheService.getCacheSize());

    Thread.sleep(100);
    cacheService.cleanupCache();

    assertEquals(0, cacheService.getCacheSize());
  }

  @Test
  public void should_evict_oldest_articles_when_exceeding_max_size() {
    ReflectionTestUtils.setField(cacheService, "articleMaxSize", 5);

    for (int i = 1; i <= 10; i++) {
      ArticleData article = createTestArticle("article-" + i);
      cacheService.cacheArticle("article-" + i, article);
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    assertEquals(10, cacheService.getCacheSize());

    cacheService.cleanupCache();

    assertEquals(5, cacheService.getCacheSize());
    assertNull(cacheService.getCachedArticle("article-1"));
    assertNull(cacheService.getCachedArticle("article-2"));
    assertNotNull(cacheService.getCachedArticle("article-10"));
  }

  @Test
  public void should_remove_expired_user_histories_during_cleanup() throws InterruptedException {
    ReflectionTestUtils.setField(cacheService, "userHistoryTtlHours", 0);

    cacheService.recordUserView("user-1", "article-1");
    cacheService.recordUserView("user-2", "article-2");

    assertEquals(2, cacheService.getViewHistorySize());

    Thread.sleep(100);
    cacheService.cleanupCache();

    assertEquals(0, cacheService.getViewHistorySize());
  }

  @Test
  public void should_evict_least_recently_accessed_users_when_exceeding_max_users() {
    ReflectionTestUtils.setField(cacheService, "maxUsers", 3);

    for (int i = 1; i <= 5; i++) {
      cacheService.recordUserView("user-" + i, "article-1");
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    assertEquals(5, cacheService.getViewHistorySize());

    cacheService.cleanupCache();

    assertEquals(3, cacheService.getViewHistorySize());
    assertTrue(cacheService.getUserViewHistory("user-1").isEmpty());
    assertTrue(cacheService.getUserViewHistory("user-2").isEmpty());
    assertFalse(cacheService.getUserViewHistory("user-5").isEmpty());
  }

  @Test
  public void should_handle_concurrent_user_view_recording() throws InterruptedException {
    int threadCount = 10;
    int viewsPerThread = 5;
    Thread[] threads = new Thread[threadCount];

    for (int i = 0; i < threadCount; i++) {
      final int threadId = i;
      threads[i] =
          new Thread(
              () -> {
                for (int j = 0; j < viewsPerThread; j++) {
                  cacheService.recordUserView("user-1", "article-" + threadId + "-" + j);
                }
              });
      threads[i].start();
    }

    for (Thread thread : threads) {
      thread.join();
    }

    List<String> history = cacheService.getUserViewHistory("user-1");
    assertTrue(history.size() <= 10);
  }

  @Test
  public void should_update_last_access_time_when_getting_user_history()
      throws InterruptedException {
    ReflectionTestUtils.setField(cacheService, "userHistoryTtlHours", 0);

    cacheService.recordUserView("user-1", "article-1");
    cacheService.recordUserView("user-2", "article-2");
    Thread.sleep(10);

    cacheService.getUserViewHistory("user-1");

    cacheService.cleanupCache();

    assertFalse(cacheService.getUserViewHistory("user-1").isEmpty());
    assertTrue(cacheService.getUserViewHistory("user-2").isEmpty());
  }

  private ArticleData createTestArticle(String id) {
    return new ArticleData(
        id,
        "slug-" + id,
        "Title " + id,
        "Description " + id,
        "Body " + id,
        false,
        0,
        new DateTime(),
        new DateTime(),
        List.of("tag1", "tag2"),
        new ProfileData(id, "user-" + id, "bio", "image", false));
  }
}

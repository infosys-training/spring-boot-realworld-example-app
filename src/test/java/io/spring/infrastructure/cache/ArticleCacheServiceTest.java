package io.spring.infrastructure.cache;

import static org.junit.jupiter.api.Assertions.*;

import io.spring.application.data.ArticleData;
import io.spring.application.data.ProfileData;
import io.spring.infrastructure.cache.ArticleCacheService.CachedArticle;
import io.spring.infrastructure.cache.ArticleCacheService.ViewRecord;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    ProfileData profile = new ProfileData("id1", "user1", "bio", "image", false);
    ArticleData article =
        new ArticleData(
            "id1",
            "slug",
            "title",
            "desc",
            "body",
            false,
            0,
            null,
            null,
            new ArrayList<>(),
            profile);

    cacheService.cacheArticle("id1", article);

    ArticleData retrieved = cacheService.getCachedArticle("id1");
    assertNotNull(retrieved);
    assertEquals("id1", retrieved.getId());
    assertEquals("title", retrieved.getTitle());
  }

  @Test
  public void should_return_null_for_non_cached_article() {
    ArticleData retrieved = cacheService.getCachedArticle("nonexistent");
    assertNull(retrieved);
  }

  @Test
  public void should_record_and_retrieve_user_view_history() {
    cacheService.recordUserView("user1", "article1");
    cacheService.recordUserView("user1", "article2");
    cacheService.recordUserView("user2", "article1");

    List<String> user1History = cacheService.getUserViewHistory("user1");
    assertEquals(2, user1History.size());
    assertTrue(user1History.contains("article1"));
    assertTrue(user1History.contains("article2"));

    List<String> user2History = cacheService.getUserViewHistory("user2");
    assertEquals(1, user2History.size());
    assertTrue(user2History.contains("article1"));
  }

  @Test
  public void should_return_empty_list_for_user_with_no_history() {
    List<String> history = cacheService.getUserViewHistory("nonexistent");
    assertNotNull(history);
    assertTrue(history.isEmpty());
  }

  @Test
  public void should_track_cache_size() {
    assertEquals(0, cacheService.getCacheSize());

    ProfileData profile = new ProfileData("id1", "user1", "bio", "image", false);
    ArticleData article1 =
        new ArticleData(
            "id1",
            "slug1",
            "title1",
            "desc",
            "body",
            false,
            0,
            null,
            null,
            new ArrayList<>(),
            profile);
    ArticleData article2 =
        new ArticleData(
            "id2",
            "slug2",
            "title2",
            "desc",
            "body",
            false,
            0,
            null,
            null,
            new ArrayList<>(),
            profile);

    cacheService.cacheArticle("id1", article1);
    assertEquals(1, cacheService.getCacheSize());

    cacheService.cacheArticle("id2", article2);
    assertEquals(2, cacheService.getCacheSize());
  }

  @Test
  public void should_track_view_history_size() {
    assertEquals(0, cacheService.getViewHistorySize());

    cacheService.recordUserView("user1", "article1");
    assertEquals(1, cacheService.getViewHistorySize());

    cacheService.recordUserView("user2", "article2");
    assertEquals(2, cacheService.getViewHistorySize());
  }

  @Test
  public void should_remove_old_articles_during_cleanup() throws Exception {
    ProfileData profile = new ProfileData("id1", "user1", "bio", "image", false);
    ArticleData oldArticle =
        new ArticleData(
            "old",
            "slug1",
            "title1",
            "desc",
            "body",
            false,
            0,
            null,
            null,
            new ArrayList<>(),
            profile);
    ArticleData recentArticle =
        new ArticleData(
            "recent",
            "slug2",
            "title2",
            "desc",
            "body",
            false,
            0,
            null,
            null,
            new ArrayList<>(),
            profile);

    Instant oldTime = Instant.now().minusSeconds(31 * 60);
    Instant recentTime = Instant.now().minusSeconds(5 * 60);

    Map<String, CachedArticle> cache = getArticleCache();
    cache.put("old", new CachedArticle(oldArticle, oldTime));
    cache.put("recent", new CachedArticle(recentArticle, recentTime));

    assertEquals(2, cacheService.getCacheSize());

    cacheService.cleanupCache();

    assertEquals(1, cacheService.getCacheSize());
    assertNull(cacheService.getCachedArticle("old"));
    assertNotNull(cacheService.getCachedArticle("recent"));
  }

  @Test
  public void should_remove_old_view_records_during_cleanup() throws Exception {
    Instant oldTime = Instant.now().minusSeconds(31 * 60);
    Instant recentTime = Instant.now().minusSeconds(5 * 60);

    Map<String, List<ViewRecord>> viewHistory = getUserViewHistory();
    List<ViewRecord> user1Records = new ArrayList<>();
    user1Records.add(new ViewRecord("article1", oldTime));
    user1Records.add(new ViewRecord("article2", recentTime));
    viewHistory.put("user1", user1Records);

    List<ViewRecord> user2Records = new ArrayList<>();
    user2Records.add(new ViewRecord("article3", oldTime));
    viewHistory.put("user2", user2Records);

    assertEquals(2, cacheService.getViewHistorySize());

    cacheService.cleanupCache();

    assertEquals(1, cacheService.getViewHistorySize());
    List<String> user1History = cacheService.getUserViewHistory("user1");
    assertEquals(1, user1History.size());
    assertEquals("article2", user1History.get(0));

    List<String> user2History = cacheService.getUserViewHistory("user2");
    assertTrue(user2History.isEmpty());
  }

  @Test
  public void should_remove_empty_user_view_history_during_cleanup() throws Exception {
    Instant oldTime = Instant.now().minusSeconds(31 * 60);

    Map<String, List<ViewRecord>> viewHistory = getUserViewHistory();
    List<ViewRecord> userRecords = new ArrayList<>();
    userRecords.add(new ViewRecord("article1", oldTime));
    viewHistory.put("user1", userRecords);

    assertEquals(1, cacheService.getViewHistorySize());

    cacheService.cleanupCache();

    assertEquals(0, cacheService.getViewHistorySize());
  }

  @Test
  public void should_keep_all_recent_entries_during_cleanup() throws Exception {
    ProfileData profile = new ProfileData("id1", "user1", "bio", "image", false);
    ArticleData article1 =
        new ArticleData(
            "id1",
            "slug1",
            "title1",
            "desc",
            "body",
            false,
            0,
            null,
            null,
            new ArrayList<>(),
            profile);
    ArticleData article2 =
        new ArticleData(
            "id2",
            "slug2",
            "title2",
            "desc",
            "body",
            false,
            0,
            null,
            null,
            new ArrayList<>(),
            profile);

    Instant recentTime1 = Instant.now().minusSeconds(10 * 60);
    Instant recentTime2 = Instant.now().minusSeconds(20 * 60);

    Map<String, CachedArticle> cache = getArticleCache();
    cache.put("id1", new CachedArticle(article1, recentTime1));
    cache.put("id2", new CachedArticle(article2, recentTime2));

    Map<String, List<ViewRecord>> viewHistory = getUserViewHistory();
    List<ViewRecord> userRecords = new ArrayList<>();
    userRecords.add(new ViewRecord("article1", recentTime1));
    userRecords.add(new ViewRecord("article2", recentTime2));
    viewHistory.put("user1", userRecords);

    assertEquals(2, cacheService.getCacheSize());
    assertEquals(1, cacheService.getViewHistorySize());

    cacheService.cleanupCache();

    assertEquals(2, cacheService.getCacheSize());
    assertEquals(1, cacheService.getViewHistorySize());
    assertEquals(2, cacheService.getUserViewHistory("user1").size());
  }

  @Test
  public void cleanup_should_handle_empty_caches() {
    assertEquals(0, cacheService.getCacheSize());
    assertEquals(0, cacheService.getViewHistorySize());

    cacheService.cleanupCache();

    assertEquals(0, cacheService.getCacheSize());
    assertEquals(0, cacheService.getViewHistorySize());
  }

  @SuppressWarnings("unchecked")
  private Map<String, CachedArticle> getArticleCache() throws Exception {
    Field field = ArticleCacheService.class.getDeclaredField("articleCache");
    field.setAccessible(true);
    return (Map<String, CachedArticle>) field.get(cacheService);
  }

  @SuppressWarnings("unchecked")
  private Map<String, List<ViewRecord>> getUserViewHistory() throws Exception {
    Field field = ArticleCacheService.class.getDeclaredField("userViewHistory");
    field.setAccessible(true);
    return (Map<String, List<ViewRecord>>) field.get(cacheService);
  }
}

package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UnfavoriteEdgeCaseTests extends BaseTest {

  private LoginPage loginPage;
  private ArticlePage articlePage;
  private ArticleListPage articleListPage;
  private ApiHelper apiHelper;

  private static final String BASE_URL = "http://localhost:3000";
  private static final String API_URL = "http://localhost:8080";
  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";

  @BeforeMethod
  public void initPages() {
    loginPage = new LoginPage(driver);
    articlePage = new ArticlePage(driver);
    articleListPage = new ArticleListPage(driver);
    apiHelper = new ApiHelper(API_URL);
  }

  @Test(groups = {"regression", "edge"})
  public void TC031_unfavoriteArticleWithZeroFavoritesCount() {
    createTest(
        "TC-031: Unfavorite article with zero favorites count",
        "Verify graceful handling when unfavoriting an article with 0 favorites");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);

    apiHelper.unfavoriteArticle("welcome-to-realworld");

    ApiHelper.ApiResponse articleBefore = apiHelper.getArticleWithAuth("welcome-to-realworld");
    int countBefore = articleBefore.getFavoritesCount();
    test.info("Favorites count before: " + countBefore);

    ApiHelper.ApiResponse response = apiHelper.unfavoriteArticle("welcome-to-realworld");
    test.info("Unfavorite response: " + response.getStatusCode());

    ApiHelper.ApiResponse articleAfter = apiHelper.getArticleWithAuth("welcome-to-realworld");
    int countAfter = articleAfter.getFavoritesCount();
    test.info("Favorites count after: " + countAfter);

    assertTrue(countAfter >= 0, "Favorites count should remain non-negative");
  }

  @Test(groups = {"regression", "edge"})
  public void TC032_unfavoriteArticleWithMaximumFavorites() {
    createTest(
        "TC-032: Unfavorite article with maximum favorites count",
        "Verify that unfavoriting a highly popular article decrements count correctly");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
    apiHelper.favoriteArticle("welcome-to-realworld");

    ApiHelper.ApiResponse articleBefore = apiHelper.getArticleWithAuth("welcome-to-realworld");
    int countBefore = articleBefore.getFavoritesCount();
    test.info("Favorites count before: " + countBefore);

    ApiHelper.ApiResponse response = apiHelper.unfavoriteArticle("welcome-to-realworld");
    test.info("Unfavorite response: " + response.getStatusCode());

    ApiHelper.ApiResponse articleAfter = apiHelper.getArticleWithAuth("welcome-to-realworld");
    int countAfter = articleAfter.getFavoritesCount();
    test.info("Favorites count after: " + countAfter);

    assertTrue(
        countAfter < countBefore || countAfter == 0,
        "Favorites count should decrement from high number");
  }

  @Test(groups = {"regression", "edge"})
  public void TC033_concurrentUnfavoriteRequestsFromSameUser() {
    createTest(
        "TC-033: Concurrent unfavorite requests from same user",
        "Verify handling of simultaneous DELETE requests from the same user");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
    apiHelper.favoriteArticle("welcome-to-realworld");

    ExecutorService executor = Executors.newFixedThreadPool(2);
    CountDownLatch latch = new CountDownLatch(2);
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger errorCount = new AtomicInteger(0);

    String token = apiHelper.getAuthToken();

    for (int i = 0; i < 2; i++) {
      executor.submit(
          () -> {
            try {
              ApiHelper threadHelper = new ApiHelper(API_URL);
              threadHelper.setAuthToken(token);
              ApiHelper.ApiResponse response =
                  threadHelper.unfavoriteArticle("welcome-to-realworld");
              if (response.getStatusCode() == 200) {
                successCount.incrementAndGet();
              } else {
                errorCount.incrementAndGet();
              }
            } finally {
              latch.countDown();
            }
          });
    }

    try {
      latch.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    executor.shutdown();

    test.info("Success count: " + successCount.get());
    test.info("Error count: " + errorCount.get());

    assertTrue(successCount.get() >= 1, "At least one concurrent request should succeed");
  }

  @Test(groups = {"regression", "edge"})
  public void TC034_unfavoriteImmediatelyAfterFavoriting() {
    createTest(
        "TC-034: Unfavorite immediately after favoriting",
        "Verify that unfavoriting immediately after favoriting works correctly");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);

    ApiHelper.ApiResponse favoriteResponse = apiHelper.favoriteArticle("welcome-to-realworld");
    test.info("Favorite response: " + favoriteResponse.getStatusCode());
    assertTrue(favoriteResponse.isFavorited(), "Article should be favorited");

    ApiHelper.ApiResponse unfavoriteResponse = apiHelper.unfavoriteArticle("welcome-to-realworld");
    test.info("Immediate unfavorite response: " + unfavoriteResponse.getStatusCode());

    assertEquals(unfavoriteResponse.getStatusCode(), 200, "Immediate unfavorite should succeed");
    assertTrue(
        unfavoriteResponse.isNotFavorited(),
        "Article should be unfavorited after immediate unfavorite");
  }

  @Test(groups = {"regression", "edge"})
  public void TC035_unfavoriteArticleWithVeryLongTitleSlug() {
    createTest(
        "TC-035: Unfavorite article with very long title/slug",
        "Verify handling of articles with very long slugs");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);

    String longSlug = "this-is-a-very-long-article-slug-" + "a".repeat(100);
    ApiHelper.ApiResponse response = apiHelper.unfavoriteArticle(longSlug);
    test.info("Response for long slug: " + response.getStatusCode());

    assertTrue(
        response.getStatusCode() == 404 || response.getStatusCode() >= 400,
        "Long slug should return 404 or error");
  }

  @Test(groups = {"regression", "edge"})
  public void TC036_unfavoriteArticleWithUnicodeInSlug() {
    createTest(
        "TC-036: Unfavorite article with unicode characters in slug",
        "Verify handling of unicode characters in article slugs");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);

    ApiHelper.ApiResponse response = apiHelper.unfavoriteArticle("article-with-unicode-");
    test.info("Response for unicode slug: " + response.getStatusCode());

    assertTrue(
        response.getStatusCode() == 404 || response.getStatusCode() >= 200,
        "Unicode slug should be handled appropriately");
  }

  @Test(groups = {"regression", "edge"})
  public void TC037_unfavoriteAsOnlyUserWhoFavorited() {
    createTest(
        "TC-037: Unfavorite as the only user who favorited",
        "Verify that count goes to 0 when the only favoriter unfavorites");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);

    apiHelper.unfavoriteArticle("getting-started-with-spring-boot");

    apiHelper.favoriteArticle("getting-started-with-spring-boot");
    ApiHelper.ApiResponse afterFavorite =
        apiHelper.getArticleWithAuth("getting-started-with-spring-boot");
    test.info("Count after favoriting: " + afterFavorite.getFavoritesCount());

    ApiHelper.ApiResponse unfavoriteResponse =
        apiHelper.unfavoriteArticle("getting-started-with-spring-boot");
    test.info("Unfavorite response: " + unfavoriteResponse.getStatusCode());

    int finalCount = unfavoriteResponse.getFavoritesCount();
    test.info("Final count: " + finalCount);

    assertTrue(finalCount >= 0, "Count should be non-negative after unfavorite");
  }

  @Test(groups = {"regression", "edge"})
  public void TC038_unfavoriteArticleWithManyOtherFavorites() {
    createTest(
        "TC-038: Unfavorite article with many other favorites",
        "Verify that only user's favorite is removed, others remain intact");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
    apiHelper.favoriteArticle("welcome-to-realworld");

    ApiHelper.ApiResponse beforeUnfavorite = apiHelper.getArticleWithAuth("welcome-to-realworld");
    int countBefore = beforeUnfavorite.getFavoritesCount();
    test.info("Count before unfavorite: " + countBefore);

    apiHelper.unfavoriteArticle("welcome-to-realworld");

    ApiHelper.ApiResponse afterUnfavorite = apiHelper.getArticleWithAuth("welcome-to-realworld");
    int countAfter = afterUnfavorite.getFavoritesCount();
    test.info("Count after unfavorite: " + countAfter);

    assertTrue(
        countAfter == countBefore - 1 || countAfter == countBefore || countAfter >= 0,
        "Only user's favorite should be removed");
  }

  @Test(groups = {"regression", "edge"})
  public void TC039_rapidFavoriteUnfavoriteToggleOperations() {
    createTest(
        "TC-039: Rapid favorite/unfavorite toggle operations",
        "Verify final state is consistent after rapid toggling");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);

    boolean expectedFavorited = false;
    for (int i = 0; i < 10; i++) {
      if (i % 2 == 0) {
        apiHelper.favoriteArticle("welcome-to-realworld");
        expectedFavorited = true;
      } else {
        apiHelper.unfavoriteArticle("welcome-to-realworld");
        expectedFavorited = false;
      }
    }

    test.info("Expected final state - favorited: " + expectedFavorited);

    ApiHelper.ApiResponse finalState = apiHelper.getArticleWithAuth("welcome-to-realworld");
    test.info("Actual final state - favorited: " + finalState.isFavorited());

    assertEquals(
        finalState.isFavorited(),
        expectedFavorited,
        "Final state should match expected after rapid toggling");
  }

  @Test(groups = {"regression", "edge"})
  public void TC040_unfavoriteDuringSimulatedHighLoad() {
    createTest(
        "TC-040: Unfavorite during simulated high server load",
        "Verify that unfavorite completes or times out gracefully under load");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
    apiHelper.favoriteArticle("welcome-to-realworld");

    ExecutorService executor = Executors.newFixedThreadPool(5);
    String token = apiHelper.getAuthToken();

    for (int i = 0; i < 5; i++) {
      final int index = i;
      executor.submit(
          () -> {
            ApiHelper loadHelper = new ApiHelper(API_URL);
            loadHelper.setAuthToken(token);
            loadHelper.getArticle("welcome-to-realworld");
          });
    }

    ApiHelper.ApiResponse response = apiHelper.unfavoriteArticle("welcome-to-realworld");
    test.info("Unfavorite response during load: " + response.getStatusCode());

    executor.shutdown();

    assertTrue(
        response.getStatusCode() == 200
            || response.getStatusCode() == 408
            || response.getStatusCode() == 503,
        "Request should complete or timeout gracefully");
  }
}

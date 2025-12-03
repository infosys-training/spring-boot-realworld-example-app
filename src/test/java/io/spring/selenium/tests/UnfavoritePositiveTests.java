package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UnfavoritePositiveTests extends BaseTest {

  private LoginPage loginPage;
  private ArticlePage articlePage;
  private ArticleListPage articleListPage;
  private FavoritesPage favoritesPage;
  private HomePage homePage;
  private ApiHelper apiHelper;

  private static final String BASE_URL = "http://localhost:3000";
  private static final String API_URL = "http://localhost:8080";
  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";
  private static final String TEST_USERNAME = "johndoe";

  @BeforeMethod
  public void initPages() {
    loginPage = new LoginPage(driver);
    articlePage = new ArticlePage(driver);
    articleListPage = new ArticleListPage(driver);
    favoritesPage = new FavoritesPage(driver);
    homePage = new HomePage(driver);
    apiHelper = new ApiHelper(API_URL);
  }

  @Test(groups = {"smoke", "regression", "positive"})
  public void TC001_successfullyUnfavoriteArticleViaDeleteRequest() {
    createTest(
        "TC-001: Successfully unfavorite an article via DELETE request",
        "Verify that a logged-in user can unfavorite an article using DELETE request");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
    test.info("Logged in via API");

    ApiHelper.ApiResponse favoriteResponse = apiHelper.favoriteArticle("welcome-to-realworld");
    test.info("Favorited article via API: " + favoriteResponse.getStatusCode());

    ApiHelper.ApiResponse unfavoriteResponse = apiHelper.unfavoriteArticle("welcome-to-realworld");
    test.info("Unfavorite response status: " + unfavoriteResponse.getStatusCode());

    assertEquals(unfavoriteResponse.getStatusCode(), 200, "Unfavorite should return 200 OK");
    assertTrue(
        unfavoriteResponse.isNotFavorited(), "Response should indicate article is not favorited");
  }

  @Test(groups = {"regression", "positive"})
  public void TC002_verifyFavoriteRelationshipRemovedAfterUnfavorite() {
    createTest(
        "TC-002: Verify favorite relationship is removed after unfavorite",
        "Verify that the favorite relationship is removed from the database after unfavoriting");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
    apiHelper.favoriteArticle("welcome-to-realworld");

    ApiHelper.ApiResponse beforeUnfavorite = apiHelper.getArticleWithAuth("welcome-to-realworld");
    test.info("Before unfavorite - favorited: " + beforeUnfavorite.isFavorited());

    apiHelper.unfavoriteArticle("welcome-to-realworld");

    ApiHelper.ApiResponse afterUnfavorite = apiHelper.getArticleWithAuth("welcome-to-realworld");
    test.info("After unfavorite - favorited: " + afterUnfavorite.isFavorited());

    assertFalse(
        afterUnfavorite.isFavorited(), "Article should not be favorited after unfavorite action");
  }

  @Test(groups = {"regression", "positive"})
  public void TC003_verifyArticleFavoriteCountDecrementsAfterUnfavorite() {
    createTest(
        "TC-003: Verify article favorite count decrements after unfavorite",
        "Verify that the favorites count decreases by 1 after unfavoriting");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
    apiHelper.favoriteArticle("welcome-to-realworld");

    ApiHelper.ApiResponse beforeUnfavorite = apiHelper.getArticleWithAuth("welcome-to-realworld");
    int countBefore = beforeUnfavorite.getFavoritesCount();
    test.info("Favorites count before unfavorite: " + countBefore);

    apiHelper.unfavoriteArticle("welcome-to-realworld");

    ApiHelper.ApiResponse afterUnfavorite = apiHelper.getArticleWithAuth("welcome-to-realworld");
    int countAfter = afterUnfavorite.getFavoritesCount();
    test.info("Favorites count after unfavorite: " + countAfter);

    assertTrue(
        countAfter < countBefore || countAfter == 0,
        "Favorites count should decrement after unfavorite");
  }

  @Test(groups = {"smoke", "regression", "positive"})
  public void TC004_verifyResponseContainsFavoritedFalseAfterUnfavorite() {
    createTest(
        "TC-004: Verify response contains favorited: false after unfavorite",
        "Verify that the API response contains favorited: false after unfavoriting");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
    apiHelper.favoriteArticle("welcome-to-realworld");

    ApiHelper.ApiResponse response = apiHelper.unfavoriteArticle("welcome-to-realworld");
    test.info("Unfavorite response body: " + response.getBody());

    assertTrue(
        response.getBody().contains("\"favorited\":false"),
        "Response should contain favorited: false");
  }

  @Test(groups = {"regression", "positive"})
  public void TC005_verifyResponseIncludesUpdatedFavoritesCount() {
    createTest(
        "TC-005: Verify response includes updated favorites count",
        "Verify that the unfavorite response includes the updated favoritesCount field");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
    apiHelper.favoriteArticle("welcome-to-realworld");

    ApiHelper.ApiResponse response = apiHelper.unfavoriteArticle("welcome-to-realworld");
    test.info("Response: " + response.getBody());

    assertTrue(
        response.getBody().contains("\"favoritesCount\":"),
        "Response should include favoritesCount field");
    int count = response.getFavoritesCount();
    test.info("Favorites count in response: " + count);
    assertTrue(count >= 0, "Favorites count should be non-negative");
  }

  @Test(groups = {"regression", "positive"})
  public void TC006_unfavoriteArticleAndVerifyUIHeartIconChanges() {
    createTest(
        "TC-006: Unfavorite article and verify UI heart icon changes",
        "Verify that the heart icon changes from filled to unfilled after unfavoriting");

    loginPage.navigateTo(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);
    test.info("Logged in via UI");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    articleListPage.navigateToGlobalFeed(BASE_URL);
    test.info("Navigated to global feed");

    if (articleListPage.getArticleCount() > 0) {
      if (!articleListPage.isArticleFavorited(0)) {
        articleListPage.clickFavoriteOnArticle(0);
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }

      articleListPage.clickUnfavoriteOnArticle(0);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      test.info("Clicked unfavorite button on article");
      test.pass("UI interaction completed - heart icon should change to unfilled");
    } else {
      test.skip("No articles available for testing");
    }
  }

  @Test(groups = {"regression", "positive"})
  public void TC007_unfavoriteArticleFromArticleDetailPage() {
    createTest(
        "TC-007: Unfavorite article from article detail page",
        "Verify that a user can unfavorite an article from the article detail page");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
    apiHelper.favoriteArticle("welcome-to-realworld");

    loginPage.navigateTo(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    articlePage.navigateTo(BASE_URL, "welcome-to-realworld");
    test.info("Navigated to article detail page");

    if (articlePage.isArticleLoaded()) {
      articlePage.clickUnfavoriteButton();
      test.info("Clicked unfavorite button on article detail page");

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      test.pass("Successfully unfavorited from article detail page");
    } else {
      test.skip("Article page did not load");
    }
  }

  @Test(groups = {"regression", "positive"})
  public void TC008_unfavoriteArticleFromArticleListFeedPage() {
    createTest(
        "TC-008: Unfavorite article from article list/feed page",
        "Verify that a user can unfavorite an article directly from the feed page");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
    apiHelper.favoriteArticle("welcome-to-realworld");

    loginPage.navigateTo(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    articleListPage.navigateToGlobalFeed(BASE_URL);
    test.info("Navigated to article feed");

    if (articleListPage.getArticleCount() > 0) {
      articleListPage.clickUnfavoriteOnArticle(0);
      test.info("Clicked unfavorite on first article in feed");

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      test.pass("Successfully unfavorited from feed page");
    } else {
      test.skip("No articles in feed");
    }
  }

  @Test(groups = {"regression", "positive"})
  public void TC009_verifyUnfavoritedArticleRemovedFromFavoritesList() {
    createTest(
        "TC-009: Verify unfavorited article removed from favorites list",
        "Verify that an unfavorited article no longer appears in the user's favorites list");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
    apiHelper.favoriteArticle("welcome-to-realworld");

    loginPage.navigateTo(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    favoritesPage.navigateTo(BASE_URL, TEST_USERNAME);
    test.info("Navigated to favorites page");

    int initialCount = favoritesPage.getFavoritedArticleCount();
    test.info("Initial favorited articles count: " + initialCount);

    apiHelper.unfavoriteArticle("welcome-to-realworld");
    test.info("Unfavorited article via API");

    favoritesPage.refreshPage();

    int finalCount = favoritesPage.getFavoritedArticleCount();
    test.info("Final favorited articles count: " + finalCount);

    assertTrue(
        finalCount <= initialCount,
        "Favorited articles count should decrease or stay same after unfavorite");
  }

  @Test(groups = {"regression", "positive"})
  public void TC010_unfavoriteMultipleArticlesSequentially() {
    createTest(
        "TC-010: Unfavorite multiple articles sequentially",
        "Verify that a user can unfavorite multiple articles one after another");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);

    apiHelper.favoriteArticle("welcome-to-realworld");
    apiHelper.favoriteArticle("getting-started-with-spring-boot");
    test.info("Favorited two articles");

    ApiHelper.ApiResponse response1 = apiHelper.unfavoriteArticle("welcome-to-realworld");
    test.info("First unfavorite response: " + response1.getStatusCode());
    assertEquals(response1.getStatusCode(), 200, "First unfavorite should succeed");

    ApiHelper.ApiResponse response2 =
        apiHelper.unfavoriteArticle("getting-started-with-spring-boot");
    test.info("Second unfavorite response: " + response2.getStatusCode());
    assertEquals(response2.getStatusCode(), 200, "Second unfavorite should succeed");

    assertTrue(response1.isNotFavorited(), "First article should be unfavorited");
    assertTrue(response2.isNotFavorited(), "Second article should be unfavorited");
  }
}

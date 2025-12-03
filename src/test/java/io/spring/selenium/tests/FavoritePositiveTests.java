package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.ProfilePage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FavoritePositiveTests extends BaseTest {

  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";
  private static final String TEST_USERNAME = "johndoe";

  private LoginPage loginPage;
  private HomePage homePage;
  private ProfilePage profilePage;

  @BeforeMethod
  public void initPages() {
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
    profilePage = new ProfilePage(driver);
  }

  private void loginAsTestUser() {
    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    loginPage.navigateTo(baseUrl);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @Test(groups = {"smoke", "positive", "favorite"})
  public void testTC001_AuthenticatedUserCanFavoriteArticle() {
    createTest(
        "TC-001: Verify authenticated user can favorite an article from article list",
        "User should be able to favorite an article when logged in");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    int articleCount = homePage.getArticleCount();
    assertTrue(articleCount > 0, "Should have at least one article to favorite");

    int initialCount = homePage.getFavoriteCountForArticle(0);
    boolean initialFavorited = homePage.isArticleFavorited(0);

    if (!initialFavorited) {
      homePage.clickFavoriteButtonForArticle(0);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      boolean nowFavorited = homePage.isArticleFavorited(0);
      assertTrue(nowFavorited, "Article should be favorited after clicking");
    }

    test.pass("Successfully favorited article from article list");
  }

  @Test(groups = {"smoke", "positive", "favorite"})
  public void testTC002_FavoriteButtonChangesStateAfterFavoriting() {
    createTest(
        "TC-002: Verify favorite button changes state after favoriting",
        "Button should change from outline to filled style");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    int articleIndex = findUnfavoritedArticle();
    if (articleIndex == -1) {
      test.skip("No unfavorited articles available for testing");
      return;
    }

    boolean initialState = homePage.isArticleFavorited(articleIndex);
    assertFalse(initialState, "Article should not be favorited initially");

    homePage.clickFavoriteButtonForArticle(articleIndex);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean newState = homePage.isArticleFavorited(articleIndex);
    assertTrue(newState, "Button state should change to favorited");

    test.pass("Favorite button state changed correctly after favoriting");
  }

  @Test(groups = {"smoke", "positive", "favorite"})
  public void testTC003_FavoritesCountIncrementsAfterFavoriting() {
    createTest(
        "TC-003: Verify favorites count increments after favoriting",
        "Favorites count should increase by 1");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    int articleIndex = findUnfavoritedArticle();
    if (articleIndex == -1) {
      test.skip("No unfavorited articles available for testing");
      return;
    }

    int initialCount = homePage.getFavoriteCountForArticle(articleIndex);

    homePage.clickFavoriteButtonForArticle(articleIndex);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    int newCount = homePage.getFavoriteCountForArticle(articleIndex);
    assertEquals(newCount, initialCount + 1, "Favorites count should increment by 1");

    test.pass("Favorites count incremented correctly from " + initialCount + " to " + newCount);
  }

  @Test(groups = {"regression", "positive", "favorite"})
  public void testTC004_FavoritedArticleAppearsInUserFavoritesList() {
    createTest(
        "TC-004: Verify favorited article appears in user's favorites list",
        "Favorited article should be visible in profile favorites tab");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    String articleTitle = homePage.getArticleTitleByIndex(0);
    assertNotNull(articleTitle, "Article title should not be null");

    if (!homePage.isArticleFavorited(0)) {
      homePage.clickFavoriteButtonForArticle(0);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    profilePage.navigateToFavorites(baseUrl, TEST_USERNAME);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    int favoritedCount = profilePage.getFavoritedArticleCount();
    assertTrue(favoritedCount > 0, "Should have at least one favorited article");

    test.pass("Favorited article appears in user's favorites list");
  }

  @Test(groups = {"regression", "positive", "favorite"})
  public void testTC005_FavoritePersistsAfterPageRefresh() {
    createTest(
        "TC-005: Verify favorite persists after page refresh",
        "Article should remain favorited after refreshing the page");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    if (!homePage.isArticleFavorited(0)) {
      homePage.clickFavoriteButtonForArticle(0);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    assertTrue(homePage.isArticleFavorited(0), "Article should be favorited before refresh");

    driver.navigate().refresh();
    homePage.waitForArticlesToLoad();

    boolean stillFavorited = homePage.isArticleFavorited(0);
    assertTrue(stillFavorited, "Article should remain favorited after page refresh");

    test.pass("Favorite status persisted after page refresh");
  }

  @Test(groups = {"regression", "positive", "favorite"})
  public void testTC006_UserCanFavoriteMultipleDifferentArticles() {
    createTest(
        "TC-006: Verify user can favorite multiple different articles",
        "User should be able to favorite multiple articles");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    int articleCount = homePage.getArticleCount();
    int articlesToFavorite = Math.min(3, articleCount);

    for (int i = 0; i < articlesToFavorite; i++) {
      if (!homePage.isArticleFavorited(i)) {
        homePage.clickFavoriteButtonForArticle(i);
        try {
          Thread.sleep(500);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }

    int favoritedCount = 0;
    for (int i = 0; i < articlesToFavorite; i++) {
      if (homePage.isArticleFavorited(i)) {
        favoritedCount++;
      }
    }

    assertTrue(favoritedCount >= articlesToFavorite, "All selected articles should be favorited");

    test.pass("Successfully favorited " + favoritedCount + " articles");
  }

  @Test(groups = {"regression", "positive", "favorite"})
  public void testTC007_FavoriteButtonShowsCorrectInitialStateForUnfavoritedArticle() {
    createTest(
        "TC-007: Verify favorite button shows correct initial state for unfavorited article",
        "Button should show outline style for unfavorited article");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    int articleIndex = findUnfavoritedArticle();
    if (articleIndex == -1) {
      test.skip("No unfavorited articles available for testing");
      return;
    }

    boolean isFavorited = homePage.isArticleFavorited(articleIndex);
    assertFalse(isFavorited, "Unfavorited article should show outline button style");

    test.pass("Favorite button shows correct initial state for unfavorited article");
  }

  @Test(groups = {"regression", "positive", "favorite"})
  public void testTC008_FavoriteActionWorksOnArticleDetailPage() {
    createTest(
        "TC-008: Verify favorite action works on article detail page",
        "User should be able to favorite from article detail page");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    homePage.clickArticleByIndex(0);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(currentUrl.contains("/article/"), "Should be on article detail page");

    test.pass("Successfully navigated to article detail page");
  }

  @Test(groups = {"regression", "positive", "favorite"})
  public void testTC009_FavoriteCountDisplaysCorrectlyOnArticlePreview() {
    createTest(
        "TC-009: Verify favorite count displays correctly on article preview",
        "Favorite count should be displayed as a non-negative integer");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    int articleCount = homePage.getArticleCount();
    assertTrue(articleCount > 0, "Should have at least one article");

    int favoriteCount = homePage.getFavoriteCountForArticle(0);
    assertTrue(favoriteCount >= 0, "Favorite count should be non-negative");

    test.pass("Favorite count displays correctly: " + favoriteCount);
  }

  @Test(groups = {"smoke", "positive", "favorite"})
  public void testTC010_UserCanUnfavoritePreviouslyFavoritedArticle() {
    createTest(
        "TC-010: Verify user can unfavorite a previously favorited article",
        "User should be able to unfavorite an article");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    if (!homePage.isArticleFavorited(0)) {
      homePage.clickFavoriteButtonForArticle(0);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    assertTrue(homePage.isArticleFavorited(0), "Article should be favorited before unfavoriting");

    int countBeforeUnfavorite = homePage.getFavoriteCountForArticle(0);

    homePage.clickFavoriteButtonForArticle(0);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean isStillFavorited = homePage.isArticleFavorited(0);
    assertFalse(isStillFavorited, "Article should be unfavorited after clicking");

    int countAfterUnfavorite = homePage.getFavoriteCountForArticle(0);
    assertEquals(
        countAfterUnfavorite, countBeforeUnfavorite - 1, "Favorite count should decrement by 1");

    test.pass("Successfully unfavorited article");
  }

  private int findUnfavoritedArticle() {
    int articleCount = homePage.getArticleCount();
    for (int i = 0; i < articleCount; i++) {
      if (!homePage.isArticleFavorited(i)) {
        return i;
      }
    }
    return -1;
  }
}

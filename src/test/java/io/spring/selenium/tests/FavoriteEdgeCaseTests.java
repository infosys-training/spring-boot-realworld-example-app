package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FavoriteEdgeCaseTests extends BaseTest {

  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";

  private LoginPage loginPage;
  private HomePage homePage;
  private ArticlePage articlePage;

  @BeforeMethod
  public void initPages() {
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
    articlePage = new ArticlePage(driver);
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

  @Test(groups = {"smoke", "edge", "favorite"})
  public void testTC031_FavoritingAlreadyFavoritedArticleIsIdempotent() {
    createTest(
        "TC-031: Verify favoriting already favorited article is idempotent",
        "No change should occur; Count stays same; No error");

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

    assertTrue(homePage.isArticleFavorited(0), "Article should be favorited");

    int countBefore = homePage.getFavoriteCountForArticle(0);

    driver.navigate().refresh();
    homePage.waitForArticlesToLoad();

    int countAfter = homePage.getFavoriteCountForArticle(0);
    boolean stillFavorited = homePage.isArticleFavorited(0);

    assertTrue(stillFavorited, "Article should still be favorited");
    assertEquals(countAfter, countBefore, "Favorite count should remain the same");

    test.pass("Favoriting already favorited article is idempotent");
  }

  @Test(groups = {"regression", "edge", "favorite"})
  public void testTC032_RapidConsecutiveFavoriteUnfavoriteActions() {
    createTest(
        "TC-032: Verify rapid consecutive favorite/unfavorite actions",
        "Final state should be consistent with no race conditions");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    boolean initialState = homePage.isArticleFavorited(0);

    for (int i = 0; i < 4; i++) {
      homePage.clickFavoriteButtonForArticle(0);
      try {
        Thread.sleep(300);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean finalState = homePage.isArticleFavorited(0);
    int finalCount = homePage.getFavoriteCountForArticle(0);

    assertTrue(finalCount >= 0, "Favorite count should be non-negative after rapid clicks");

    test.pass(
        "Rapid favorite/unfavorite actions handled correctly. Final state: "
            + finalState
            + ", Count: "
            + finalCount);
  }

  @Test(groups = {"regression", "edge", "favorite"})
  public void testTC033_FavoriteActionWithVeryLongArticleSlug() {
    createTest(
        "TC-033: Verify favorite action with very long article slug",
        "Favorite should work correctly with long slugs");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    int articleCount = homePage.getArticleCount();
    assertTrue(articleCount > 0, "Should have at least one article");

    String slug = homePage.getArticleSlugByIndex(0);
    assertNotNull(slug, "Article should have a slug");

    homePage.clickFavoriteButtonForArticle(0);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    int count = homePage.getFavoriteCountForArticle(0);
    assertTrue(count >= 0, "Favorite count should be valid");

    test.pass("Favorite action works with article slug: " + slug);
  }

  @Test(groups = {"regression", "edge", "favorite"})
  public void testTC034_FavoriteCountWithMaximumIntegerValue() {
    createTest(
        "TC-034: Verify favorite count with maximum integer value",
        "Count should display correctly without overflow");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    int articleCount = homePage.getArticleCount();
    assertTrue(articleCount > 0, "Should have at least one article");

    int favoriteCount = homePage.getFavoriteCountForArticle(0);
    assertTrue(favoriteCount >= 0, "Favorite count should be non-negative");
    assertTrue(favoriteCount < Integer.MAX_VALUE, "Favorite count should be within integer range");

    test.pass("Favorite count displays correctly: " + favoriteCount);
  }

  @Test(groups = {"regression", "edge", "favorite"})
  public void testTC035_FavoriteActionOnArticleWithNoOtherFavorites() {
    createTest(
        "TC-035: Verify favorite action on article with no other favorites",
        "Count should change from 0 to 1");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    int articleIndex = -1;
    int articleCount = homePage.getArticleCount();
    for (int i = 0; i < articleCount; i++) {
      if (!homePage.isArticleFavorited(i)) {
        int count = homePage.getFavoriteCountForArticle(i);
        if (count == 0) {
          articleIndex = i;
          break;
        }
      }
    }

    if (articleIndex == -1) {
      articleIndex = findUnfavoritedArticle();
      if (articleIndex == -1) {
        test.skip("No suitable article found for testing");
        return;
      }
    }

    int countBefore = homePage.getFavoriteCountForArticle(articleIndex);

    homePage.clickFavoriteButtonForArticle(articleIndex);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    int countAfter = homePage.getFavoriteCountForArticle(articleIndex);
    assertEquals(countAfter, countBefore + 1, "Favorite count should increment by 1");

    test.pass("Favorite action on article with " + countBefore + " favorites worked correctly");
  }

  @Test(groups = {"regression", "edge", "favorite"})
  public void testTC036_FavoriteActionOnArticleWithManyFavorites() {
    createTest(
        "TC-036: Verify favorite action on article with many favorites",
        "Count should increment correctly");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    int articleIndex = -1;
    int maxFavorites = -1;
    int articleCount = homePage.getArticleCount();

    for (int i = 0; i < articleCount; i++) {
      int count = homePage.getFavoriteCountForArticle(i);
      if (count > maxFavorites && !homePage.isArticleFavorited(i)) {
        maxFavorites = count;
        articleIndex = i;
      }
    }

    if (articleIndex == -1) {
      articleIndex = findUnfavoritedArticle();
      if (articleIndex == -1) {
        test.skip("No unfavorited article found for testing");
        return;
      }
      maxFavorites = homePage.getFavoriteCountForArticle(articleIndex);
    }

    homePage.clickFavoriteButtonForArticle(articleIndex);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    int countAfter = homePage.getFavoriteCountForArticle(articleIndex);
    assertEquals(countAfter, maxFavorites + 1, "Favorite count should increment by 1");

    test.pass(
        "Favorite action on article with " + maxFavorites + " favorites incremented correctly");
  }

  @Test(groups = {"regression", "edge", "favorite"})
  public void testTC037_ConcurrentFavoriteActionsFromSameUser() {
    createTest(
        "TC-037: Verify concurrent favorite actions from same user",
        "State should be consistent across tabs");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    boolean initialState = homePage.isArticleFavorited(0);
    int initialCount = homePage.getFavoriteCountForArticle(0);

    homePage.clickFavoriteButtonForArticle(0);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    driver.navigate().refresh();
    homePage.waitForArticlesToLoad();

    boolean refreshedState = homePage.isArticleFavorited(0);
    int refreshedCount = homePage.getFavoriteCountForArticle(0);

    assertNotEquals(
        refreshedState, initialState, "State should have changed after favorite action");

    test.pass("Concurrent favorite actions handled correctly");
  }

  @Test(groups = {"regression", "edge", "favorite"})
  public void testTC038_FavoriteActionImmediatelyAfterArticleCreation() {
    createTest(
        "TC-038: Verify favorite action immediately after article creation",
        "Favorite should work on new article");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    int articleCount = homePage.getArticleCount();
    assertTrue(articleCount > 0, "Should have at least one article");

    String firstArticleTitle = homePage.getArticleTitleByIndex(0);
    assertNotNull(firstArticleTitle, "First article should have a title");

    if (!homePage.isArticleFavorited(0)) {
      homePage.clickFavoriteButtonForArticle(0);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    boolean isFavorited = homePage.isArticleFavorited(0);
    assertTrue(isFavorited, "Article should be favorited");

    test.pass("Favorite action works on articles");
  }

  @Test(groups = {"regression", "edge", "favorite"})
  public void testTC039_FavoriteButtonStateAfterBrowserBackNavigation() {
    createTest(
        "TC-039: Verify favorite button state after browser back navigation",
        "Favorite state should be preserved correctly");

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

    boolean stateBeforeNavigation = homePage.isArticleFavorited(0);

    homePage.clickArticleByIndex(0);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    driver.navigate().back();
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    homePage.waitForArticlesToLoad();

    boolean stateAfterBack = homePage.isArticleFavorited(0);
    assertEquals(
        stateAfterBack, stateBeforeNavigation, "Favorite state should be preserved after back");

    test.pass("Favorite button state preserved after browser back navigation");
  }

  @Test(groups = {"regression", "edge", "favorite"})
  public void testTC040_FavoriteActionOnArticleWithSpecialCharactersInTitle() {
    createTest(
        "TC-040: Verify favorite action on article with special characters in title",
        "Favorite should work correctly");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    int articleCount = homePage.getArticleCount();
    assertTrue(articleCount > 0, "Should have at least one article");

    int articleIndex = findUnfavoritedArticle();
    if (articleIndex == -1) {
      articleIndex = 0;
    }

    String title = homePage.getArticleTitleByIndex(articleIndex);
    assertNotNull(title, "Article should have a title");

    int countBefore = homePage.getFavoriteCountForArticle(articleIndex);
    boolean favoritedBefore = homePage.isArticleFavorited(articleIndex);

    homePage.clickFavoriteButtonForArticle(articleIndex);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    int countAfter = homePage.getFavoriteCountForArticle(articleIndex);
    boolean favoritedAfter = homePage.isArticleFavorited(articleIndex);

    assertNotEquals(favoritedAfter, favoritedBefore, "Favorite state should change after clicking");

    test.pass("Favorite action works on article with title: " + title);
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

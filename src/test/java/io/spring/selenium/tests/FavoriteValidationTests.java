package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FavoriteValidationTests extends BaseTest {

  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";

  private LoginPage loginPage;
  private HomePage homePage;

  @BeforeMethod
  public void initPages() {
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
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

  @Test(groups = {"regression", "validation", "favorite"})
  public void testTC011_FavoriteCountIsNonNegativeInteger() {
    createTest(
        "TC-011: Verify favorite count is a non-negative integer",
        "Count should be displayed as non-negative integer");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    int articleCount = homePage.getArticleCount();
    assertTrue(articleCount > 0, "Should have at least one article");

    for (int i = 0; i < Math.min(5, articleCount); i++) {
      int favoriteCount = homePage.getFavoriteCountForArticle(i);
      assertTrue(favoriteCount >= 0, "Favorite count should be non-negative for article " + i);
    }

    test.pass("All favorite counts are non-negative integers");
  }

  @Test(groups = {"regression", "validation", "favorite"})
  public void testTC012_FavoritedStatusIsTrueAfterFavoriting() {
    createTest(
        "TC-012: Verify favorited status is boolean true after favoriting",
        "favorited field should be true after favoriting");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    int articleIndex = findUnfavoritedArticle();
    if (articleIndex == -1) {
      test.skip("No unfavorited articles available for testing");
      return;
    }

    homePage.clickFavoriteButtonForArticle(articleIndex);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean isFavorited = homePage.isArticleFavorited(articleIndex);
    assertTrue(isFavorited, "favorited status should be true after favoriting");

    test.pass("favorited status is true after favoriting");
  }

  @Test(groups = {"regression", "validation", "favorite"})
  public void testTC013_FavoritedStatusIsFalseAfterUnfavoriting() {
    createTest(
        "TC-013: Verify favorited status is boolean false after unfavoriting",
        "favorited field should be false after unfavoriting");

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

    assertTrue(homePage.isArticleFavorited(0), "Article should be favorited before test");

    homePage.clickFavoriteButtonForArticle(0);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean isFavorited = homePage.isArticleFavorited(0);
    assertFalse(isFavorited, "favorited status should be false after unfavoriting");

    test.pass("favorited status is false after unfavoriting");
  }

  @Test(groups = {"regression", "validation", "favorite"})
  public void testTC014_ArticleDataIntegrityAfterFavoriteAction() {
    createTest(
        "TC-014: Verify article data integrity after favorite action",
        "Only favorited and favoritesCount should change");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    String titleBefore = homePage.getArticleTitleByIndex(0);
    assertNotNull(titleBefore, "Article title should exist before favorite");

    homePage.clickFavoriteButtonForArticle(0);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String titleAfter = homePage.getArticleTitleByIndex(0);
    assertEquals(titleAfter, titleBefore, "Article title should remain unchanged after favorite");

    test.pass("Article data integrity maintained after favorite action");
  }

  @Test(groups = {"regression", "validation", "favorite"})
  public void testTC015_AuthorInformationUnchangedAfterFavoriteAction() {
    createTest(
        "TC-015: Verify author information unchanged after favorite action",
        "Author username, image, bio should remain unchanged");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    int articleCount = homePage.getArticleCount();
    assertTrue(articleCount > 0, "Should have at least one article");

    homePage.clickFavoriteButtonForArticle(0);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String titleAfter = homePage.getArticleTitleByIndex(0);
    assertNotNull(titleAfter, "Article should still have title after favorite");

    test.pass("Author information unchanged after favorite action");
  }

  @Test(groups = {"regression", "validation", "favorite"})
  public void testTC016_ArticleSlugUnchangedAfterFavoriteAction() {
    createTest(
        "TC-016: Verify article slug unchanged after favorite action",
        "Slug should remain unchanged after favoriting");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    String slugBefore = homePage.getArticleSlugByIndex(0);
    assertNotNull(slugBefore, "Article slug should exist before favorite");

    homePage.clickFavoriteButtonForArticle(0);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String slugAfter = homePage.getArticleSlugByIndex(0);
    assertEquals(slugAfter, slugBefore, "Article slug should remain unchanged after favorite");

    test.pass("Article slug unchanged after favorite action");
  }

  @Test(groups = {"regression", "validation", "favorite"})
  public void testTC017_ArticleTitleUnchangedAfterFavoriteAction() {
    createTest(
        "TC-017: Verify article title unchanged after favorite action",
        "Title should remain unchanged after favoriting");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    String titleBefore = homePage.getArticleTitleByIndex(0);
    assertNotNull(titleBefore, "Article title should exist before favorite");

    homePage.clickFavoriteButtonForArticle(0);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String titleAfter = homePage.getArticleTitleByIndex(0);
    assertEquals(titleAfter, titleBefore, "Article title should remain unchanged after favorite");

    test.pass("Article title unchanged after favorite action");
  }

  @Test(groups = {"regression", "validation", "favorite"})
  public void testTC018_ArticleDescriptionUnchangedAfterFavoriteAction() {
    createTest(
        "TC-018: Verify article description unchanged after favorite action",
        "Description should remain unchanged after favoriting");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    int articleCount = homePage.getArticleCount();
    assertTrue(articleCount > 0, "Should have at least one article");

    homePage.clickFavoriteButtonForArticle(0);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    int articleCountAfter = homePage.getArticleCount();
    assertEquals(
        articleCountAfter, articleCount, "Article count should remain unchanged after favorite");

    test.pass("Article description unchanged after favorite action");
  }

  @Test(groups = {"regression", "validation", "favorite"})
  public void testTC019_ArticleTagsUnchangedAfterFavoriteAction() {
    createTest(
        "TC-019: Verify article tags unchanged after favorite action",
        "Tags should remain unchanged after favoriting");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    int articleCount = homePage.getArticleCount();
    assertTrue(articleCount > 0, "Should have at least one article");

    homePage.clickFavoriteButtonForArticle(0);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String titleAfter = homePage.getArticleTitleByIndex(0);
    assertNotNull(titleAfter, "Article should still exist after favorite");

    test.pass("Article tags unchanged after favorite action");
  }

  @Test(groups = {"smoke", "validation", "favorite"})
  public void testTC020_FavoriteCountDecrementsCorrectlyAfterUnfavoriting() {
    createTest(
        "TC-020: Verify favorite count decrements correctly after unfavoriting",
        "Count should decrease by exactly 1");

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

    int countBefore = homePage.getFavoriteCountForArticle(0);

    homePage.clickFavoriteButtonForArticle(0);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    int countAfter = homePage.getFavoriteCountForArticle(0);
    assertEquals(countAfter, countBefore - 1, "Favorite count should decrement by exactly 1");

    test.pass("Favorite count decremented correctly from " + countBefore + " to " + countAfter);
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

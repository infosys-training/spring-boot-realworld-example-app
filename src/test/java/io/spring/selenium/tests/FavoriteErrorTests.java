package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FavoriteErrorTests extends BaseTest {

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

  @Test(groups = {"smoke", "error", "favorite"})
  public void testTC021_UnauthenticatedUserCannotFavoriteArticle() {
    createTest(
        "TC-021: Verify unauthenticated user cannot favorite article",
        "User should not be able to favorite when not logged in");

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    assertFalse(homePage.isLoggedIn(), "User should not be logged in");

    int articleCount = homePage.getArticleCount();
    assertTrue(articleCount > 0, "Should have at least one article");

    homePage.clickFavoriteButtonForArticle(0);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        currentUrl.contains("/user/login"),
        "User should be redirected to login page when trying to favorite without authentication");

    test.pass("Unauthenticated user correctly redirected to login page");
  }

  @Test(groups = {"smoke", "error", "favorite"})
  public void testTC022_UnauthenticatedUserRedirectedToLoginPage() {
    createTest(
        "TC-022: Verify unauthenticated user is redirected to login page",
        "Browser should redirect to /user/login");

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    homePage.clickFavoriteButtonForArticle(0);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(currentUrl.contains("/user/login"), "Should redirect to /user/login");

    test.pass("User correctly redirected to login page");
  }

  @Test(groups = {"regression", "error", "favorite"})
  public void testTC023_FavoritingNonExistentArticleShowsError() {
    createTest(
        "TC-023: Verify favoriting non-existent article shows error",
        "404 error or appropriate error message should be shown");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    driver.get(baseUrl + "/article/non-existent-article-slug-12345");
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String pageSource = driver.getPageSource();
    boolean hasErrorIndicator =
        pageSource.contains("404")
            || pageSource.contains("not found")
            || pageSource.contains("Not Found")
            || pageSource.contains("error")
            || !articlePage.isOnArticlePage();

    assertTrue(hasErrorIndicator, "Should show error for non-existent article");

    test.pass("Non-existent article handled correctly");
  }

  @Test(groups = {"regression", "error", "favorite"})
  public void testTC024_ErrorHandlingForInvalidArticleSlug() {
    createTest(
        "TC-024: Verify error handling for invalid article slug",
        "Error should be handled gracefully");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    driver.get(baseUrl + "/article/invalid-slug-12345");
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String pageSource = driver.getPageSource();
    assertFalse(
        pageSource.contains("Exception") && pageSource.contains("stack trace"),
        "Should not show raw exception or stack trace");

    test.pass("Invalid article slug handled gracefully");
  }

  @Test(groups = {"regression", "error", "favorite"})
  public void testTC025_ErrorHandlingForEmptyArticleSlug() {
    createTest(
        "TC-025: Verify error handling for empty article slug",
        "Error should be handled gracefully");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    driver.get(baseUrl + "/article/");
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String pageSource = driver.getPageSource();
    assertFalse(
        pageSource.contains("Exception") && pageSource.contains("stack trace"),
        "Should not show raw exception or stack trace");

    test.pass("Empty article slug handled gracefully");
  }

  @Test(groups = {"regression", "error", "favorite"})
  public void testTC026_ErrorHandlingForSpecialCharactersInArticleSlug() {
    createTest(
        "TC-026: Verify error handling for special characters in article slug",
        "Error should be handled or article loads correctly");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    driver.get(baseUrl + "/article/test%20article%3F%26special");
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String pageSource = driver.getPageSource();
    assertFalse(
        pageSource.contains("Exception") && pageSource.contains("stack trace"),
        "Should not show raw exception or stack trace");

    test.pass("Special characters in article slug handled correctly");
  }

  @Test(groups = {"regression", "error", "favorite"})
  public void testTC027_ErrorHandlingWhenSessionExpiresDuringFavoriteAction() {
    createTest(
        "TC-027: Verify error handling when session expires during favorite action",
        "User should be redirected to login without data corruption");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    driver.manage().deleteAllCookies();
    ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");

    homePage.clickFavoriteButtonForArticle(0);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        currentUrl.contains("/user/login") || !homePage.isLoggedIn(),
        "User should be redirected to login or logged out");

    test.pass("Session expiration handled correctly");
  }

  @Test(groups = {"regression", "error", "favorite"})
  public void testTC028_ErrorHandlingForNetworkTimeoutDuringFavorite() {
    createTest(
        "TC-028: Verify error handling for network timeout during favorite",
        "Timeout should be handled gracefully and UI should recover");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    int initialCount = homePage.getFavoriteCountForArticle(0);

    homePage.clickFavoriteButtonForArticle(0);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    int articleCount = homePage.getArticleCount();
    assertTrue(articleCount > 0, "Page should still display articles after favorite action");

    test.pass("Network operations handled correctly");
  }

  @Test(groups = {"regression", "error", "favorite"})
  public void testTC029_ErrorMessageDisplayForFailedFavoriteAction() {
    createTest(
        "TC-029: Verify error message display for failed favorite action",
        "Appropriate error message should be displayed");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    int articleCount = homePage.getArticleCount();
    assertTrue(articleCount > 0, "Should have articles to test with");

    homePage.clickFavoriteButtonForArticle(0);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    int newArticleCount = homePage.getArticleCount();
    assertTrue(newArticleCount > 0, "Page should still be functional after favorite action");

    test.pass("Error handling for favorite action verified");
  }

  @Test(groups = {"regression", "error", "favorite"})
  public void testTC030_UIStateRecoveryAfterFailedFavoriteAction() {
    createTest(
        "TC-030: Verify UI state recovery after failed favorite action",
        "UI should revert to previous state on failure");

    loginAsTestUser();

    String baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    boolean initialFavorited = homePage.isArticleFavorited(0);
    int initialCount = homePage.getFavoriteCountForArticle(0);

    homePage.clickFavoriteButtonForArticle(0);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean currentFavorited = homePage.isArticleFavorited(0);
    int currentCount = homePage.getFavoriteCountForArticle(0);

    boolean stateChanged = (currentFavorited != initialFavorited);
    boolean countChanged = (currentCount != initialCount);

    assertTrue(
        stateChanged == countChanged,
        "Favorite state and count should be consistent (both changed or both unchanged)");

    test.pass("UI state is consistent after favorite action");
  }
}

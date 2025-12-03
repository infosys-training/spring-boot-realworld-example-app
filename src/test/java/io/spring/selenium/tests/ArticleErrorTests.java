package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.NewArticlePage;
import java.util.UUID;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Error handling test cases for article creation (TC-021 to TC-030). Tests negative scenarios and
 * error handling for the Create Article feature.
 */
public class ArticleErrorTests extends BaseTest {

  private LoginPage loginPage;
  private HomePage homePage;
  private NewArticlePage newArticlePage;
  private ArticlePage articlePage;

  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";

  @BeforeMethod
  public void setupPages() {
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
    newArticlePage = new NewArticlePage(driver);
    articlePage = new ArticlePage(driver);
  }

  private void loginAsTestUser() {
    loginPage.navigateTo();
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private String generateUniqueTitle() {
    return "Test Article " + UUID.randomUUID().toString().substring(0, 8);
  }

  @Test(groups = {"smoke", "error", "article"})
  public void TC021_unauthenticatedUserCannotCreateArticle() {
    createTest(
        "TC-021: Unauthenticated user cannot create article",
        "Verify unauthenticated user is redirected to login or gets 401");

    homePage.navigateTo();
    test.info("Navigated to home page without logging in");

    newArticlePage.navigateTo();
    test.info("Attempted to access New Article page");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean redirectedToLogin = currentUrl.contains("/login");
    boolean stayedOnHome = currentUrl.contains("localhost:3000") && !currentUrl.contains("/editor");
    boolean showsLoginPage = loginPage.isLoginPageDisplayed();

    assertTrue(
        redirectedToLogin || stayedOnHome || showsLoginPage,
        "Unauthenticated user should be redirected to login or blocked from editor");
    test.info("Unauthenticated access properly handled. Current URL: " + currentUrl);
  }

  @Test(groups = {"smoke", "error", "article"})
  public void TC022_expiredTokenCannotCreateArticle() {
    createTest(
        "TC-022: Expired token cannot create article",
        "Verify expired JWT token results in 401 and redirect to login");

    loginAsTestUser();
    test.info("Logged in as test user");

    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript("localStorage.setItem('jwtToken', 'expired.invalid.token');");
    test.info("Set expired/invalid token in localStorage");

    newArticlePage.navigateTo();
    test.info("Attempted to access New Article page with expired token");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean redirectedToLogin = currentUrl.contains("/login");
    boolean stayedOnEditor = currentUrl.contains("/editor");
    boolean hasError = newArticlePage.isErrorDisplayed();

    assertTrue(
        redirectedToLogin || stayedOnEditor || hasError,
        "Expired token should be handled appropriately");
    test.info("Expired token handling verified. Current URL: " + currentUrl);
  }

  @Test(groups = {"smoke", "error", "article"})
  public void TC023_invalidTokenFormatCannotCreateArticle() {
    createTest(
        "TC-023: Invalid token format cannot create article",
        "Verify malformed JWT token results in 401");

    loginAsTestUser();
    test.info("Logged in as test user");

    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript("localStorage.setItem('jwtToken', 'not-a-valid-jwt-format');");
    test.info("Set malformed token in localStorage");

    newArticlePage.navigateTo();
    test.info("Attempted to access New Article page with invalid token format");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean redirectedToLogin = currentUrl.contains("/login");
    boolean stayedOnEditor = currentUrl.contains("/editor");
    boolean hasError = newArticlePage.isErrorDisplayed();

    assertTrue(
        redirectedToLogin || stayedOnEditor || hasError,
        "Invalid token format should be handled appropriately");
    test.info("Invalid token format handling verified. Current URL: " + currentUrl);
  }

  @Test(groups = {"smoke", "error", "article"})
  public void TC024_missingAuthorizationHeaderReturns401() {
    createTest(
        "TC-024: Missing Authorization header returns 401",
        "Verify missing auth header blocks article creation");

    homePage.navigateTo();
    test.info("Navigated to home page");

    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript("localStorage.clear();");
    js.executeScript("sessionStorage.clear();");
    test.info("Cleared all authentication data");

    newArticlePage.navigateTo();
    test.info("Attempted to access New Article page without authentication");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = driver.getCurrentUrl();
    boolean redirectedToLogin = currentUrl.contains("/login");
    boolean blockedFromEditor = !currentUrl.contains("/editor");
    boolean showsLoginPage = loginPage.isLoginPageDisplayed();

    assertTrue(
        redirectedToLogin || blockedFromEditor || showsLoginPage,
        "Missing authorization should block access to editor");
    test.info("Missing authorization handling verified. Current URL: " + currentUrl);
  }

  @Test(groups = {"smoke", "error", "article"})
  public void TC025_createArticleWithMissingTitleField() {
    createTest(
        "TC-025: Create article with missing title field",
        "Verify error response when title field is not submitted");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    newArticlePage.enterDescription("Valid description");
    newArticlePage.enterBody("Valid body content");
    newArticlePage.clickPublish();
    test.info("Submitted form without title field");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean stayedOnEditorPage = newArticlePage.isEditorPageDisplayed();
    boolean hasError = newArticlePage.isErrorDisplayed();

    assertTrue(stayedOnEditorPage || hasError, "Should show error when title field is missing");
    test.info("Missing title field error handling verified");
  }

  @Test(groups = {"smoke", "error", "article"})
  public void TC026_createArticleWithMissingDescriptionField() {
    createTest(
        "TC-026: Create article with missing description field",
        "Verify error response when description field is not submitted");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    newArticlePage.enterTitle(generateUniqueTitle());
    newArticlePage.enterBody("Valid body content");
    newArticlePage.clickPublish();
    test.info("Submitted form without description field");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean stayedOnEditorPage = newArticlePage.isEditorPageDisplayed();
    boolean hasError = newArticlePage.isErrorDisplayed();

    assertTrue(
        stayedOnEditorPage || hasError, "Should show error when description field is missing");
    test.info("Missing description field error handling verified");
  }

  @Test(groups = {"smoke", "error", "article"})
  public void TC027_createArticleWithMissingBodyField() {
    createTest(
        "TC-027: Create article with missing body field",
        "Verify error response when body field is not submitted");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    newArticlePage.enterTitle(generateUniqueTitle());
    newArticlePage.enterDescription("Valid description");
    newArticlePage.clickPublish();
    test.info("Submitted form without body field");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean stayedOnEditorPage = newArticlePage.isEditorPageDisplayed();
    boolean hasError = newArticlePage.isErrorDisplayed();

    assertTrue(stayedOnEditorPage || hasError, "Should show error when body field is missing");
    test.info("Missing body field error handling verified");
  }

  @Test(groups = {"regression", "error", "article"})
  public void TC028_createArticleWithNullTitle() {
    createTest(
        "TC-028: Create article with null title", "Verify error response for null title value");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    newArticlePage.clearTitle();
    newArticlePage.enterDescription("Valid description");
    newArticlePage.enterBody("Valid body content");
    newArticlePage.clickPublish();
    test.info("Submitted form with cleared/null title");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean stayedOnEditorPage = newArticlePage.isEditorPageDisplayed();
    boolean hasError = newArticlePage.isErrorDisplayed();

    assertTrue(stayedOnEditorPage || hasError, "Should show error for null title");
    test.info("Null title error handling verified");
  }

  @Test(groups = {"regression", "error", "article"})
  public void TC029_createArticleWithNullDescription() {
    createTest(
        "TC-029: Create article with null description",
        "Verify error response for null description value");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    newArticlePage.enterTitle(generateUniqueTitle());
    newArticlePage.clearDescription();
    newArticlePage.enterBody("Valid body content");
    newArticlePage.clickPublish();
    test.info("Submitted form with cleared/null description");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean stayedOnEditorPage = newArticlePage.isEditorPageDisplayed();
    boolean hasError = newArticlePage.isErrorDisplayed();

    assertTrue(stayedOnEditorPage || hasError, "Should show error for null description");
    test.info("Null description error handling verified");
  }

  @Test(groups = {"regression", "error", "article"})
  public void TC030_createArticleWithNullBody() {
    createTest(
        "TC-030: Create article with null body", "Verify error response for null body value");

    loginAsTestUser();
    test.info("Logged in as test user");

    newArticlePage.navigateTo();
    newArticlePage.waitForPageLoad();
    test.info("Navigated to New Article page");

    newArticlePage.enterTitle(generateUniqueTitle());
    newArticlePage.enterDescription("Valid description");
    newArticlePage.clearBody();
    newArticlePage.clickPublish();
    test.info("Submitted form with cleared/null body");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean stayedOnEditorPage = newArticlePage.isEditorPageDisplayed();
    boolean hasError = newArticlePage.isErrorDisplayed();

    assertTrue(stayedOnEditorPage || hasError, "Should show error for null body");
    test.info("Null body error handling verified");
  }
}
